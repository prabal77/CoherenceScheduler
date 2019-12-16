/**
 * 
 */
package org.prabal.scheduler.internals.processors;

import java.util.Date;

import org.prabal.scheduler.core.BaseCoherenceCalendar;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.internals.SchedulerContantsEnum;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.tangosol.net.BackingMapContext;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class ApplyMisFireTrigger extends AbstractProcessor {
   @PortableProperty(0)
   private long misfireThreshold = 5000l;

   public ApplyMisFireTrigger() {
      super();
   }

   public ApplyMisFireTrigger(long misfireThreshold) {
      super();
      this.misfireThreshold = misfireThreshold;
   }

   @Override
   public Object process(Entry entry) {
      long currentTimeinMilis = ((BinaryEntry) entry).getContext().getCacheService().getCluster().getTimeMillis();
      long misfireTime = currentTimeinMilis - misfireThreshold;
      applyMisFireInfo(currentTimeinMilis, misfireTime, entry);

      Trigger triggerInstance = (Trigger) entry.getValue();
      if (triggerInstance.getNextFireTime() == null) {
         entry.remove(true);
      }
      return null;
   }

   private void applyMisFireInfo(long currentTime, long misfireTime, InvocableMap.Entry entry) {
      Trigger triggerInstance = (Trigger) entry.getValue();

      Date tnft = triggerInstance.getNextFireTime();
      if (tnft == null || tnft.getTime() > misfireTime || triggerInstance.getMisfireInstruction() == Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
         return;
      }
      BaseCoherenceCalendar calendar = null;
      if (triggerInstance.getCalendarName() != null) {
         BackingMapContext backingMapContext = ((BinaryEntry) entry).getContext().getBackingMapContext(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue());
         InvocableMap.Entry calendarEntry = backingMapContext.getBackingMapEntry(triggerInstance.getCalendarName());
         if (calendarEntry.isPresent())
            calendar = (BaseCoherenceCalendar) calendarEntry.getValue();
      }
      triggerInstance.updateAfterMisfire(calendar, new Date(currentTime));
      entry.setValue(triggerInstance);
      return;
   }

   public long getMisfireThreshold() {
      return misfireThreshold;
   }

   public void setMisfireThreshold(long misfireThreshold) {
      this.misfireThreshold = misfireThreshold;
   }

   @Override
   public String toString() {
      return "ApplyMisFireTrigger [misfireThreshold=" + misfireThreshold + "]";
   }

}
