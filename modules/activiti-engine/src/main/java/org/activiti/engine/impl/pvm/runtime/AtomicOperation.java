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
package org.activiti.engine.impl.pvm.runtime;



/**
 * 定义操作的动作
 * 1. 可以参考用在业务代码的分节点和分操作处理上
 *  比如，将一个业务商品发布分成（ContextBuildOperation、CheckOperation、PreSubmitOperation、SubmitOperation、PostSubmitOperation）
 * 2. 通过对 Operation 的自定义管理来实现复杂业务的控制
 * @author Tom Baeyens
 * @author Daniel Meyer
 */
public interface AtomicOperation {
  
  AtomicOperation PROCESS_START = new AtomicOperationProcessStart();
  AtomicOperation PROCESS_START_INITIAL = new AtomicOperationProcessStartInitial();
  AtomicOperation PROCESS_END = new AtomicOperationProcessEnd();
  AtomicOperation ACTIVITY_START = new AtomicOperationActivityStart();
  AtomicOperation ACTIVITY_EXECUTE = new AtomicOperationActivityExecute();
  AtomicOperation ACTIVITY_END = new AtomicOperationActivityEnd();
  AtomicOperation TRANSITION_NOTIFY_LISTENER_END = new AtomicOperationTransitionNotifyListenerEnd();
  AtomicOperation TRANSITION_DESTROY_SCOPE = new AtomicOperationTransitionDestroyScope();
  AtomicOperation TRANSITION_NOTIFY_LISTENER_TAKE = new AtomicOperationTransitionNotifyListenerTake();
  AtomicOperation TRANSITION_CREATE_SCOPE = new AtomicOperationTransitionCreateScope();
  AtomicOperation TRANSITION_NOTIFY_LISTENER_START = new AtomicOperationTransitionNotifyListenerStart();

  AtomicOperation DELETE_CASCADE = new AtomicOperationDeleteCascade();
  AtomicOperation DELETE_CASCADE_FIRE_ACTIVITY_END = new AtomicOperationDeleteCascadeFireActivityEnd();

  void execute(InterpretableExecution execution);
  
  boolean isAsync(InterpretableExecution execution);
}
