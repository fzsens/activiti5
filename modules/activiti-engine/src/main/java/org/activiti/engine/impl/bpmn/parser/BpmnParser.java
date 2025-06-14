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
package org.activiti.engine.impl.bpmn.parser;

import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.bpmn.parser.factory.ListenerFactory;
import org.activiti.engine.impl.cfg.BpmnParseFactory;
import org.activiti.engine.impl.el.ExpressionManager;


/**
 * Parser for BPMN 2.0 process models.
 * 
 * There is only one instance of this parser in the process engine.
 * This {@link Parser} creates {@link BpmnParse} instances that 
 * can be used to actually parse the BPMN 2.0 XML process definitions.
 *
 * 思考为什么需要这个近似于工厂类的实现呢？
 * 1. 核心的作用是创建 BpmnParse
 * 2. BpmnParser 被创建的位置是在 ProcessEngineConfiguration 中
 * 3. 具体创建 BpmnParse 使用了一个工厂类 BpmnParseFactory，并且可以作为外部参数传递到 ProcessEngineConfiguration 中
 * 4. 这样设计的好处，在于可扩展性，用户可以通过 BpmnParseFactory 来进行扩展，并且默认带有一系列的参数，这些参数被封装在BpmnParser中
 *
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class BpmnParser {
  
  /**
   * The namepace of the BPMN 2.0 diagram interchange elements.
   */
  public static final String BPMN_DI_NS = "http://www.omg.org/spec/BPMN/20100524/DI";
  
  /**
   * The namespace of the BPMN 2.0 diagram common elements.
   */
  public static final String BPMN_DC_NS = "http://www.omg.org/spec/DD/20100524/DC";
  
  /**
   * The namespace of the generic OMG DI elements (don't ask me why they didnt use the BPMN_DI_NS ...)
   */
  public static final String OMG_DI_NS = "http://www.omg.org/spec/DD/20100524/DI";

  protected ExpressionManager expressionManager;
  protected ActivityBehaviorFactory activityBehaviorFactory;
  protected ListenerFactory listenerFactory;
  protected BpmnParseFactory bpmnParseFactory;
  protected BpmnParseHandlers bpmnParserHandlers;
  
  /**
   * Creates a new {@link BpmnParse} instance that can be used
   * to parse only one BPMN 2.0 process definition.
   */
  public BpmnParse createParse() {
    return bpmnParseFactory.createBpmnParse(this);
  }
  
  public ActivityBehaviorFactory getActivityBehaviorFactory() {
    return activityBehaviorFactory;
  }
  
  public void setActivityBehaviorFactory(ActivityBehaviorFactory activityBehaviorFactory) {
    this.activityBehaviorFactory = activityBehaviorFactory;
  }
  
  public ListenerFactory getListenerFactory() {
    return listenerFactory;
  }

  public void setListenerFactory(ListenerFactory listenerFactory) {
    this.listenerFactory = listenerFactory;
  }
  
  public BpmnParseFactory getBpmnParseFactory() {
    return bpmnParseFactory;
  }
  
  public void setBpmnParseFactory(BpmnParseFactory bpmnParseFactory) {
    this.bpmnParseFactory = bpmnParseFactory;
  }

  public ExpressionManager getExpressionManager() {
    return expressionManager;
  }

  public void setExpressionManager(ExpressionManager expressionManager) {
    this.expressionManager = expressionManager;
  }

  public BpmnParseHandlers getBpmnParserHandlers() {
    return bpmnParserHandlers;
  }

  public void setBpmnParserHandlers(BpmnParseHandlers bpmnParserHandlers) {
    this.bpmnParserHandlers = bpmnParserHandlers;
  }
}
