
/**
 * 
 */
package org.prabal.scheduler.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.StartupTriggerProcessor.CronTriggerConfig;
import org.prabal.scheduler.processor.StartupTriggerProcessor.SimpleTriggerConfig;
import org.prabal.scheduler.processor.config.StartupTriggerConfig;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.Builder;

/**
 * @author Prabal Nandi
 *
 */
@XmlSimpleName(SchedulerNamespaceHandler.JOB_TRIGGER_LIST)
public class StartupTriggerListProcessor implements ElementProcessor<List<StartupTriggerConfig>> {

   @SuppressWarnings("unchecked")
   @Override
   public List<StartupTriggerConfig> process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
      StartupTriggerListBuilder startupTriggerListBuilder = new StartupTriggerListBuilder();
      Map<String, StartupTriggerConfig> returnMap = (Map<String, StartupTriggerConfig>) context.processElementsOf(xmlElement);
      if (returnMap != null && !returnMap.values().isEmpty()) {
         for (StartupTriggerConfig startupTriggerConfig : returnMap.values()) {
            startupTriggerListBuilder.addStartupTriggerConfig(startupTriggerConfig);
         }
      }
      return startupTriggerListBuilder.realize();
   }

   public class StartupTriggerListBuilder implements Builder<List<StartupTriggerConfig>> {
      private volatile List<StartupTriggerConfig> m_StartupTriggerConfigList = null;

      @Override
      public List<StartupTriggerConfig> realize() {
         if (m_StartupTriggerConfigList == null) {
            m_StartupTriggerConfigList = Collections.emptyList();
         }
         return m_StartupTriggerConfigList;
      }

      public void addStartupTriggerConfig(StartupTriggerConfig startupTriggerConfig) {
         if (m_StartupTriggerConfigList == null) {
            m_StartupTriggerConfigList = new ArrayList<StartupTriggerConfig>();
         }
         m_StartupTriggerConfigList.add(startupTriggerConfig);
      }

   }

   @XmlSimpleName(SchedulerNamespaceHandler.JOB_TRIGGER_TYPE_SIMPLE)
   public static class SimpleTriggerProcessor implements ElementProcessor<StartupTriggerProcessor.SimpleTriggerConfig> {

      @Override
      public SimpleTriggerConfig process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
         SimpleTriggerConfig simpleTriggerConfig = new SimpleTriggerConfig();
         context.inject(simpleTriggerConfig, xmlElement);
         return simpleTriggerConfig;
      }

   }

   @XmlSimpleName(SchedulerNamespaceHandler.JOB_TRIGGER_TYPE_CRON)
   public static class CronTriggerProcessor implements ElementProcessor<StartupTriggerProcessor.CronTriggerConfig> {

      @Override
      public CronTriggerConfig process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
         CronTriggerConfig cronTriggerConfig = new CronTriggerConfig();
         context.inject(cronTriggerConfig, xmlElement);
         return cronTriggerConfig;
      }

   }
}
