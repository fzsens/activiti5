package org.activiti.engine.impl.event.logger.handler;

import java.util.Date;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.EventLogEntryEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Joram Barrez
 */
public interface EventLoggerEventHandler {

	/**
	 * 日志系列的 EventHandler 核心方法是长生 EventLogEntryEntity
	 */
	EventLogEntryEntity generateEventLogEntry(CommandContext commandContext);
	
	void setEvent(ActivitiEvent event);
	
	void setTimeStamp(Date timeStamp);
	
	void setObjectMapper(ObjectMapper objectMapper);
	
}
