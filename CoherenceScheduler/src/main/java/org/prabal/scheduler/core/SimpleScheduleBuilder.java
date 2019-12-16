package org.prabal.scheduler.core;

import org.prabal.scheduler.triggers.SimpleTrigger;
import org.prabal.scheduler.util.DateBuilder;

import com.tangosol.util.Builder;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */

public class SimpleScheduleBuilder implements Builder<SimpleTrigger> {

   private long interval = 0;
   private int repeatCount = 0;
   private int misfireInstruction = Trigger.MISFIRE_INSTRUCTION_SMART_POLICY;

   protected SimpleScheduleBuilder() {
   }

   public static SimpleScheduleBuilder simpleSchedule() {
      return new SimpleScheduleBuilder();
   }

   public static SimpleScheduleBuilder repeatMinutelyForever() {
      return simpleSchedule().withIntervalInMinutes(1).repeatForever();
   }

   public static SimpleScheduleBuilder repeatMinutelyForever(int minutes) {
      return simpleSchedule().withIntervalInMinutes(minutes).repeatForever();
   }

   public static SimpleScheduleBuilder repeatSecondlyForever() {
      return simpleSchedule().withIntervalInSeconds(1).repeatForever();
   }

   public static SimpleScheduleBuilder repeatSecondlyForever(int seconds) {
      return simpleSchedule().withIntervalInSeconds(seconds).repeatForever();
   }

   public static SimpleScheduleBuilder repeatHourlyForever() {
      return simpleSchedule().withIntervalInHours(1).repeatForever();
   }

   public static SimpleScheduleBuilder repeatHourlyForever(int hours) {
      return simpleSchedule().withIntervalInHours(hours).repeatForever();
   }

   public static SimpleScheduleBuilder repeatMinutelyForTotalCount(int count) {
      if (count < 1)
         throw new IllegalArgumentException("Total count of firings must be at least one! Given count: " + count);
      return simpleSchedule().withIntervalInMinutes(1).withRepeatCount(count - 1);
   }

   public static SimpleScheduleBuilder repeatMinutelyForTotalCount(int count, int minutes) {
      if (count < 1)
         throw new IllegalArgumentException("Total count of firings must be at least one! Given count: " + count);
      return simpleSchedule().withIntervalInMinutes(minutes).withRepeatCount(count - 1);
   }

   public static SimpleScheduleBuilder repeatSecondlyForTotalCount(int count) {
      if (count < 1)
         throw new IllegalArgumentException("Total count of firings must be at least one! Given count: " + count);
      return simpleSchedule().withIntervalInSeconds(1).withRepeatCount(count - 1);
   }

   public static SimpleScheduleBuilder repeatSecondlyForTotalCount(int count, int seconds) {
      if (count < 1)
         throw new IllegalArgumentException("Total count of firings must be at least one! Given count: " + count);
      return simpleSchedule().withIntervalInSeconds(seconds).withRepeatCount(count - 1);
   }

   public static SimpleScheduleBuilder repeatHourlyForTotalCount(int count) {
      if (count < 1)
         throw new IllegalArgumentException("Total count of firings must be at least one! Given count: " + count);
      return simpleSchedule().withIntervalInHours(1).withRepeatCount(count - 1);
   }

   public static SimpleScheduleBuilder repeatHourlyForTotalCount(int count, int hours) {
      if (count < 1)
         throw new IllegalArgumentException("Total count of firings must be at least one! Given count: " + count);
      return simpleSchedule().withIntervalInHours(hours).withRepeatCount(count - 1);
   }

   public SimpleScheduleBuilder withIntervalInMilliseconds(long intervalInMillis) {
      this.interval = intervalInMillis;
      return this;
   }

   public SimpleScheduleBuilder withIntervalInSeconds(int intervalInSeconds) {
      this.interval = intervalInSeconds * 1000L;
      return this;
   }

   public SimpleScheduleBuilder withIntervalInMinutes(int intervalInMinutes) {
      this.interval = intervalInMinutes * DateBuilder.MILLISECONDS_IN_MINUTE;
      return this;
   }

   public SimpleScheduleBuilder withIntervalInHours(int intervalInHours) {
      this.interval = intervalInHours * DateBuilder.MILLISECONDS_IN_HOUR;
      return this;
   }

   public SimpleScheduleBuilder withRepeatCount(int triggerRepeatCount) {
      this.repeatCount = triggerRepeatCount;
      return this;
   }

   public SimpleScheduleBuilder repeatForever() {
      this.repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
      return this;
   }

   public SimpleScheduleBuilder withMisfireHandlingInstructionIgnoreMisfires() {
      misfireInstruction = Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY;
      return this;
   }

   public SimpleScheduleBuilder withMisfireHandlingInstructionFireNow() {
      misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW;
      return this;
   }

   public SimpleScheduleBuilder withMisfireHandlingInstructionNextWithExistingCount() {
      misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT;
      return this;
   }

   public SimpleScheduleBuilder withMisfireHandlingInstructionNextWithRemainingCount() {
      misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT;
      return this;
   }

   public SimpleScheduleBuilder withMisfireHandlingInstructionNowWithExistingCount() {
      misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
      return this;
   }

   public SimpleScheduleBuilder withMisfireHandlingInstructionNowWithRemainingCount() {
      misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;
      return this;
   }

   @Override
   public SimpleTrigger realize() {
      SimpleTrigger st = new SimpleTrigger();
      if (repeatCount != 0 && interval < 1) {
         throw new WrapperException("Repeat Interval cannot be zero with Repeat forever");
      }
      st.setRepeatInterval(interval);
      st.setRepeatCount(repeatCount);
      st.setMisfireInstruction(misfireInstruction);
      return st;
   }

}
