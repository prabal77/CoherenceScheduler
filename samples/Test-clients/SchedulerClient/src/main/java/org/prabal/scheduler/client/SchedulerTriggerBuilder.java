/**
 * 
 */
package org.prabal.scheduler.client;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.prabal.scheduler.core.CronScheduleBuilder;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.JobKey;
import org.prabal.scheduler.core.SimpleScheduleBuilder;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.triggers.AbstractTrigger;

import com.tangosol.util.Builder;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
public class SchedulerTriggerBuilder<T extends Trigger> implements Builder<Trigger> {
   private String triggerName;
   private String triggerGroup;
   private TriggerKey key;
   private String description;
   private Date startTime = new Date();
   private Date endTime;
   private int priority = Trigger.DEFAULT_PRIORITY;
   private String calendarName;
   private JobKey jobKey;
   private Map<Object, Object> jobDataMap = new HashMap<Object, Object>();

   private Builder<?> scheduleBuilder = null;

   @SuppressWarnings("unchecked")
   @Override
   public T realize() {
      if (scheduleBuilder == null)
         scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
      AbstractTrigger trig = (AbstractTrigger) scheduleBuilder.realize();

      trig.setCalendarName(calendarName);
      trig.setDescription(description);
      trig.setStartTime(startTime);
      trig.setEndTime(endTime);

      if (jobKey != null) {
         trig.setJobKey(jobKey);
         key = new TriggerKey(this.triggerName, this.triggerGroup, this.jobKey);
      }
      if (key == null)
         throw new WrapperException("Key is mandatory. Provide identity and associated Job to the TriggerBuilder");
      trig.setKey(key);
      trig.setPriority(priority);

      if (!jobDataMap.isEmpty())
         trig.setJobDataMap(jobDataMap);

      return (T) trig;
   }

   /**
    * Create a new SchedulerTriggerBuilder with which to define a specification for a Trigger.
    * 
    * @return the new SchedulerTriggerBuilder
    */
   public static SchedulerTriggerBuilder<Trigger> newTrigger() {
      return new SchedulerTriggerBuilder<Trigger>();
   }

   /**
    * Use a <code>TriggerKey</code> with the given name and default group to identify the Trigger.
    * 
    * <p>
    * If none of the 'withIdentity' methods are set on the SchedulerTriggerBuilder, then a random,
    * unique TriggerKey will be generated.
    * </p>
    * 
    * @param name the name element for the Trigger's TriggerKey
    * @return the updated SchedulerTriggerBuilder
    * @see TriggerKey
    * @see Trigger#getKey()
    */
   public SchedulerTriggerBuilder<T> withIdentity(String name) {
      this.triggerName = name;
      return this;
   }

   /**
    * Use a TriggerKey with the given name and group to identify the Trigger.
    * 
    * <p>
    * If none of the 'withIdentity' methods are set on the SchedulerTriggerBuilder, then a random,
    * unique TriggerKey will be generated.
    * </p>
    * 
    * @param name the name element for the Trigger's TriggerKey
    * @param group the group element for the Trigger's TriggerKey
    * @return the updated SchedulerTriggerBuilder
    * @see TriggerKey
    * @see Trigger#getKey()
    */
   public SchedulerTriggerBuilder<T> withIdentity(String name, String group) {
      this.triggerName = name;
      this.triggerGroup = group;
      return this;
   }

   /**
    * Use the given TriggerKey to identify the Trigger.
    * 
    * <p>
    * If none of the 'withIdentity' methods are set on the SchedulerTriggerBuilder, then a random,
    * unique TriggerKey will be generated.
    * </p>
    * 
    * @param triggerKey the TriggerKey for the Trigger to be built
    * @return the updated SchedulerTriggerBuilder
    * @see TriggerKey
    * @see Trigger#getKey()
    */
   public SchedulerTriggerBuilder<T> withIdentity(TriggerKey triggerKey) {
      this.key = triggerKey;
      return this;
   }

   /**
    * Set the given (human-meaningful) description of the Trigger.
    * 
    * @param triggerDescription the description for the Trigger
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getDescription()
    */
   public SchedulerTriggerBuilder<T> withDescription(String triggerDescription) {
      this.description = triggerDescription;
      return this;
   }

   /**
    * Set the Trigger's priority. When more than one Trigger have the same fire time, the scheduler
    * will fire the one with the highest priority first.
    * 
    * @param triggerPriority the priority for the Trigger
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#DEFAULT_PRIORITY
    * @see Trigger#getPriority()
    */
   public SchedulerTriggerBuilder<T> withPriority(int triggerPriority) {
      this.priority = triggerPriority;
      return this;
   }

   /**
    * Set the name of the {@link Calendar} that should be applied to this Trigger's schedule.
    * 
    * @param calName the name of the Calendar to reference.
    * @return the updated SchedulerTriggerBuilder
    * @see Calendar
    * @see Trigger#getCalendarName()
    */
   public SchedulerTriggerBuilder<T> modifiedByCalendar(String calName) {
      this.calendarName = calName;
      return this;
   }

