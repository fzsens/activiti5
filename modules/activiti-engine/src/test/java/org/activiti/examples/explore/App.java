package org.activiti.examples.explore;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;

public class App {

    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService().createDeployment().addClasspathResource("org/activiti/examples/explore/explore-demo.bpmn20.xml").deploy();
        processEngine.getRuntimeService().startProcessInstanceByKey("hello");
    }
}
