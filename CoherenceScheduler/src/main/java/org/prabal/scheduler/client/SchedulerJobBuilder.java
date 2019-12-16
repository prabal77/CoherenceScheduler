/**
 * 
 */
package org.prabal.scheduler.client;

import java.util.HashMap;
import java.util.Map;

import org.prabal.scheduler.core.BaseKey;
import org.prabal.scheduler.core.Job;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.JobKey;
import org.prabal.scheduler.core.SimpleJobDetail;
import org.prabal.scheduler.core.Trigger;

import com.tangosol.util.Builder;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
public class SchedulerJobBuilder implements Builder<JobDetails<Object, Object>> {
   private JobKey key;
   private String description;
   private String jobClass;
   private boolean durability;
   private boolean shouldRecover;
   private Map<Object, Object> jobDataMap = new HashMap<Object, Object>();

   private SchedulerJobBuilder() {
   }

   public static SchedulerJobBuilder newJob() {
      return new SchedulerJobBuilder();
   }

   /**
    * Create a JobBuilder with which to define a {@link JobDetails}, and set the class name of the
    * {@link Job} to be executed.
    * 
    * @return a new SchedulerJobBuilder
    */
   public static SchedulerJobBuilder newJob(Class<? extends Job> jobClass) {
      SchedulerJobBuilder jobBuilder = new SchedulerJobBuilder();
      jobBuilder.ofType(jobClass);
      return jobBuilder;
   }

   /**
    * Create a JobBuilder with which to define a {@link JobDetails}, and set the class name of the
    * {@link Job} to be executed.
    * 
    * @return a new SchedulerJobBuilder
    */
   public static SchedulerJobBuilder newJob(String jobClassName) {
      SchedulerJobBuilder jobBuilder = new SchedulerJobBuilder();
      jobBuilder.setJobClassName(jobClassName);
      return jobBuilder;
   }

   public SchedulerJobBuilder setJobClassName(String jobClassName) {
      this.jobClass = jobClassName;
      return this;
   }

   /**
    * Set the class which will be instantiated and executed when a Trigger fires that is associated
    * with this {@link JobDetails}.
    * 
    * @param jobClazz a class implementing the Job interface.
    * @return the updated SchedulerJobBuilder
    * @see SchedulerJobBuilder#getJobClass()
    */
   public SchedulerJobBuilder ofType(Class<? extends Job> jobClazz) {
      this.jobClass = jobClazz.getName();
      return this;
   }

   /**
    * Use a {@link JobKey} with the given name and default group to identify the {@link JobDetails}
    * <p>
    * If none of the 'withIdentity' methods are set on the SchedulerJobBuilder, then a random,
    * unique {@link JobKey} will be generated.
    * </p>
    * 
    * @param name the name element for the Job's {@link JobKey}
    * @return the updated SchedulerJobBuilder
    * @see BaseKey
    * @see JobDetail#getKey()
    */
   public SchedulerJobBuilder withIdentity(String name) {
      this.key = new JobKey(name);
      return this;
   }

   /**
    * Use a {@link JobKey} with the given name and group to identify the JobDetail.
    * 
    * <p>
    * If none of the 'withIdentity' methods are set on the JobBuilder, then a random, unique
    * {@link JobKey} will be generated.
    * </p>
    * 
    * @param name the name element for the Job's {@link JobKey}
    * @param group the group element for the Job's {@link JobKey}
    * @return the updated SchedulerJobBuilder
    * @see BaseKey
    * @see JobDetail#getKey()
    */
   public SchedulerJobBuilder withIdentity(String name, String group) {
      this.key = new JobKey(name, group);
      return this;
   }

   /**
    * Use a {@link JobKey} to identify the JobDetail.
    * 
    * <p>
    * If none of the 'withIdentity' methods are set on the SchedulerJobBuilder, then a random,
    * unique JobKey will be generated.
    * </p>
    * 
    * @param jobKey the Job's JobKey
    * @return the updated JobBuilder
    * @see JobKey
    * @see JobDetail#getKey()
    */
   public SchedulerJobBuilder withIdentity(JobKey jobKey) {
      this.key = jobKey;
      return this;
   }

