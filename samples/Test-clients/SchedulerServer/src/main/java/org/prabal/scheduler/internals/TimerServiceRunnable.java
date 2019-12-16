/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.internals.processorpolicy.TaskProcessorPolicy;
import org.prabal.scheduler.internals.processorpolicy.TaskProcessorPolicySelector;
import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.util.Base;

/**
 * @author Prabal Nandi
 *
 */
public class TimerServiceRunnable implements Runnable {
   private ConfigurableCacheFactory cacheFactory;
   private CoherenceJobStore coherenceJobStore;
   private int restartTryCount;

   private static final int NO_OF_RESTART_TRIES = 5;

   public TimerServiceRunnable(ConfigurableCacheFactory cacheFactory) {
      this.cacheFactory = cacheFactory;
      this.restartTryCount = NO_OF_RESTART_TRIES;
      this.coherenceJobStore = new CoherenceJobStore(cacheFactory);
   }

   @Override
   public void run() {
      SchedulerServiceConfig schedulerServiceConfig = this.cacheFactory.getResourceRegistry().getResource(SchedulerServiceConfig.class, SchedulerNamespaceHandler.RESOURCE_SCHEDULER_CONFIG);
      long networkTimeBuffer = Long.parseLong(SchedulerContantsEnum.NETWORK_TIME_BUFFER.getConstantValue());
      int batchSize = Integer.parseInt(SchedulerContantsEnum.MAX_BATCH_SIZE.getConstantValue());

      TaskProcessorPolicy taskProcessorPolicy = TaskProcessorPolicySelector.getTaskProcessorPolicy(this.cacheFactory);
      TriggerListHolder triggerListHolder = null;
      List<Trigger> triggeredSuccessfully = null;
      while (!Thread.currentThread().isInterrupted()) {
         try {
            Date startTime = ClusterInfoUtil.getCurrentClusterTime();
            coherenceJobStore.prepareTriggerInstance(startTime);

            Date upperLimitDate = new Date(startTime.getTime() + schedulerServiceConfig.getIdleWaitTime());
            triggerListHolder = coherenceJobStore.acquireNextTrigger(upperLimitDate, batchSize);
            System.out.println("Acquire Trigger. size " + triggerListHolder.size());
            if (!triggerListHolder.isEmpty()) {
               Map<TriggerKey, JobExecutionPayload> jobExecutionPayloadMap = coherenceJobStore.getJobExecutionPayloadList(triggerListHolder);
               if (jobExecutionPayloadMap != null) {

                  List<Trigger> sortedTriggerList = triggerListHolder.getSortedTriggerList();
                  triggeredSuccessfully = new ArrayList<Trigger>(sortedTriggerList.size());
                  for (Trigger triggerToExecute : sortedTriggerList) {
                     JobExecutionPayload executionPayload = jobExecutionPayloadMap.get(triggerToExecute.getKey());
                     TaskProcessorInstance taskProcessorInstance = taskProcessorPolicy.getNextAvailableProcessor();
                     if (executionPayload != null && taskProcessorInstance != null) {
                        taskProcessorInstance.enqueuePayload(executionPayload);
                        triggeredSuccessfully.add(triggerToExecute);
                     }
                  }
               }
            }
            long timeElapsed = ClusterInfoUtil.getCurrentClusterTime().getTime() - startTime.getTime();
            long waittime = schedulerServiceConfig.getIdleWaitTime() - (timeElapsed + networkTimeBuffer);
            if (waittime > networkTimeBuffer) {
               Thread.sleep(waittime);
            }
            if (restartTryCount < NO_OF_RESTART_TRIES) {
               restartTryCount = NO_OF_RESTART_TRIES;
               // resetting the restart try count
            }
         }
         catch (Exception exception) {
            handleException(exception, triggerListHolder, triggeredSuccessfully);
         }

      }
   }

   private void handleException(Exception exception, TriggerListHolder triggerListHolder, List<Trigger> triggeredSuccessfully) {
      if (Thread.currentThread().isInterrupted()) {
         CacheFactory.log("Stop is requested. TimeThread is shutting down", CacheFactory.LOG_DEBUG);
         return;
      }
      else {
         System.out.println("Removing. size " + triggerListHolder.size() + " success size " + triggeredSuccessfully.size());
         if (triggerListHolder != null) {
            triggerListHolder.removeAllTriggerFromList(triggeredSuccessfully);
            coherenceJobStore.updateStatusForFailure(triggerListHolder);
         }
         if ((--restartTryCount) <= 0) {
            CacheFactory.log("All trials failed to restart the TimeThread. Closing this instance of thread. Exception details = " + exception.getMessage() + " triggerHolderList " + triggerListHolder,
                  CacheFactory.LOG_ERR);
            throw Base.ensureRuntimeException(exception);
         }
         exception.printStackTrace();
         CacheFactory.log("Restart Try Number = " + restartTryCount + ". Unexpected exception occured at TimerThread. Message " + exception.getMessage() + " triggerHolderList " + triggerListHolder,
               CacheFactory.LOG_ERR);
      }
   }
}
