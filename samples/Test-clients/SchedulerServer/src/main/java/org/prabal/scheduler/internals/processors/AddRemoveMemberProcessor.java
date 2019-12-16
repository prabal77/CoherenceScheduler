/**
 * 
 */
package org.prabal.scheduler.internals.processors;

import java.util.ArrayList;
import java.util.Collection;

import org.prabal.scheduler.internals.TaskProcessorKey;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * @author Prabal Nandi
 *
 */
@SuppressWarnings("serial")
@Portable
public class AddRemoveMemberProcessor extends AbstractProcessor {
   @PortableProperty(0)
   private String typeOfOperation;
   @PortableProperty(1)
   private TaskProcessorKey processorKey;

   public static final String ADD_PROCESSING_MEMBER = "ADD_MEMBER";
   public static final String DELETE_PROCESSING_MEMBER = "DELETE_MEMBER";

   public AddRemoveMemberProcessor() {
      super();
   }

   public AddRemoveMemberProcessor(String typeOfOperation, TaskProcessorKey processorKey) {
      this.typeOfOperation = typeOfOperation;
      this.processorKey = processorKey;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object process(Entry entry) {
      ArrayList<TaskProcessorKey> tempSet = new ArrayList<TaskProcessorKey>();
      Collection<TaskProcessorKey> collection = (Collection<TaskProcessorKey>) entry.getValue();
      if (collection != null)
         tempSet.addAll(collection);

      switch (this.typeOfOperation) {
         case ADD_PROCESSING_MEMBER: {
            tempSet.add(this.processorKey);
            break;
         }
         case DELETE_PROCESSING_MEMBER: {
            int currentIndex = tempSet.indexOf(this.processorKey);
            if (currentIndex == -1)
               break;
            TaskProcessorKey element = tempSet.get(currentIndex);

            if (element.isNextAvailableProcessor()) {
               int newAvailableIndex = 0;
               if (currentIndex == (tempSet.size() - 1)) {
                  newAvailableIndex = 0;
               }
               else {
                  newAvailableIndex = currentIndex + 1;
               }
               tempSet.get(newAvailableIndex).setNextAvailableProcessor(true);
            }
            tempSet.remove(currentIndex);
            break;
         }
         default: {
            break;
         }
      }
      entry.setValue(tempSet, true);
      return null;
   }
}
