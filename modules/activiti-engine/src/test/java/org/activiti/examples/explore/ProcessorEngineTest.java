package org.activiti.examples.explore;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.io.InputStreamSource;
import org.activiti.engine.impl.util.io.StreamSource;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class ProcessorEngineTest {

    @Test
    public void getDefaultProcessEngine() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Assert.assertNotNull("not null", taskService);
        engine.close();
    }

    @Test
    public void testDeploy() throws Exception {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
                String resource = "org/activiti/examples/explore/explore-demo.bpmn20.xml";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
        BpmnXMLConverter bpmnXMLConverter =  new BpmnXMLConverter();
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(new InputStreamSource(inputStream), true,false);
        repositoryService.createDeployment().addBpmnModel("explore-demo.bpmn20.xml",bpmnModel).deploy();
    }

    @Test
    public void testConvertToBpmnModel() throws Exception {
        String resource = "org/activiti/examples/explore/explore-demo.bpmn20.xml";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
        BpmnXMLConverter bpmnXMLConverter =  new BpmnXMLConverter();
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(new InputStreamSource(inputStream), true,false);
        String xml = new String(bpmnXMLConverter.convertToXML(bpmnModel));
        System.out.println(xml);
    }

    @Test
    public void convertToBpmnModel() {
        InputStream xml = this.getClass().getClassLoader().getResourceAsStream("org/activiti/examples/explore/explore-demo2.bpmn20.xml");
        StreamSource xmlSource = new InputStreamSource(xml);
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = converter.convertToBpmnModel(xmlSource,false,false);
        List<Process> processes = bpmnModel.getProcesses();

    }

}
