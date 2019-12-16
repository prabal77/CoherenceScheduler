/**
 * 
 */
package org.prabal.scheduler.internals.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.prabal.scheduler.core.ExecutionStatus;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.pof.ExecutionStateCodec;
import org.prabal.scheduler.pof.ExecutionStatusListCodec;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.InvocableMap.EntryProcessor;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class UpdateTriggerStatusProcessor implements EntryProcessor {
   @PortableProperty(value = 0, codec = ExecutionStateCodec.class)
   private ExecutionStatus toStatus;
   @PortableProperty(value = 1, codec = ExecutionStatusListCodec.class)
   private List<ExecutionStatus> fromStatusList;

   public UpdateTriggerStatusProcessor() {
      super();
   }

   public UpdateTriggerStatusProcessor(List<ExecutionStatus> fromStatusList, ExecutionStatus toStatus) {
      super();
      this.fromStatusList = fromStatusList;
      this.toStatus = toStatus;
   }

   @Override
   public Object process(Entry entry) {
      Trigger trigger = (Trigger) entry.getValue();
      if (this.fromStatusList != null && !this.fromStatusList.isEmpty() && !this.fromStatusList.contains(trigger.getTriggerState())) {
         return null;
      }
      trigger.changeTriggerState(this.toStatus);
      entry.setValue(trigger, true);
      return true;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public Map processAll(Set entrySet) {
      Map<TriggerKey, Boolean> returnDataMap = new HashMap<TriggerKey, Boolean>();

      for (InvocableMap.Entry entry : (Set<InvocableMap.Entry>) entrySet) {
         if ((boolean) process(entry)) {
            returnDataMap.put((TriggerKey) entry.getKey(), true);
         }
      }
      return returnDataMap;
   }

}
