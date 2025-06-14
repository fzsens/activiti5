package org.activiti.engine.impl.event.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.event.logger.handler.ActivityCompensatedEventHandler;
import org.activiti.engine.impl.event.logger.handler.ActivityCompletedEventHandler;
import org.activiti.engine.impl.event.logger.handler.ActivityErrorReceivedEventHandler;
import org.activiti.engine.impl.event.logger.handler.ActivityMessageEventHandler;
import org.activiti.engine.impl.event.logger.handler.ActivitySignaledEventHandler;
import org.activiti.engine.impl.event.logger.handler.ActivityStartedEventHandler;
import org.activiti.engine.impl.event.logger.handler.EventLoggerEventHandler;
import org.activiti.engine.impl.event.logger.handler.ProcessInstanceEndedEventHandler;
import org.activiti.engine.impl.event.logger.handler.ProcessInstanceStartedEventHandler;
import org.activiti.engine.impl.event.logger.handler.SequenceFlowTakenEventHandler;
import org.activiti.engine.impl.event.logger.handler.TaskAssignedEventHandler;
import org.activiti.engine.impl.event.logger.handler.TaskCompletedEventHandler;
import org.activiti.engine.impl.event.logger.handler.TaskCreatedEventHandler;
import org.activiti.engine.impl.event.logger.handler.VariableCreatedEventHandler;
import org.activiti.engine.impl.event.logger.handler.VariableDeletedEventHandler;
import org.activiti.engine.impl.event.logger.handler.VariableUpdatedEventHandler;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandContextCloseListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 默认添加，用于进行事件的监听和日志记录（数据库记录）
 *
 * 1. 每个 handler 产生对应的事件记录 entry
 * 2. 最后通过同意的 database flusher 写入数据库，这是一个有效的优化方法
 *
 * @author Joram Barrez
 */
