/**
 * 
 */
package org.prabal.scheduler.processor.config;

import java.util.List;
import java.util.Map;

/**
 * @author Prabal Nandi
 *
 */
public class StartupJobConfig {
   private String jobName;
   private String jobGroup;
   private String jobDescription;
   private String jobClass;
   private boolean durable;
   private boolean recoverable;
   private Map<String, String> jobDataMap = null;
   private List<StartupTriggerConfig> triggerConfigList = null;

   public StartupJobConfig() {
      super();
   }

   public StartupJobConfig(String jobName, String jobGroup, String jobDescription, String jobClass, boolean durable, boolean recoverable, Map<String, String> jobDataMap,
         List<StartupTriggerConfig> triggerConfigList) {
      super();
      this.jobName = jobName;
      this.jobGroup = jobGroup;
      this.jobDescription = jobDescription;
      this.jobClass = jobClass;
      this.durable = durable;
      this.recoverable = recoverable;
      this.jobDataMap = jobDataMap;
      this.triggerConfigList = triggerConfigList;
   }

   public String getJobName() {
      return jobName;
   }

   public void setJobName(String jobName) {
      this.jobName = jobName;
   }

   public String getJobGroup() {
      return jobGroup;
   }

   public void setJobGroup(String jobGroup) {
      this.jobGroup = jobGroup;
   }

   public String getJobDescription() {
      return jobDescription;
   }

   public void setJobDescription(String jobDescription) {
      this.jobDescription = jobDescription;
   }

   public String getJobClass() {
      return jobClass;
   }

   public void setJobClass(String jobClass) {
      this.jobClass = jobClass;
   }

   public boolean isDurable() {
      return durable;
   }

   public void setDurable(boolean durable) {
      this.durable = durable;
   }

   public boolean isRecoverable() {
      return recoverable;
   }

   public void setRecoverable(boolean recoverable) {
      this.recoverable = recoverable;
   }

   public Map<String, String> getJobDataMap() {
      return jobDataMap;
   }

   public void setJobDataMap(Map<String, String> jobDataMap) {
      this.jobDataMap = jobDataMap;
   }

   public List<StartupTriggerConfig> getTriggerConfigList() {
      return triggerConfigList;
   }

   public void setTriggerConfigList(List<StartupTriggerConfig> triggerConfigList) {
      this.triggerConfigList = triggerConfigList;
   }

   @Override
   public String toString() {
      return "StartupJobConfig [jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobDescription=" + jobDescription + ", jobClass=" + jobClass + ", durable=" + durable + ", recoverable="
            + recoverable + ", jobDataMap=" + jobDataMap + ", triggerConfigList=" + triggerConfigList + "]";
   }
}
