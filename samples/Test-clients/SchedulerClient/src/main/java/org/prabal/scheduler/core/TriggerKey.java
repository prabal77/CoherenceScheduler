/**
 * 
 */
package org.prabal.scheduler.core;


/**
 * @author Prabal Nandi
 *
 */
public class TriggerKey extends BaseKey {
   private final JobKey jobKey;

   public TriggerKey(String name, String group, JobKey jobKey) {
      super(name, group, BaseKey.TRIGGER_KEY);
      this.jobKey = jobKey;
   }

   public TriggerKey(String name, JobKey jobKey) {
      super(name, null, BaseKey.TRIGGER_KEY);
      this.jobKey = jobKey;
   }

   public JobKey getJobKey() {
      return jobKey;
   }

   @Override
   public Object getAssociatedKey() {
      return this.jobKey.getAssociatedKey();
   }

   @Override
   public String toString() {
      return "TriggerKey [jobKey=" + jobKey + ", getName()=" + getName() + ", getGroup()=" + getGroup() + ", getType()=" + getType() + "]";
   }

}