/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.engine.impl.interceptor;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在执行 Command 时候的上下文构建、移除
 * {
 *     创建、复用 Context
 *     调用 Command 执行
 *     移除 Context
 * }
 * @author Tom Baeyens
 */
public class CommandContextInterceptor extends AbstractCommandInterceptor {
  private static final Logger log = LoggerFactory.getLogger(CommandContextInterceptor.class);

  protected CommandContextFactory commandContextFactory;
  protected ProcessEngineConfigurationImpl processEngineConfiguration;

  public CommandContextInterceptor() {
  }

  public CommandContextInterceptor(CommandContextFactory commandContextFactory, ProcessEngineConfigurationImpl processEngineConfiguration) {
    this.commandContextFactory = commandContextFactory;
    this.processEngineConfiguration = processEngineConfiguration;
  }

  public <T> T execute(CommandConfig config, Command<T> command) {
    CommandContext context = Context.getCommandContext();
    
    boolean contextReused = false;
    // We need to check the exception, because the transaction can be in a rollback state,
    // and some other command is being fired to compensate (eg. decrementing job retries)
    if (!config.isContextReusePossible() || context == null || context.getException() != null) {
        /**
         * 从工厂中读取
         */
    	context = commandContextFactory.createCommandContext(command);    	
    }  
    else {
    	log.debug("Valid context found. Reusing it for the current command '{}'", command.getClass().getCanonicalName());
    	contextReused = true;
    }

    try {
      // Push on stack
      // 在 Context 中按照线程、分别常见了对应的栈，用于存储 context
      Context.setCommandContext(context);
      Context.setProcessEngineConfiguration(processEngineConfiguration);
      
      return next.execute(config, command);
      
    } catch (Exception e) {
    	
      context.exception(e);
      
    } finally {
      try {
    	  if (!contextReused) {
    		  context.close();
    	  }
      } finally {
    	  // Pop from stack
          // 这里移除之后，就无法从 Context 中得到 context 了
    	  Context.removeCommandContext();
    	  Context.removeProcessEngineConfiguration();
    	  Context.removeBpmnOverrideContext();
      }
    }
    
    return null;
  }
  
  public CommandContextFactory getCommandContextFactory() {
    return commandContextFactory;
  }
  
  public void setCommandContextFactory(CommandContextFactory commandContextFactory) {
    this.commandContextFactory = commandContextFactory;
  }

  public ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
    return processEngineConfiguration;
  }

  public void setProcessEngineContext(ProcessEngineConfigurationImpl processEngineContext) {
    this.processEngineConfiguration = processEngineContext;
  }
}