   /**
    * Set the given (human-meaningful) description of the Job.
    * 
    * @param jobDescription the description for the Job
    * @return the updated JobBuilder
    * @see JobDetail#getDescription()
    */
   public SchedulerJobBuilder withDescription(String jobDescription) {
      this.description = jobDescription;
      return this;
   }

   /**
    * Instructs the <code>Scheduler</code> whether or not the <code>Job</code> should be re-executed
    * if a 'recovery' or 'fail-over' situation is encountered.
    * 
    * <p>
    * If not explicitly set, the default value is <code>false</code>.
    * </p>
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#requestsRecovery()
    */
   public SchedulerJobBuilder requestRecovery() {
      this.shouldRecover = true;
      return this;
   }

   /**
    * Instructs the <code>Scheduler</code> whether or not the <code>Job</code> should be re-executed
    * if a 'recovery' or 'fail-over' situation is encountered.
    * 
    * <p>
    * If not explicitly set, the default value is <code>false</code>.
    * </p>
    * 
    * @param jobShouldRecover the desired setting
    * @return the updated SchedulerJobBuilder
    */
   public SchedulerJobBuilder requestRecovery(boolean jobShouldRecover) {
      this.shouldRecover = jobShouldRecover;
      return this;
   }

   /**
    * Whether or not the <code>Job</code> should remain stored after it is orphaned (no
    * <code>{@link Trigger}s</code> point to it).
    * 
    * <p>
    * If not explicitly set, the default value is <code>false</code> - this method sets the value to
    * <code>true</code>.
    * </p>
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#isDurable()
    */
   public SchedulerJobBuilder storeDurably() {
      this.durability = true;
      return this;
   }

   /**
    * Whether or not the <code>Job</code> should remain stored after it is orphaned (no
    * <code>{@link Trigger}s</code> point to it).
    * 
    * <p>
    * If not explicitly set, the default value is <code>false</code>.
    * </p>
    * 
    * @param jobDurability the value to set for the durability property.
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#isDurable()
    */
   public SchedulerJobBuilder storeDurably(boolean jobDurability) {
      this.durability = jobDurability;
      return this;
   }

   /**
    * Add the given key-value pair to the JobDetail's {@link JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder usingJobData(String dataKey, String value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the JobDetail's {@link JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder usingJobData(String dataKey, Integer value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the JobDetail's {@link JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder usingJobData(String dataKey, Long value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the JobDetail's {@link JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder usingJobData(String dataKey, Float value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the JobDetail's {@link JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder usingJobData(String dataKey, Double value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the JobDetail's {@link JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder usingJobData(String dataKey, Boolean value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add all the data from the given {@link JobDataMap} to the {@code JobDetail}'s
    * {@code JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder usingJobData(Map<Object, Object> newJobDataMap) {
      jobDataMap.putAll(newJobDataMap);
      return this;
   }

   /**
    * Replace the {@code JobDetail}'s {@link JobDataMap} with the given {@code JobDataMap}.
    * 
    * @return the updated SchedulerJobBuilder
    * @see JobDetail#getJobDataMap()
    */
   public SchedulerJobBuilder setJobData(Map<Object, Object> newJobDataMap) {
      jobDataMap = newJobDataMap;
      return this;
   }

   @Override
   public JobDetails<Object, Object> realize() {
      SimpleJobDetail<Object, Object> job = new SimpleJobDetail<Object, Object>();

      job.setJobClassName(jobClass);
      job.setDescription(description);
      if (key == null)
         throw new WrapperException("Key is mandatory. Provide identity to the JobDetails");
      job.setJobKey(key);
      job.setDurable(durability);
      job.setShouldRecover(shouldRecover);
      if (!jobDataMap.isEmpty())
         job.setJobDataMap(jobDataMap);

      return job;
   }

}
