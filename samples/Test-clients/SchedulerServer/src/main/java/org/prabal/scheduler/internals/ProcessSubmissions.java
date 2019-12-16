/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.concurrent.atomic.AtomicBoolean;

import org.prabal.scheduler.core.BaseCoherenceCalendar;
import org.prabal.scheduler.core.DefaultSubmissionResult;
import org.prabal.scheduler.core.ExecutionStatus;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.JobKey;
import org.prabal.scheduler.core.ResultInstanceKey;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * @author Prabal Nandi
 *
 */
public class ProcessSubmissions {
   private static NamedCache jobStoreCache = null;
   private static NamedCache triggerStoreCache = null;
   private static NamedCache resultStoreCache = null;
   private static volatile AtomicBoolean startedFlag = new AtomicBoolean(false);

   public static void processTriggers(TriggerKey triggerKey, Trigger triggerInstance) {
      initialize();
      try {
         triggerInstance = ProcessSubmissions.setTriggerInitialState(triggerInstance);
         if (triggerInstance.getStartTime() == null)
            triggerInstance.setStartTime(ClusterInfoUtil.getCurrentClusterTime());

         // triggerInstance.setStartTime(new Date(triggerInstance.getStartTime().getTime()));
         triggerInstance.validate();

         BaseCoherenceCalendar calendar = null;
         if (triggerInstance.getCalendarName() != null && triggerInstance.getCalendarName().equals("")) {
            calendar = (BaseCoherenceCalendar) jobStoreCache.get(triggerInstance.getCalendarName());
         }

         triggerInstance.computeFirstFireTime(calendar);
         triggerStoreCache.put(triggerKey, triggerInstance);
         addResultInstanceKey(triggerKey);
      }
      catch (Exception exception) {
         CacheFactory.log("Error submitting Trigger with Key = " + triggerKey + ". Exception " + exception.getMessage(), CacheFactory.LOG_ERR);
      }
   }

   public static void processJobDetails(JobKey jobKey, JobDetails jobDetails) {
      initialize();
      try {
         jobDetails.setJobStatus(ExecutionStatus.NORMAL);
         jobStoreCache.put(jobKey, jobDetails);
      }
      catch (Exception exception) {
         CacheFactory.log("Error submitting JobDetails with Key = " + jobKey + ". Exception " + exception.getMessage(), CacheFactory.LOG_ERR);
      }
   }

   public static void processCalendar(String calendarName, BaseCoherenceCalendar calendarInstance) {
      initialize();
   }

   private static Trigger setTriggerInitialState(Trigger trigger) {
      trigger.changeTriggerState(ExecutionStatus.NORMAL);
      if (triggerStoreCache.containsKey(SchedulerContantsEnum.PAUSED_TRIGGER_PREFIX.getConstantValue() + trigger.getKey().getGroup())
            || triggerStoreCache.containsKey(SchedulerContantsEnum.PAUSED_JOB_GROUP_PREFIX.getConstantValue() + trigger.getJobKey().getGroup())) {
         trigger.changeTriggerState(ExecutionStatus.PAUSED);
      }
      JobDetails details = (JobDetails) jobStoreCache.get(trigger.getJobKey());
      if (details != null && details.getJobStatus() == ExecutionStatus.BLOCKED) {
         trigger.changeTriggerState(ExecutionStatus.BLOCKED);
      }
      return trigger;
   }

   private static void initialize() {
      if (!startedFlag.get()) {
         synchronized (ProcessSubmissions.class) {
            if (!startedFlag.get()) {
               triggerStoreCache = CacheFactory.getCache(SchedulerContantsEnum.TRIGGER_STORE_CACHE.getConstantValue());
               jobStoreCache = CacheFactory.getCache(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue());
               resultStoreCache = CacheFactory.getCache(SchedulerContantsEnum.RESULT_STORE_CACHE.getConstantValue());
               startedFlag.compareAndSet(false, true);
            }
         }
      }
   }

   private static void addResultInstanceKey(TriggerKey triggerKey) {
      ResultInstanceKey resultInstanceKey = new ResultInstanceKey(triggerKey);
      DefaultSubmissionResult defaultSubmissionResult = new DefaultSubmissionResult<Object>();
      resultStoreCache.put(resultInstanceKey, defaultSubmissionResult);
   }
}