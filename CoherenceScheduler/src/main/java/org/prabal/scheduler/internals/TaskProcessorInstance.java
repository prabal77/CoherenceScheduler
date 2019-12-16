/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.List;

import com.oracle.coherence.common.util.ChangeIndication;
import com.tangosol.io.pof.PortableObject;

/**
 * @author Prabal Nandi
 *
 */
public interface TaskProcessorInstance extends PortableObject, ChangeIndication {

   public boolean enqueuePayload(JobExecutionPayload payload);

   public JobExecutionPayload fetchNextPayload();

   public List<JobExecutionPayload> drainAllPayLoad();

   public void clearPendingListWithoutProcessing();

   public TaskProcessorKey getTaskProcessorKey();

}