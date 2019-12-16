/**
 * 
 */
package org.prabal.scheduler.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.StartupJobConfig;

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
@XmlSimpleName(SchedulerNamespaceHandler.JOB_LIST)
public class StartupJobListProcessor implements ElementProcessor<List<StartupJobConfig>> {

   @SuppressWarnings("unchecked")
   @Override
   public List<StartupJobConfig> process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
      StartupJobListBuilder startupJobBuilder = new StartupJobListBuilder();
      Map<String, StartupJobConfig> returnMap = (Map<String, StartupJobConfig>) context.processElementsOf(xmlElement);
      if (returnMap != null && !returnMap.values().isEmpty()) {
         for (StartupJobConfig jobConfig : returnMap.values()) {
            startupJobBuilder.addStartupJobConfig(jobConfig);
         }
      }
      List<StartupJobConfig> startupJobConfigList = startupJobBuilder.realize();
      context.getResourceRegistry().registerResource(List.class, SchedulerNamespaceHandler.RESOURCE_STARTUP_JOB_LIST, startupJobConfigList);
      return startupJobConfigList;
   }

   public class StartupJobListBuilder implements Builder<List<StartupJobConfig>> {
      private volatile List<StartupJobConfig> m_StartupJobConfigList = null;

      @Override
      public List<StartupJobConfig> realize() {
         if (m_StartupJobConfigList == null) {
            m_StartupJobConfigList = Collections.emptyList();
         }
         return m_StartupJobConfigList;
      }

      public void addStartupJobConfig(StartupJobConfig startupJobConfig) {
         if (m_StartupJobConfigList == null) {
            m_StartupJobConfigList = new ArrayList<StartupJobConfig>();
         }
         m_StartupJobConfigList.add(startupJobConfig);
      }
   }

}