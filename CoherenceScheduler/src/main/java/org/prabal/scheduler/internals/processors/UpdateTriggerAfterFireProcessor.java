/**
 * 
 */
package org.prabal.scheduler.internals.processors;

import org.prabal.scheduler.core.BaseCoherenceCalendar;
import org.prabal.scheduler.core.ExecutionStatus;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.internals.SchedulerContantsEnum;

import com.tangosol.io.pof.annotation.Portable;
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
public class UpdateTriggerAfterFireProcessor extends AbstractProcessor {

   @Override
   public Object process(Entry entry) {
      Trigger triggerInstance = (Trigger) entry.getValue();
      ExecutionStatus executionStatus = ExecutionStatus.NORMAL;

      BaseCoherenceCalendar calendar = null;
      if (triggerInstance.getCalendarName() != null) {
         BackingMapContext backingMapContext = ((BinaryEntry) entry).getContext().getBackingMapContext(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue());
         InvocableMap.Entry calendarEntry = backingMapContext.getBackingMapEntry(triggerInstance.getCalendarName());
         if (calendarEntry.isPresent())
            calendar = (BaseCoherenceCalendar) calendarEntry.getValue();
      }
      triggerInstance.triggered(calendar);
      if (triggerInstance.getNextFireTime() == null) {
         executionStatus = ExecutionStatus.COMPLETE;
      }
      triggerInstance.changeTriggerState(executionStatus);
      entry.setValue(triggerInstance);
      return null;
   }

}
