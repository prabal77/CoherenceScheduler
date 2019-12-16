/**
 * 
 */
package org.prabal.scheduler.processor;

import java.util.List;
import java.util.Map;

import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.StartupJobConfig;
import org.prabal.scheduler.processor.config.StartupTriggerConfig;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.annotation.Injectable;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.Builder;

/**
 * @author Prabal Nandi
 *
 */
@XmlSimpleName(SchedulerNamespaceHandler.JOB_INSTANCE)
public class StartupJobProcessor implements ElementProcessor<StartupJobConfig> {

   @Override
   public StartupJobConfig process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
      StartupJobInstanceBuilder jobInstanceBuilder = new StartupJobInstanceBuilder();
      context.inject(jobInstanceBuilder, xmlElement);
      return jobInstanceBuilder.realize();
   }

   public class StartupJobInstanceBuilder implements Builder<StartupJobConfig> {
      private String jobName;
      private String jobGroup;
      private String jobDescription;
      private String jobClass;
      private boolean durable;
      private boolean recoverable;
      private Map<String, String> jobDataMap = null;
      private List<StartupTriggerConfig> triggerConfigList = null;

      @Override
      public StartupJobConfig realize() {
         StartupJobConfig startupJobConfig = new StartupJobConfig();
         if (this.jobName == null || this.jobName.equals("") || this.jobClass == null || this.jobClass.equals("") || this.triggerConfigList == null || this.triggerConfigList.isEmpty())
            throw new ConfigurationException("Please check the configuration. One or more mandatory fields are missing.", "Mandatory Fields are JobName, JobClass and Triggers");

         startupJobConfig.setJobName(this.jobName.trim());
         startupJobConfig.setJobGroup(this.jobGroup.trim());
         startupJobConfig.setJobDescription(this.jobDescription.trim());
         startupJobConfig.setJobClass(this.jobClass.trim());
         startupJobConfig.setDurable(this.durable);
         startupJobConfig.setRecoverable(this.recoverable);
         startupJobConfig.setJobDataMap(this.jobDataMap);
         startupJobConfig.setTriggerConfigList(this.triggerConfigList);
         return startupJobConfig;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_NAME)
      public void setJobName(String jobName) {
         this.jobName = jobName;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_GROUP)
      public void setJobGroup(String jobGroup) {
         this.jobGroup = jobGroup;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_DESCRIPTION)
      public void setJobDescription(String jobDescription) {
         this.jobDescription = jobDescription;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_CLASS)
      public void setJobClass(String jobClass) {
         this.jobClass = jobClass;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_DURABLE)
      public void setDurable(boolean durable) {
         this.durable = durable;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_RECOVERABLE)
      public void setRecoverable(boolean recoverable) {
         this.recoverable = recoverable;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_DATA_MAP)
      public void setJobDataMap(Map<String, String> jobDataMap) {
         this.jobDataMap = jobDataMap;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_LIST)
      public void setTriggerConfigList(List<StartupTriggerConfig> triggerConfigList) {
         this.triggerConfigList = triggerConfigList;
      }
   }
}
