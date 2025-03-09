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

package org.activiti.engine.impl.cfg;

import java.io.InputStream;

import org.activiti.engine.ProcessEngineConfiguration;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;


/**
 * @author Tom Baeyens
 */
public class BeansConfigurationHelper {

  /**
   * 实际执行
   * @param springResource
   * @param beanName
   * @return
   */
  public static ProcessEngineConfiguration parseProcessEngineConfiguration(Resource springResource, String beanName) {
    /**
     * 配置文件转换为 Spring 将配置项目作为 Spring 的普通 bean 进行初始化和使用
     * 这是一种便捷的方式，Spring 作为当前 Java 系统的基建性组件不依赖是不现实的，依赖的深度如果做好隔离
     * 就可以使用，减少诸如 @Resource、Autowired 的使用，更加灵活地组织代码
     * 在 Activiti 中，只有在 Rest 层使用这些内容
     * 这是一个值得学习的技巧，比如在容器实现中，可以使用这种方式，将 BeanFactory 导入，而不需要默认导入实现类的
     */
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
    xmlBeanDefinitionReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
    xmlBeanDefinitionReader.loadBeanDefinitions(springResource);
    // 拿到 ProcessEngineConfigurationImpl 调用了默认构造函数
    ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) beanFactory.getBean(beanName);
    processEngineConfiguration.setBeans(new SpringBeanFactoryProxyMap(beanFactory));
    return processEngineConfiguration;
  }

  public static ProcessEngineConfiguration parseProcessEngineConfigurationFromInputStream(InputStream inputStream, String beanName) {
    Resource springResource = new InputStreamResource(inputStream);
    return parseProcessEngineConfiguration(springResource, beanName);
  }

  public static ProcessEngineConfiguration parseProcessEngineConfigurationFromResource(String resource, String beanName) {
    Resource springResource = new ClassPathResource(resource);
    return parseProcessEngineConfiguration(springResource, beanName);
  }

}