public class EventLogger implements ActivitiEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	private static final String EVENT_FLUSHER_KEY = "eventFlusher";
	
	protected Clock clock;
	protected ObjectMapper objectMapper;
	
	// Mapping of type -> handler
	protected Map<ActivitiEventType, Class<? extends EventLoggerEventHandler>> eventHandlers 
		= new HashMap<ActivitiEventType, Class<? extends EventLoggerEventHandler>>();
	
	// Listeners for new events
	protected List<EventLoggerListener> listeners;
	
	public EventLogger() {
		initializeDefaultHandlers();
	}
	
	public EventLogger(Clock clock, ObjectMapper objectMapper) {
		this();
		this.clock = clock;
		this.objectMapper = objectMapper;
	}

	protected void initializeDefaultHandlers() {
	    addEventHandler(ActivitiEventType.TASK_CREATED, TaskCreatedEventHandler.class);
		addEventHandler(ActivitiEventType.TASK_COMPLETED, TaskCompletedEventHandler.class);
		addEventHandler(ActivitiEventType.TASK_ASSIGNED, TaskAssignedEventHandler.class);
		
		addEventHandler(ActivitiEventType.SEQUENCEFLOW_TAKEN, SequenceFlowTakenEventHandler.class);
		
		addEventHandler(ActivitiEventType.ACTIVITY_COMPLETED, ActivityCompletedEventHandler.class);
		addEventHandler(ActivitiEventType.ACTIVITY_STARTED, ActivityStartedEventHandler.class);
		addEventHandler(ActivitiEventType.ACTIVITY_SIGNALED, ActivitySignaledEventHandler.class);
		addEventHandler(ActivitiEventType.ACTIVITY_MESSAGE_RECEIVED, ActivityMessageEventHandler.class);
		addEventHandler(ActivitiEventType.ACTIVITY_COMPENSATE, ActivityCompensatedEventHandler.class);
		addEventHandler(ActivitiEventType.ACTIVITY_ERROR_RECEIVED, ActivityErrorReceivedEventHandler.class);
		
		addEventHandler(ActivitiEventType.VARIABLE_CREATED, VariableCreatedEventHandler.class);
		addEventHandler(ActivitiEventType.VARIABLE_DELETED, VariableDeletedEventHandler.class);
		addEventHandler(ActivitiEventType.VARIABLE_UPDATED, VariableUpdatedEventHandler.class);
  }
	
	@Override
	public void onEvent(ActivitiEvent event) {
		EventLoggerEventHandler eventHandler = getEventHandler(event);
		if (eventHandler != null) {

			// Events are flushed when command context is closed
			CommandContext currentCommandContext = Context.getCommandContext();
			EventFlusher eventFlusher = (EventFlusher) currentCommandContext.getAttribute(EVENT_FLUSHER_KEY);
			
			if (eventHandler != null && eventFlusher == null) {

				/**
				 * flusher 不符合对扩展开放的原则
				 */
				eventFlusher = createEventFlusher();
				if (eventFlusher == null) {
					eventFlusher = new DatabaseEventFlusher(); // Default
				}
				currentCommandContext.addAttribute(EVENT_FLUSHER_KEY, eventFlusher);
				/*
				 * 上下文关闭的时候，也需要进行写入
				 */
				currentCommandContext.addCloseListener(eventFlusher);
				currentCommandContext
				    .addCloseListener(new CommandContextCloseListener() {

					    @Override
					    public void closing(CommandContext commandContext) {
					    }

						/**
						 * Activiti 给予 CQRS 模式构建，当 context 的命令执行完毕之后，会调用 context 的 closing- closed 方法
						 */
					    @Override
					    public void closed(CommandContext commandContext) {
						    // For those who are interested: we can now broadcast the events were added
								if (listeners != null) {
									for (EventLoggerListener listener : listeners) {
										listener.eventsAdded(EventLogger.this);
									}
								}
					    }
					    
				    });
			}
			/**
			 * 存储到 flusher 中，这边的 eventHandler，并非一般事件处理器的无状态模式
			 * 而是采用了一个 eventHandler 对应一个 event 的有状态模式，有状态模式的好处是什么呢？
			 * 如果要借鉴这种模式，应该在什么场景下？
			 */
			eventFlusher.addEventHandler(eventHandler);
		}
	}
	
	// Subclasses can override this if defaults are not ok
	protected EventLoggerEventHandler getEventHandler(ActivitiEvent event) {

		Class<? extends EventLoggerEventHandler> eventHandlerClass = null;
		if (event.getType().equals(ActivitiEventType.ENTITY_INITIALIZED)) {
			Object entity = ((ActivitiEntityEvent) event).getEntity();
			if (entity instanceof ExecutionEntity) {
				ExecutionEntity executionEntity = (ExecutionEntity) entity;
				if (executionEntity.getProcessInstanceId().equals(executionEntity.getId())) {
					eventHandlerClass = ProcessInstanceStartedEventHandler.class;
				}
			}
		} else if (event.getType().equals(ActivitiEventType.ENTITY_DELETED)) {
			Object entity = ((ActivitiEntityEvent) event).getEntity();
			if (entity instanceof ExecutionEntity) {
				ExecutionEntity executionEntity = (ExecutionEntity) entity;
				if (executionEntity.getProcessInstanceId().equals(executionEntity.getId())) {
					eventHandlerClass = ProcessInstanceEndedEventHandler.class;
				}
			}
		} else {
			// Default: dedicated mapper for the type
			eventHandlerClass = eventHandlers.get(event.getType());
		}
		
		if (eventHandlerClass != null) {
			return instantiateEventHandler(event, eventHandlerClass);
		}
		
		return null;
	}

	protected EventLoggerEventHandler instantiateEventHandler(ActivitiEvent event,
      Class<? extends EventLoggerEventHandler> eventHandlerClass) {
		try {
			EventLoggerEventHandler eventHandler = eventHandlerClass.newInstance();
			eventHandler.setTimeStamp(clock.getCurrentTime());
			eventHandler.setEvent(event);
			eventHandler.setObjectMapper(objectMapper);
			return eventHandler;
		} catch (Exception e) {
			logger.warn("Could not instantiate " + eventHandlerClass + ", this is most likely a programmatic error");
		}
		return null;
  }
	
	@Override
  public boolean isFailOnException() {
		return false;
  }
	
	public void addEventHandler(ActivitiEventType eventType, Class<? extends EventLoggerEventHandler> eventHandlerClass) {
		eventHandlers.put(eventType, eventHandlerClass);
	}
	
	public void addEventLoggerListener(EventLoggerListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<EventLoggerListener>(1);
		}
		listeners.add(listener);
	}
	
	/**
	 * Subclasses that want something else than the database flusher should override this method
	 */
	protected EventFlusher createEventFlusher() {
		return null;
	}

	public Clock getClock() {
		return clock;
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public List<EventLoggerListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<EventLoggerListener> listeners) {
		this.listeners = listeners;
	}
	
}
