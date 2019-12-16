/**
 * 
 */
package org.prabal.scheduler.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.prabal.scheduler.internals.SchedulerContantsEnum;
import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.StartupTriggerConfig;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.annotation.Injectable;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.net.CacheFactory;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.Builder;

/**
 * @author Prabal Nandi
 *
 */
@XmlSimpleName(SchedulerNamespaceHandler.JOB_TRIGGER_INSTANCE)
public class StartupTriggerProcessor implements ElementProcessor<StartupTriggerConfig> {
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat(SchedulerContantsEnum.START_END_DATE_FORMAT.getConstantValue());

   @Override
   public StartupTriggerConfig process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
      StartupTriggerInstanceBuilder startupTriggerInstanceBuilder = new StartupTriggerInstanceBuilder();
      context.inject(startupTriggerInstanceBuilder, xmlElement);
      return startupTriggerInstanceBuilder.realize();
   }

   public class StartupTriggerInstanceBuilder implements Builder<StartupTriggerConfig> {
      private String triggerName;
      private String triggerGroup;
      private SchedulerContantsEnum triggerType;
      private String triggerDescription;
      private Map<String, String> triggerDataMap;
      private SimpleTriggerConfig simpleTriggerConfig;
      private CronTriggerConfig cronTriggerConfig;
      private AtomicBoolean triggerTypeSet = new AtomicBoolean(false);

      @Override
      public StartupTriggerConfig realize() {
         StartupTriggerConfig startupTriggerConfig = new StartupTriggerConfig();
         startupTriggerConfig.setTriggerName(this.triggerName.trim());
         startupTriggerConfig.setTriggerGroup(this.triggerGroup.trim());
         startupTriggerConfig.setTriggerType(this.triggerType);
         startupTriggerConfig.setTriggerDescription(this.triggerDescription.trim());

         if (this.triggerType == null)
            throw new ConfigurationException("Trigger Type element not configured", "You need to configure one Trigger (either Simple of Cron) type per trigger");

         switch (this.triggerType) {
            case TRIGGER_TYPE_SIMPLE: {
               if (this.simpleTriggerConfig != null) {
                  startupTriggerConfig.setTriggerStartTime(this.simpleTriggerConfig.getTriggerStartTime());
                  startupTriggerConfig.setTriggerEndTime(this.simpleTriggerConfig.getTriggerEndTime());
                  startupTriggerConfig.setRepeatCount(this.simpleTriggerConfig.getRepeatCount());
                  startupTriggerConfig.setRepeatInterval(this.simpleTriggerConfig.getRepeatInterval());
                  startupTriggerConfig.setRepeatIntervalUnit(this.simpleTriggerConfig.getRepeatIntervalUnit());
                  startupTriggerConfig.setMisfirePolicy(this.simpleTriggerConfig.getMisFirePolicy());
                  startupTriggerConfig.setCronExpression(null);
                  break;
               }
            }
            case TRIGGER_TYPE_CRON: {
               startupTriggerConfig.setTriggerStartTime(null);
               startupTriggerConfig.setTriggerEndTime(null);
               startupTriggerConfig.setRepeatCount(-1);
               startupTriggerConfig.setRepeatInterval(1);
               startupTriggerConfig.setRepeatIntervalUnit(null);
               startupTriggerConfig.setMisfirePolicy(this.cronTriggerConfig.getMisFirePolicy());
               startupTriggerConfig.setCronExpression(this.cronTriggerConfig.getCronExpression());
               break;
            }
            default: {
               break;
            }
         }
         startupTriggerConfig.setTriggerDataMap(this.triggerDataMap);
         return startupTriggerConfig;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_NAME)
      public void setTriggerName(String triggerName) {
         this.triggerName = triggerName;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_GROUP)
      public void setTriggerGroup(String triggerGroup) {
         this.triggerGroup = triggerGroup;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_TYPE_SIMPLE)
      public void setTriggerType(SimpleTriggerConfig simpleTriggerConfig) {

         if (triggerTypeSet.compareAndSet(false, true)) {
            this.triggerType = SchedulerContantsEnum.TRIGGER_TYPE_SIMPLE;
            this.simpleTriggerConfig = simpleTriggerConfig;
         }
         else {
            CacheFactory.log("One Trigger cannot have multiple types. Trigger type is already set to Cron Trigger. Rejecting Simple Trigger Configurations", CacheFactory.LOG_WARN);
         }
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_TYPE_CRON)
      public void setTriggerTypeCron(CronTriggerConfig cronTriggerConfig) {

         if (triggerTypeSet.compareAndSet(false, true)) {
            this.triggerType = SchedulerContantsEnum.TRIGGER_TYPE_CRON;
            this.cronTriggerConfig = cronTriggerConfig;
         }
         else {
            CacheFactory.log("One Trigger cannot have multiple types. Trigger type is already set to Simple Trigger. Rejecting Cron trigger Configurations", CacheFactory.LOG_WARN);
         }
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_DESCRIPTION)
      public void setTriggerDescription(String triggerDescription) {
         this.triggerDescription = triggerDescription;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_DATA_MAP)
      public void setTriggerDataMap(Map<String, String> triggerDataMap) {
         this.triggerDataMap = triggerDataMap;
      }

   }

   public static class SimpleTriggerConfig {
      private Date triggerStartTime;
      private Date triggerEndTime;
      private int repeatCount;
      private int repeatInterval;
      private String repeatIntervalUnit;
      private String misFirePolicy;

      public SimpleTriggerConfig() {
         super();
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_START_TIME)
      public void setTriggerStartTime(String triggerStartTime) {
         try {
            this.triggerStartTime = (triggerStartTime == null || triggerStartTime.equals("")) ? null : dateFormat.parse(triggerStartTime);
         }
         catch (ParseException exception) {
            this.triggerStartTime = null;
            CacheFactory.log("Error parsing startDate " + triggerStartTime + ". Trigger will be fired right away. Exception message = " + exception.getMessage(), CacheFactory.LOG_WARN);
         }
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_END_TIME)
      public void setTriggerEndTime(String triggerEndTime) {
         try {
            this.triggerEndTime = (triggerEndTime == null || triggerEndTime.equals("")) ? null : dateFormat.parse(triggerEndTime);
         }
         catch (ParseException exception) {
            this.triggerEndTime = null;
            CacheFactory.log("Error parsing startDate " + triggerEndTime + ". Setting Infinite as End Date. Exception message = " + exception.getMessage(), CacheFactory.LOG_WARN);
         }
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_REPEAT_COUNT)
      public void setRepeatCount(String repeatCount) {
         try {
            this.repeatCount = Integer.parseInt((repeatCount != null && !repeatCount.isEmpty()) ? repeatCount : "-1");
         }
         catch (NumberFormatException exception) {
            this.repeatCount = -1;
            CacheFactory.log("Error parsing repeatCount " + repeatCount + ". Trigger set to repeat forever. Exception message = " + exception.getMessage(), CacheFactory.LOG_WARN);
         }
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_REPEAT_INTERVAL)
      public void setRepeatInterval(String repeatInterval) {
         if (repeatInterval == null || repeatInterval.isEmpty() || (repeatInterval.length() < 2)) {
            throw new ConfigurationException("Mandatory field repeat interval is blank or incorrect. = " + repeatInterval, "Repeat Interval cannot be null or incorrect");
         }
         try {
            int lastindex = repeatInterval.length() - 1;
            this.repeatInterval = Integer.parseInt(repeatInterval.substring(0, lastindex));
            this.repeatIntervalUnit = String.valueOf(repeatInterval.charAt(lastindex));

            if (this.repeatInterval <= 0) {
               throw new NumberFormatException("Repeat Interval is less than or equal to 0");
            }

            if (repeatIntervalUnit == null || (!repeatIntervalUnit.equalsIgnoreCase("S") && !repeatIntervalUnit.equalsIgnoreCase("M") && !repeatIntervalUnit.equalsIgnoreCase("H"))) {
               throw new ConfigurationException("Incorrect format for repeat interval", "Correct format is #TimeValue#[s,S,m,M,h,H]");
            }
         }
         catch (NumberFormatException exception) {
            this.repeatInterval = 1;
            this.repeatIntervalUnit = "S";
            CacheFactory.log("Error parsing repeatInterval " + repeatInterval + ". Hence using 1 second as repeat Interval. Exception message = " + exception.getMessage(), CacheFactory.LOG_WARN);
         }
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_MISFIRE_POLICY)
      public void setMisFirePolicy(String misFirePolicy) {
         this.misFirePolicy = misFirePolicy;
      }

      public Date getTriggerStartTime() {
         return triggerStartTime;
      }

      public Date getTriggerEndTime() {
         return triggerEndTime;
      }

      public int getRepeatCount() {
         return repeatCount;
      }

      public int getRepeatInterval() {
         return repeatInterval;
      }

      public String getRepeatIntervalUnit() {
         return repeatIntervalUnit;
      }

      public String getMisFirePolicy() {
         return misFirePolicy;
      }

      @Override
      public String toString() {
         return "SimpleTriggerConfig [triggerStartTime=" + triggerStartTime + ", triggerEndTime=" + triggerEndTime + ", repeatCount=" + repeatCount + ", repeatInterval=" + repeatInterval
               + ", repeatIntervalUnit=" + repeatIntervalUnit + ", misFirePolicy=" + misFirePolicy + "]";
      }

   }

   public static class CronTriggerConfig {
      private String cronExpression;
      private String misFirePolicy;

      public CronTriggerConfig() {
         super();
      }

      public String getCronExpression() {
         return cronExpression;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_CRON_EXP)
      public void setCronExpression(String cronExpression) {
         this.cronExpression = cronExpression;
      }

      public String getMisFirePolicy() {
         return misFirePolicy;
      }

      @Injectable(SchedulerNamespaceHandler.JOB_TRIGGER_MISFIRE_POLICY)
      public void setMisFirePolicy(String misFirePolicy) {
         this.misFirePolicy = misFirePolicy;
      }

      @Override
      public String toString() {
         return "CronTriggerConfig [cronExpression=" + cronExpression + ", misFirePolicy=" + misFirePolicy + "]";
      }

   }

}