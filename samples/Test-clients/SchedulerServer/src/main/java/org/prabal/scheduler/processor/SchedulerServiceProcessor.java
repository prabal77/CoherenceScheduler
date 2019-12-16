/**
 * 
 */
package org.prabal.scheduler.processor;

import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.builder.SchedulerServiceBuilder;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.run.xml.XmlElement;

/**
 * @author Prabal Nandi
 *
 */
@XmlSimpleName(SchedulerNamespaceHandler.SCHEDULER_SERVICE)
public class SchedulerServiceProcessor implements ElementProcessor<SchedulerServiceConfig> {

   @Override
   public SchedulerServiceConfig process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
      SchedulerServiceBuilder schedulerServiceBuilder = new SchedulerServiceBuilder();
      context.inject(schedulerServiceBuilder, xmlElement);
      SchedulerServiceConfig schedulerServiceConfig = schedulerServiceBuilder.realize();
      schedulerServiceConfig.setSingleServerMode((xmlElement.getElement("scheduler:"+SchedulerNamespaceHandler.SCHEDULER_SINGLE_SERVER_MODE) != null));
      context.getResourceRegistry().registerResource(SchedulerServiceConfig.class, SchedulerNamespaceHandler.RESOURCE_SCHEDULER_CONFIG,
            schedulerServiceConfig);
      return schedulerServiceConfig;
   }
}
