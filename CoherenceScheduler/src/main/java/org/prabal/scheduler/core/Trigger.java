/**
 * 
 */
package org.prabal.scheduler.core;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
public interface Trigger extends Comparable<Trigger> {

   public enum CompletedExecutionInstruction {
      NOOP, RE_EXECUTE_JOB, SET_TRIGGER_COMPLETE, DELETE_TRIGGER, SET_ALL_JOB_TRIGGERS_COMPLETE, SET_TRIGGER_ERROR, SET_ALL_JOB_TRIGGERS_ERROR
   }

   int MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY = -1;
   int MISFIRE_INSTRUCTION_SMART_POLICY = 0;
   int MISFIRE_INSTRUCTION_FIRE_NOW = 1;
   int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT = 2;
   int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT = 3;
   int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT = 4;
   int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT = 5;

   int REPEAT_INDEFINITELY = -1;
   int DEFAULT_PRIORITY = 5;

   public TriggerKey getKey();

   public JobKey getJobKey();

   public String getDescription();

   public String getCalendarName();

   public Map<Object, Object> getJobDataMap();

   public int getPriority();

   public boolean mayFireAgain();

   public Date getStartTime();

   public Date getEndTime();

   public Date getNextFireTime();

   public Date getPreviousFireTime();

   public Date getFireTimeAfter(Date afterTime);

   public Date getFinalFireTime();

   public int getMisfireInstruction();

   public void triggered(BaseCoherenceCalendar calendar);

   public Date computeFirstFireTime(BaseCoherenceCalendar calendar);

  // public CompletedExecutionInstruction executionComplete(JobContext environment, JobExecutionException result);

   public void updateAfterMisfire(BaseCoherenceCalendar cal, Date currentDate);

   public void updateWithNewCalendar(BaseCoherenceCalendar cal, long misfireThreshold);

   public void validate() throws WrapperException;

   public void setFireInstanceId(String id);

   public String getFireInstanceId();

   public void setStartTime(Date date);

   public void setEndTime(Date date);

   public void setNextFireTime(Date nextFireTime);

   public void setPreviousFireTime(Date previousFireTime);

   public boolean equals(Object other);

   public ExecutionStatus getTriggerState();

   public void changeTriggerState(ExecutionStatus triggerState);

   @Override
   public int compareTo(Trigger o);

   /**
    * A Comparator that compares trigger's next fire times, or in other words, sorts them according
    * to earliest next fire time. If the fire times are the same, then the triggers are sorted
    * according to priority (highest value first), if the priorities are the same, then they are
    * sorted by key.
    */
   @Portable
   class TriggerTimeComparator implements Comparator<Trigger> {

      // This static method exists for comparator in TC clustered quartz
      public static int compare(Date nextFireTime1, int priority1, TriggerKey key1, Date nextFireTime2, int priority2, TriggerKey key2) {
         if (nextFireTime1 != null || nextFireTime2 != null) {
            if (nextFireTime1 == null) {
               return 1;
            }

            if (nextFireTime2 == null) {
               return -1;
            }

            if (nextFireTime1.before(nextFireTime2)) {
               return -1;
            }

            if (nextFireTime1.after(nextFireTime2)) {
               return 1;
            }
         }

         int comp = priority2 - priority1;
         if (comp != 0) {
            return comp;
         }

         return key1.compareTo(key2);
      }

      public int compare(Trigger t1, Trigger t2) {
         return compare(t1.getNextFireTime(), t1.getPriority(), t1.getKey(), t2.getNextFireTime(), t2.getPriority(), t2.getKey());
      }
   }
}
