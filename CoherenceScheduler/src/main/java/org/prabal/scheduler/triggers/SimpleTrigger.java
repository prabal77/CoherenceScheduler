/**
 * 
 */
package org.prabal.scheduler.triggers;

import java.util.Date;

import org.prabal.scheduler.core.BaseCoherenceCalendar;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class SimpleTrigger extends AbstractTrigger {
   @PortableProperty(20)
   private Date startTime = null;
   @PortableProperty(21)
   private Date endTime = null;
   @PortableProperty(22)
   private Date nextFireTime = null;
   @PortableProperty(23)
   private Date previousFireTime = null;
   @PortableProperty(24)
   private int repeatCount = 0;
   @PortableProperty(25)
   private long repeatInterval = 0;
   @PortableProperty(26)
   private int timesTriggered = 0;
   @PortableProperty(27)
   private boolean complete = false;
   private static final int YEAR_TO_GIVEUP_SCHEDULING_AT = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 100;

   public SimpleTrigger() {
      super();
   }

   @Override
   public Date getStartTime() {
      return startTime;
   }

   @Override
   public void setStartTime(Date startTime) {
      if (startTime == null) {
         throw new IllegalArgumentException("Start time cannot be null");
      }
      Date eTime = getEndTime();
      if (eTime != null && startTime != null && eTime.before(startTime)) {
         throw new IllegalArgumentException("End time cannot be before start time");
      }
      this.startTime = startTime;
   }

   @Override
   public Date getEndTime() {
      return endTime;
   }

   @Override
   public void setEndTime(Date endTime) {
      Date sTime = getStartTime();
      if (sTime != null && endTime != null && sTime.after(endTime)) {
         throw new IllegalArgumentException("End time cannot be before start time");
      }
      this.endTime = endTime;
   }

   public int getRepeatCount() {
      return repeatCount;
   }

   public void setRepeatCount(int repeatCount) {
      if (repeatCount < 0 && repeatCount != REPEAT_INDEFINITELY) {
         throw new IllegalArgumentException("Repeat count must be >= 0, use the " + "constant REPEAT_INDEFINITELY for infinite.");
      }
      this.repeatCount = repeatCount;
   }

   public long getRepeatInterval() {
      return repeatInterval;
   }

   public void setRepeatInterval(long repeatInterval) {
      if (repeatInterval < 0) {
         throw new IllegalArgumentException("Repeat interval must be >= 0");
      }
      this.repeatInterval = repeatInterval;
   }

   public int getTimesTriggered() {
      return timesTriggered;
   }

   public void setTimesTriggered(int timesTriggered) {
      this.timesTriggered = timesTriggered;
   }

   @Override
   protected boolean validateMisfireInstruction(int misfireInstruction) {
      if (misfireInstruction < MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
         return false;
      }
      if (misfireInstruction > MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) {
         return false;
      }
      return true;
   }

   @Override
   public void updateAfterMisfire(BaseCoherenceCalendar cal, Date currentDate) {
      int instr = getMisfireInstruction();

      if (instr == MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY)
         return;

      if (instr == MISFIRE_INSTRUCTION_SMART_POLICY) {
         switch (getRepeatCount()) {
            case 0:
               instr = MISFIRE_INSTRUCTION_FIRE_NOW;
               break;
            case REPEAT_INDEFINITELY:
               instr = MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT;
               break;
            default:
               instr = MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
               break;
         }
      }
      else if (instr == MISFIRE_INSTRUCTION_FIRE_NOW && getRepeatCount() != 0) {
         instr = MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;
      }

      if (instr == MISFIRE_INSTRUCTION_FIRE_NOW) {
         setNextFireTime(currentDate);
      }
      else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) {
         Date newFireTime = getFireTimeAfter(currentDate);
         while (newFireTime != null && cal != null && !cal.isTimeIncluded(newFireTime.getTime())) {
            newFireTime = getFireTimeAfter(newFireTime);

            if (newFireTime == null)
               break;

            // avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(newFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
               newFireTime = null;
            }
         }
         setNextFireTime(newFireTime);
      }
      else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT) {
         Date newFireTime = getFireTimeAfter(currentDate);
         while (newFireTime != null && cal != null && !cal.isTimeIncluded(newFireTime.getTime())) {
            newFireTime = getFireTimeAfter(newFireTime);

            if (newFireTime == null)
               break;

            // avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(newFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
               newFireTime = null;
            }
         }
         if (newFireTime != null) {
            int timesMissed = computeNumTimesFiredBetween(nextFireTime, newFireTime);
            setTimesTriggered(getTimesTriggered() + timesMissed);
         }
         setNextFireTime(newFireTime);
      }
      else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT) {
         Date newFireTime = currentDate;
         if (repeatCount != 0 && repeatCount != REPEAT_INDEFINITELY) {
            setRepeatCount(getRepeatCount() - getTimesTriggered());
            setTimesTriggered(0);
         }

         if (getEndTime() != null && getEndTime().before(newFireTime)) {
            setNextFireTime(null); // We are past the end time
         }
         else {
            setStartTime(newFireTime);
            setNextFireTime(newFireTime);
         }
      }
      else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT) {
         Date newFireTime = currentDate;

         int timesMissed = computeNumTimesFiredBetween(nextFireTime, newFireTime);

         if (repeatCount != 0 && repeatCount != REPEAT_INDEFINITELY) {
            int remainingCount = getRepeatCount() - (getTimesTriggered() + timesMissed);
            if (remainingCount <= 0) {
               remainingCount = 0;
            }
            setRepeatCount(remainingCount);
            setTimesTriggered(0);
         }

         if (getEndTime() != null && getEndTime().before(newFireTime)) {
            setNextFireTime(null); // We are past the end time
         }
         else {
            setStartTime(newFireTime);
            setNextFireTime(newFireTime);
         }
      }
   }

   @Override
   public void triggered(BaseCoherenceCalendar calendar) {
      timesTriggered++;
      previousFireTime = nextFireTime;
      nextFireTime = getFireTimeAfter(nextFireTime);
      while (nextFireTime != null && calendar != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {
         nextFireTime = getFireTimeAfter(nextFireTime);
         if (nextFireTime == null)
            break;

         // avoid infinite loop
         java.util.Calendar c = java.util.Calendar.getInstance();
         c.setTime(nextFireTime);
         if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
            nextFireTime = null;
         }
      }
   }

   @Override
   public void updateWithNewCalendar(BaseCoherenceCalendar calendar, long misfireThreshold) {
      nextFireTime = getFireTimeAfter(previousFireTime);

      if (nextFireTime == null || calendar == null) {
         return;
      }
      Date now = new Date();
      while (nextFireTime != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {
         nextFireTime = getFireTimeAfter(nextFireTime);
         if (nextFireTime == null)
            break;

         // avoid infinite loop
         java.util.Calendar c = java.util.Calendar.getInstance();
         c.setTime(nextFireTime);
         if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
            nextFireTime = null;
         }
         if (nextFireTime != null && nextFireTime.before(now)) {
            long diff = now.getTime() - nextFireTime.getTime();
            if (diff >= misfireThreshold) {
               nextFireTime = getFireTimeAfter(nextFireTime);
            }
         }
      }
   }

   @Override
   public Date computeFirstFireTime(BaseCoherenceCalendar calendar) {
      nextFireTime = getStartTime();

      while (nextFireTime != null && calendar != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {
         nextFireTime = getFireTimeAfter(nextFireTime);

         if (nextFireTime == null)
            break;

         // avoid infinite loop
         java.util.Calendar c = java.util.Calendar.getInstance();
         c.setTime(nextFireTime);
         if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
            return null;
         }
      }
      return nextFireTime;
   }

   @Override
   public Date getNextFireTime() {
      return nextFireTime;
   }

   @Override
   public Date getPreviousFireTime() {
      return previousFireTime;
   }

   public void setNextFireTime(Date nextFireTime) {
      this.nextFireTime = nextFireTime;
   }

   public void setPreviousFireTime(Date previousFireTime) {
      this.previousFireTime = previousFireTime;
   }

   @Override
   public Date getFireTimeAfter(Date afterTime) {
      if (complete) {
         return null;
      }
      if ((timesTriggered >= repeatCount) && (repeatCount != REPEAT_INDEFINITELY)) {
         return null;
      }
      if (afterTime == null) {
         afterTime = new Date();
      }
      if (repeatCount == 0 && afterTime.compareTo(getStartTime()) >= 0) {
         return null;
      }
      long startMillis = getStartTime().getTime();
      long afterMillis = afterTime.getTime();
      long endMillis = (getEndTime() == null) ? Long.MAX_VALUE : getEndTime().getTime();

      if (endMillis <= afterMillis) {
         return null;
      }
      if (afterMillis < startMillis) {
         return new Date(startMillis);
      }

      long numberOfTimesExecuted = ((afterMillis - startMillis) / repeatInterval) + 1;
      if ((numberOfTimesExecuted > repeatCount) && (repeatCount != REPEAT_INDEFINITELY)) {
         return null;
      }
      Date time = new Date(startMillis + (numberOfTimesExecuted * repeatInterval));
      if (endMillis <= time.getTime()) {
         return null;
      }
      return time;
   }

   public Date getFireTimeBefore(Date end) {
      if (end.getTime() < getStartTime().getTime()) {
         return null;
      }
      int numFires = computeNumTimesFiredBetween(getStartTime(), end);
      return new Date(getStartTime().getTime() + (numFires * repeatInterval));
   }

   public int computeNumTimesFiredBetween(Date start, Date end) {
      if (repeatInterval < 1) {
         return 0;
      }
      long time = end.getTime() - start.getTime();
      return (int) (time / repeatInterval);
   }

   @Override
   public Date getFinalFireTime() {
      if (repeatCount == 0) {
         return startTime;
      }
      if (repeatCount == REPEAT_INDEFINITELY) {
         return (getEndTime() == null) ? null : getFireTimeBefore(getEndTime());
      }
      long lastTrigger = startTime.getTime() + (repeatCount * repeatInterval);
      if ((getEndTime() == null) || (lastTrigger < getEndTime().getTime())) {
         return new Date(lastTrigger);
      }
      else {
         return getFireTimeBefore(getEndTime());
      }
   }

   @Override
   public boolean mayFireAgain() {
      return (getNextFireTime() != null);
   }

   @Override
   public void validate() throws WrapperException {
      super.validate();
      if (repeatCount != 0 && repeatInterval < 1) {
         throw new WrapperException("Repeat Interval cannot be zero.");
      }
   }

   public boolean hasAdditionalProperties() {
      return false;
   }

   @Override
   public String toString() {
      return "SimpleTrigger [startTime=" + startTime + ", endTime=" + endTime + ", nextFireTime=" + nextFireTime + ", previousFireTime=" + previousFireTime + ", repeatCount=" + repeatCount
            + ", repeatInterval=" + repeatInterval + ", timesTriggered=" + timesTriggered + ", complete=" + complete + ", getKey()=" + getKey() + ", getJobKey()=" + getJobKey()
            + ", getDescription()=" + getDescription() + ", getCalendarName()=" + getCalendarName() + ", getJobDataMap()=" + getJobDataMap() + ", getPriority()=" + getPriority()
            + ", getMisfireInstruction()=" + getMisfireInstruction() + ", getTriggerState()=" + getTriggerState() + ", getFireInstanceId()=" + getFireInstanceId() + "]";
   }

}
