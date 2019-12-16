/**
 * 
 */
package org.prabal.scheduler.internals;

import org.prabal.scheduler.core.JobKey;
import org.prabal.scheduler.core.TriggerKey;

import com.tangosol.net.PartitionedService;
import com.tangosol.net.partition.KeyAssociator;

/**
 * @author Prabal Nandi
 *
 */
public class JobKeyAssociator implements KeyAssociator {

   @Override
   public Object getAssociatedKey(Object objectKey) {
      if (objectKey instanceof TriggerKey) {
         return ((TriggerKey) objectKey).getJobKey();
      }
      else if (objectKey instanceof JobKey) {
         return ((JobKey) objectKey);
      }
      return objectKey;
   }

   @Override
   public void init(PartitionedService service) {
   }

}