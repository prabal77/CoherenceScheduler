/**
 * 
 */
package org.prabal.scheduler.client;

import java.util.Collections;

import org.prabal.scheduler.core.BaseCoherenceCalendar;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.ResultInstanceKey;
import org.prabal.scheduler.core.SubmissionResult;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.internals.SchedulerContantsEnum;
import org.prabal.scheduler.triggers.AbstractTrigger;

import com.oracle.coherence.common.util.ObjectProxyFactory;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;
import com.tangosol.util.WrapperException;
import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.filter.NotFilter;
import com.tangosol.util.filter.PresentFilter;
import com.tangosol.util.processor.ConditionalPut;

/**
 * @author Prabal Nandi
 *
 */
public class CoherenceSchedulerFacade {
   private NamedCache namedCache;
   private static final String DEFAULT_SUBMISSION_CACHE = SchedulerContantsEnum.DEFAULT_SUBMISSION_CACHE.getConstantValue();

   public CoherenceSchedulerFacade() {
      this.namedCache = CacheFactory.getCache(DEFAULT_SUBMISSION_CACHE);
   }

   /**
    * <p>
    * Submits the job to the Submission Cache for Scheduling
    * </p>
    */
   public void scheduleJob(JobDetails<Object, Object> jobDetail, Trigger trigger) throws WrapperException {
      if (jobDetail == null) {
         throw new WrapperException("JobDetail cannot be null");
      }
      if (trigger == null) {
         throw new WrapperException("Trigger cannot be null");
      }
      if (jobDetail.getJobKey() == null) {
         throw new WrapperException("Job's key cannot be null");
      }
      if (jobDetail.getJobClassName() == null || jobDetail.getJobClassName().equals("")) {
         throw new WrapperException("Job's class cannot be null");
      }
      if (trigger.getJobKey() == null) {
         ((AbstractTrigger) trigger).setJobKey(jobDetail.getJobKey());
      }
      else if (!trigger.getJobKey().equals(jobDetail.getJobKey())) {
         throw new WrapperException("Trigger does not reference given job!");
      }
      try {
         this.namedCache.putAll(Collections.singletonMap(jobDetail.getJobKey(), jobDetail));
         this.namedCache.putAll(Collections.singletonMap(trigger.getKey(), trigger));
      }
      catch (Throwable throwable) {
         throw new WrapperException(throwable, "Error while submitting job to submission cache. " + throwable.getMessage());
      }
   }

   /**
    * <p>
    * Calls the equivalent method on the 'proxied' <code>QuartzScheduler</code>.
    * </p>
    */
   public void scheduleJob(Trigger trigger) throws WrapperException {
      if (trigger == null) {
         throw new WrapperException("Trigger cannot be null");
      }
      if (trigger.getJobKey() == null) {
         throw new WrapperException("Target JobKey is not provided as part of this trigger");
      }
      try {
         this.namedCache.putAll(Collections.singletonMap(trigger.getKey(), trigger));
      }
      catch (Throwable throwable) {
         throw new WrapperException(throwable, "Error while submitting job to submission cache");
      }
   }

   public void storeCalendar(String calendarName, BaseCoherenceCalendar baseCoherenceCalendar, boolean overrideExisting) {
      if (baseCoherenceCalendar == null) {
         throw new WrapperException("BaseCoherenceCalendar cannot be null");
      }
      Filter filter = AlwaysFilter.INSTANCE;
      if (!overrideExisting)
         filter = new NotFilter(PresentFilter.INSTANCE);

      this.namedCache.invoke(calendarName, new ConditionalPut(filter, baseCoherenceCalendar));
   }

   /**
    * <p>
    * Submits the job to the Submission Cache for Scheduling
    * </p>
    */
   public void scheduleJobUnique(JobDetails<Object, Object> jobDetail, Trigger trigger) throws WrapperException {
      if (jobDetail == null) {
         throw new WrapperException("JobDetail cannot be null");
      }
      if (trigger == null) {
         throw new WrapperException("Trigger cannot be null");
      }
      if (jobDetail.getJobKey() == null) {
         throw new WrapperException("Job's key cannot be null");
      }
      if (jobDetail.getJobClassName() == null || jobDetail.getJobClassName().equals("")) {
         throw new WrapperException("Job's class cannot be null");
      }
      if (trigger.getJobKey() == null) {
         ((AbstractTrigger) trigger).setJobKey(jobDetail.getJobKey());
      }
      else if (!trigger.getJobKey().equals(jobDetail.getJobKey())) {
         throw new WrapperException("Trigger does not reference given job!");
      }
      try {
         Filter filter = new NotFilter(PresentFilter.INSTANCE);
         this.namedCache.invoke(jobDetail.getJobKey(), new ConditionalPut(filter, jobDetail));
         this.namedCache.invoke(trigger.getKey(), new ConditionalPut(filter, trigger));
      }
      catch (Throwable throwable) {
         throw new WrapperException(throwable, "Error while submitting job to submission cache. " + throwable.getMessage());
      }
   }

   public void scheduleJobUnique(Trigger trigger) throws WrapperException {
      if (trigger == null) {
         throw new WrapperException("Trigger cannot be null");
      }
      if (trigger.getJobKey() == null) {
         throw new WrapperException("Target JobKey is not provided as part of this trigger");
      }
      try {
         Filter filter = new NotFilter(PresentFilter.INSTANCE);
         this.namedCache.invoke(trigger.getKey(), new ConditionalPut(filter, trigger));
      }
      catch (Throwable throwable) {
         throw new WrapperException(throwable, "Error while submitting job to submission cache");
      }
   }

   public SubmissionResult getResultInstanceReadOnly(ResultInstanceKey resultInstanceKey) {
      ObjectProxyFactory<SubmissionResult> objectProxyFactory = new ObjectProxyFactory<SubmissionResult>(SchedulerContantsEnum.RESULT_STORE_CACHE.getConstantValue(), SubmissionResult.class);
      return objectProxyFactory.getLocalCopyOfRemoteObject(resultInstanceKey);
   }

   public SubmissionResult getResultInstance(ResultInstanceKey resultInstanceKey) {
      ObjectProxyFactory<SubmissionResult> objectProxyFactory = new ObjectProxyFactory<SubmissionResult>(SchedulerContantsEnum.RESULT_STORE_CACHE.getConstantValue(), SubmissionResult.class);
      return objectProxyFactory.getProxy(resultInstanceKey);
   }

}