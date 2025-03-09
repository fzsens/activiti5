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
package org.activiti.engine.cfg;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;


/**
 * Implementations of this class can be plugged into a {@link ProcessEngineConfigurationImpl}.
 * Such implementations can configure the engine in any way programmatically possible.
 *
 * 是 ProcessEngine 的配置可扩展插件，通过配置 Configurator 可以在 ProcessEngine 初始化的前后对 ProcessEngineConfiguration 的配置项
 * 进行干预，典型的增加 JMX 暴露，比如修改 jdbc 的地址、增加 ldap 支持单点登录
 * 
 * @author Joram Barrez
 */
public interface ProcessEngineConfigurator {
	
	/**
	 * Called <b>before</b> any initialisation has been done.
	 * This can for example be useful to change configuration settings
	 * before anything that uses those properties is created.
	 * 
	 * Allows to tweak the process engine by passing the {@link ProcessEngineConfigurationImpl}
   * which allows tweaking it programmatically.
	 * 
	 * An example is the jdbc url. When a {@link ProcessEngineConfigurator} instance
	 * wants to change it, it needs to do it in this method, or otherwise
	 * the datasource would already have been created with the 'old' value
	 * for the jdbc url.
	 */
	void beforeInit(ProcessEngineConfigurationImpl processEngineConfiguration);
  
  /**
   * Called when the engine boots up, before it is usable, but after
   * the initialisation of internal objects is done.
   *  
   * Allows to tweak the process engine by passing the {@link ProcessEngineConfigurationImpl}
   * which allows tweaking it programmatically.
   * 
   * An example is the ldap user/group manager, which is an addition to the engine.
   * No default properties need to be overridden for this (otherwise the {@link #beforeInit(ProcessEngineConfigurationImpl)} 
   * method should be used) so the logic contained in this method is executed
   * after initialisation of the default objects.
   * 
   * Probably a better name would be 'afterInit' (cfr {@link #beforeInit(ProcessEngineConfigurationImpl)}),
   * but not possible due to backwards compatibility.
   */
  void configure(ProcessEngineConfigurationImpl processEngineConfiguration);
  
  /**
   * When the {@link ProcessEngineConfigurator} instances are used, they are first
   * ordered by this priority number (lowest to highest).
   * If you have dependencies between {@link ProcessEngineConfigurator}
   * instances, use the priorities accordingly to order them as needed.
   */
  int getPriority();

}
