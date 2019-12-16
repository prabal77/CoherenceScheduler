/**
 * 
 */
package org.prabal.scheduler.internals.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.prabal.scheduler.internals.TaskProcessorKey;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class FetchNextTaskProcessor extends AbstractProcessor {

   @Override
   public Object process(Entry entry) {
      ArrayList<TaskProcessorKey> taskInstanceList = new ArrayList<TaskProcessorKey>();
      taskInstanceList.addAll((Collection<TaskProcessorKey>) entry.getValue());
      TaskProcessorKey currentProcessor = null;
      if (taskInstanceList == null || taskInstanceList.isEmpty())
         return currentProcessor;

      for (int i = 0; i < taskInstanceList.size(); i++) {

         if (taskInstanceList.get(i).isNextAvailableProcessor()) {
            currentProcessor = taskInstanceList.get(i);
            taskInstanceList.get(i).setNextAvailableProcessor(false);
            if (i == (taskInstanceList.size() - 1)) {
               i = 0;
            }
            else {
               i += 1;
            }
            if (taskInstanceList.get(i) != null) {
               taskInstanceList.get(i).setNextAvailableProcessor(true);
            }
            break;
         }
      }
      if (currentProcessor == null && !taskInstanceList.isEmpty()) {
         currentProcessor = taskInstanceList.get(0);
         TaskProcessorKey nextEligibleProcessor = null;
         // Added security to avoid any unexpected error
         if (taskInstanceList.size() >= 2) {
            nextEligibleProcessor = taskInstanceList.get(1);
         }
         else {
            nextEligibleProcessor = taskInstanceList.get(0);
         }
         nextEligibleProcessor.setNextAvailableProcessor(true);
      }
      entry.setValue(taskInstanceList, true);
      return currentProcessor;
   }

   @Override
   public Map processAll(Set entrySet) {
      return null;
   }

}