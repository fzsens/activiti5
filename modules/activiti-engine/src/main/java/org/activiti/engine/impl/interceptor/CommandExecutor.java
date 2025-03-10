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

/**
 *
 * Activiti 核心执行器
 * Activiti 采用 CQRS 架构，所有非查询逻辑都是通过 CommandExecutor 来实现的
 * 内部通过给予 CommandInterceptor 的拦截器来实现
 *
 * The command executor for internal usage.
 * 
 * @author Tom Baeyens
 */
public interface CommandExecutor {
  
  /**
   * @return the default {@link CommandConfig}, used if none is provided.
   */
  CommandConfig getDefaultConfig();

  /**
   * Execute a command with the specified {@link CommandConfig}.
   */
  <T> T execute(CommandConfig config, Command<T> command);

  /**
   * Execute a command with the default {@link CommandConfig}.
   */
  <T> T execute(Command<T> command);
  
}
