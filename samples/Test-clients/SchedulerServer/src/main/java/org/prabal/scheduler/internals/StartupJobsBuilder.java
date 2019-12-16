/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.prabal.scheduler.client.SchedulerJobBuilder;
import org.prabal.scheduler.client.SchedulerTriggerBuilder;
import org.prabal.scheduler.core.CronScheduleBuilder;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.SimpleScheduleBuilder;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.processor.config.StartupJobConfig;
import org.prabal.scheduler.processor.config.StartupTriggerConfig;

import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
public class StartupJobsBuilder {

   public static JobDetails buildStartupJobDetails(StartupJobConfig jobConfig) {
      SchedulerJobBuilder jobBuilder = SchedulerJobBuilder.newJob(jobConfig.getJobClass());
      jobBuilder.withIdentity(jobConfig.getJobName(), jobConfig.getJobGroup());
      jobBuilder.withDescription(jobConfig.getJobDescription());
      jobBuilder.storeDurably(jobConfig.isDurable());
      jobBuilder.requestRecovery(jobConfig.isRecoverable());

      for (Map.Entry<String, String> jobDataMapEntry : jobConfig.getJobDataMap().entrySet()) {
         jobBuilder.usingJobData(jobDataMapEntry.getKey(), jobDataMapEntry.getValue());
      }
      return jobBuilder.realize();
   }

   public static List<Trigger> buildStartupTriggerList(StartupJobConfig jobConfig, JobDetails targetJobDetails) {

      List<Trigger> triggerList = new ArrayList<Trigger>(jobConfig.getTriggerConfigList().size());

      for (StartupTriggerConfig triggerConfig : jobConfig.getTriggerConfigList()) {

         SchedulerTriggerBuilder<Trigger> triggerBuilder = SchedulerTriggerBuilder.newTrigger();
         triggerBuilder.forJob(targetJobDetails);
         triggerBuilder.withIdentity(triggerConfig.getTriggerName(), triggerConfig.getTriggerGroup());
         triggerBuilder.withDescription(triggerConfig.getTriggerDescription());

         for (Map.Entry<String, String> triggerDataMapEntry : triggerConfig.getTriggerDataMap().entrySet()) {
            triggerBuilder.usingJobData(triggerDataMapEntry.getKey(), triggerDataMapEntry.getValue());
         }

         switch (triggerConfig.getTriggerType()) {
            case TRIGGER_TYPE_SIMPLE:
               triggerBuilder = buildSimpleTriggerScheduler(triggerConfig, triggerBuilder);
               break;
            case TRIGGER_TYPE_CRON:
               triggerBuilder = buildCronTriggerScheduler(triggerConfig, triggerBuilder);
               break;
            default:
               throw new WrapperException("Incorrect TriggerType is passed");
         }
         triggerList.add(triggerBuilder.realize());
      }
      return triggerList;
   }

   private static SchedulerTriggerBuilder<Trigger> buildSimpleTriggerScheduler(StartupTriggerConfig triggerConfig, SchedulerTriggerBuilder<Trigger> triggerBuilder) {

      SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
      if (triggerConfig.getRepeatCount() == -1) {
         simpleScheduleBuilder.repeatForever();
      }
      else {
         simpleScheduleBuilder.withRepeatCount(triggerConfig.getRepeatCount());
      }
      switch (triggerConfig.getRepeatIntervalUnit().toUpperCase()) {
         case "S":
            simpleScheduleBuilder.withIntervalInSeconds(triggerConfig.getRepeatInterval());
            break;
         case "M":
            simpleScheduleBuilder.withIntervalInMinutes(triggerConfig.getRepeatInterval());
            break;
         case "H":
            simpleScheduleBuilder.withIntervalInHours(triggerConfig.getRepeatInterval());
            break;
         default:
            simpleScheduleBuilder.withIntervalInSeconds(triggerConfig.getRepeatInterval());
            break;
      }
      // TODO Set MISFIRE Policy
      triggerBuilder.withSchedule(simpleScheduleBuilder);
      if (triggerConfig.getTriggerStartTime() == null) {
         triggerBuilder.startNow();
      }
      else {
         triggerBuilder.startAt(triggerConfig.getTriggerStartTime());
      }
      triggerBuilder.endAt(triggerConfig.getTriggerEndTime());
      return triggerBuilder;
   }

   private static SchedulerTriggerBuilder<Trigger> buildCronTriggerScheduler(StartupTriggerConfig triggerConfig, SchedulerTriggerBuilder<Trigger> triggerBuilder) {
      CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(triggerConfig.getCronExpression());
      // TODO Set MISFIRE Policy
      triggerBuilder.withSchedule(cronScheduleBuilder);
      return triggerBuilder;
   }
}