   /**
    * Set the time the Trigger should start at - the trigger may or may not fire at this time -
    * depending upon the schedule configured for the Trigger. However the Trigger will NOT fire
    * before this time, regardless of the Trigger's schedule.
    * 
    * @param triggerStartTime the start time for the Trigger.
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getStartTime()
    * @see DateBuilder
    */
   public SchedulerTriggerBuilder<T> startAt(Date triggerStartTime) {
      this.startTime = triggerStartTime;
      return this;
   }

   /**
    * Set the time the Trigger should start at to the current moment - the trigger may or may not
    * fire at this time - depending upon the schedule configured for the Trigger.
    * 
    * If Start Date is null then before triggering the start date is set to current date
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getStartTime()
    */
   public SchedulerTriggerBuilder<T> startNow() {
      this.startTime = new Date();
      return this;
   }

   /**
    * Set the time at which the Trigger will no longer fire - even if it's schedule has remaining
    * repeats.
    * 
    * @param triggerEndTime the end time for the Trigger. If null, the end time is indefinite.
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getEndTime()
    * @see DateBuilder
    */
   public SchedulerTriggerBuilder<T> endAt(Date triggerEndTime) {
      this.endTime = triggerEndTime;
      return this;
   }

   /**
    * Set the {@link ScheduleBuilder} that will be used to define the Trigger's schedule.
    * 
    * <p>
    * The particular <code>SchedulerBuilder</code> used will dictate the concrete type of Trigger
    * that is produced by the SchedulerTriggerBuilder.
    * </p>
    * 
    * @param schedBuilder the SchedulerBuilder to use.
    * @return the updated SchedulerTriggerBuilder
    * @see ScheduleBuilder
    * @see SimpleScheduleBuilder
    * @see CronScheduleBuilder
    * @see CalendarIntervalScheduleBuilder
    */
   @SuppressWarnings("unchecked")
   public <SBT extends T> SchedulerTriggerBuilder<SBT> withSchedule(Builder<SBT> schedBuilder) {
      this.scheduleBuilder = schedBuilder;
      return (SchedulerTriggerBuilder<SBT>) this;
   }

   /**
    * Set the identity of the Job which should be fired by the produced Trigger.
    * 
    * @param keyOfJobToFire the identity of the Job to fire.
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobKey()
    */
   public SchedulerTriggerBuilder<T> forJob(JobKey keyOfJobToFire) {
      this.jobKey = keyOfJobToFire;
      return this;
   }

   /**
    * Set the identity of the Job which should be fired by the produced Trigger - a
    * <code>JobKey</code> will be produced with the given name and default group.
    * 
    * @param jobName the name of the job (in default group) to fire.
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobKey()
    */
   public SchedulerTriggerBuilder<T> forJob(String jobName) {
      this.jobKey = new JobKey(jobName);
      return this;
   }

   /**
    * Set the identity of the Job which should be fired by the produced Trigger - a
    * <code>JobKey</code> will be produced with the given name and group.
    * 
    * @param jobName the name of the job to fire.
    * @param jobGroup the group of the job to fire.
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobKey()
    */
   public SchedulerTriggerBuilder<T> forJob(String jobName, String jobGroup) {
      this.jobKey = new JobKey(jobName, jobGroup);
      return this;
   }

   /**
    * Set the identity of the Job which should be fired by the produced Trigger, by extracting the
    * JobKey from the given job.
    * 
    * @param jobDetail the Job to fire.
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobKey()
    */
   public SchedulerTriggerBuilder<T> forJob(JobDetails<Object, Object> jobDetail) {
      JobKey k = jobDetail.getJobKey();
      if (k.getName() == null)
         throw new IllegalArgumentException("The given job has not yet had a name assigned to it.");
      this.jobKey = k;
      return this;
   }

   /**
    * Add the given key-value pair to the Trigger's {@link JobDataMap}.
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobDataMap()
    */
   public SchedulerTriggerBuilder<T> usingJobData(String dataKey, String value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the Trigger's {@link JobDataMap}.
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobDataMap()
    */
   public SchedulerTriggerBuilder<T> usingJobData(String dataKey, Integer value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the Trigger's {@link JobDataMap}.
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobDataMap()
    */
   public SchedulerTriggerBuilder<T> usingJobData(String dataKey, Long value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the Trigger's {@link JobDataMap}.
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobDataMap()
    */
   public SchedulerTriggerBuilder<T> usingJobData(String dataKey, Float value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the Trigger's {@link JobDataMap}.
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobDataMap()
    */
   public SchedulerTriggerBuilder<T> usingJobData(String dataKey, Double value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Add the given key-value pair to the Trigger's {@link JobDataMap}.
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobDataMap()
    */
   public SchedulerTriggerBuilder<T> usingJobData(String dataKey, Boolean value) {
      jobDataMap.put(dataKey, value);
      return this;
   }

   /**
    * Set the Trigger's {@link JobDataMap}, adding any values to it that were already set on this
    * SchedulerTriggerBuilder using any of the other 'usingJobData' methods.
    * 
    * @return the updated SchedulerTriggerBuilder
    * @see Trigger#getJobDataMap()
    */
   public SchedulerTriggerBuilder<T> usingJobData(Map<Object, Object> newJobDataMap) {
      // add any existing data to this new map
      for (Object dataKey : jobDataMap.keySet()) {
         newJobDataMap.put(dataKey, jobDataMap.get((String) dataKey));
      }
      jobDataMap = newJobDataMap; // set new map as the map to use
      return this;
   }

}