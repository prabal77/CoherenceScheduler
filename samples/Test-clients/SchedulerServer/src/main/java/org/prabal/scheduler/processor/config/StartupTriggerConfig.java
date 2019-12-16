/**
 * 
 */
package org.prabal.scheduler.processor.config;

import java.util.Date;
import java.util.Map;

import org.prabal.scheduler.internals.SchedulerContantsEnum;

/**
 * @author Prabal Nandi
 *
 */
public class StartupTriggerConfig {
   private String triggerName;
   private String triggerGroup;
   private SchedulerContantsEnum triggerType;
   private String triggerDescription;
   private Date triggerStartTime;
   private Date triggerEndTime;
   private int repeatCount;
   private int repeatInterval;
   private String repeatIntervalUnit;
   private String cronExpression;
   private String misfirePolicy;
   private Map<String, String> triggerDataMap;

   public StartupTriggerConfig() {
      super();
   }

   public String getTriggerName() {
      return triggerName;
   }

   public void setTriggerName(String triggerName) {
      this.triggerName = triggerName;
   }

   public String getTriggerGroup() {
      return triggerGroup;
   }

   public void setTriggerGroup(String triggerGroup) {
      this.triggerGroup = triggerGroup;
   }

   public SchedulerContantsEnum getTriggerType() {
      return triggerType;
   }

   public void setTriggerType(SchedulerContantsEnum triggerType) {
      this.triggerType = triggerType;
   }

   public String getTriggerDescription() {
      return triggerDescription;
   }

   public void setTriggerDescription(String triggerDescription) {
      this.triggerDescription = triggerDescription;
   }

   public Date getTriggerStartTime() {
      return triggerStartTime;
   }

   public void setTriggerStartTime(Date triggerStartTime) {
      this.triggerStartTime = triggerStartTime;
   }

   public Date getTriggerEndTime() {
      return triggerEndTime;
   }

   public void setTriggerEndTime(Date triggerEndTime) {
      this.triggerEndTime = triggerEndTime;
   }

   public int getRepeatCount() {
      return repeatCount;
   }

   public void setRepeatCount(int repeatCount) {
      this.repeatCount = repeatCount;
   }

   public int getRepeatInterval() {
      return repeatInterval;
   }

   public void setRepeatInterval(int repeatInterval) {
      this.repeatInterval = repeatInterval;
   }

   public String getRepeatIntervalUnit() {
      return repeatIntervalUnit;
   }

   public void setRepeatIntervalUnit(String repeatIntervalUnit) {
      this.repeatIntervalUnit = repeatIntervalUnit;
   }

   public Map<String, String> getTriggerDataMap() {
      return triggerDataMap;
   }

   public void setTriggerDataMap(Map<String, String> triggerDataMap) {
      this.triggerDataMap = triggerDataMap;
   }

   public String getCronExpression() {
      return cronExpression;
   }

   public void setCronExpression(String cronExpression) {
      this.cronExpression = cronExpression;
   }

   public String getMisfirePolicy() {
      return misfirePolicy;
   }

   public void setMisfirePolicy(String misfirePolicy) {
      this.misfirePolicy = misfirePolicy;
   }

   @Override
   public String toString() {
      return "StartupTriggerConfig [triggerName=" + triggerName + ", triggerGroup=" + triggerGroup + ", triggerType=" + triggerType + ", triggerDescription=" + triggerDescription
            + ", triggerStartTime=" + triggerStartTime + ", triggerEndTime=" + triggerEndTime + ", repeatCount=" + repeatCount + ", repeatInterval=" + repeatInterval + ", repeatIntervalUnit="
            + repeatIntervalUnit + ", cronExpression=" + cronExpression + ", misfirePolicy=" + misfirePolicy + ", triggerDataMap=" + triggerDataMap + "]";
   }
}