package org.activiti.examples.explore;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.junit.Assert;
import org.junit.Test;

public class ProcessorEngineTest {

    @Test
    public void getDefaultProcessEngine() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Assert.assertNotNull("not null", taskService);
        engine.close();
    }

}
