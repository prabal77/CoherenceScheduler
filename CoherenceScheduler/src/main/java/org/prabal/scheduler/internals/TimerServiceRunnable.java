/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.internals.processorpolicy.TaskProcessorPolicy;
import org.prabal.scheduler.internals.processorpolicy.TaskProcessorPolicySelector;
import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;

/**
 * @author Prabal Nandi
 *
 */
public class TimerServiceRunnable implements Runnable {
   private ConfigurableCacheFactory cacheFactory;
   private final AtomicInteger restartTryCount;

   private static final int NO_OF_RESTART_TRIES = 5;

   public TimerServiceRunnable(ConfigurableCacheFactory cacheFactory) {
      this.cacheFactory = cacheFactory;
      this.restartTryCount = new AtomicInteger(NO_OF_RESTART_TRIES);
   }

   @Override
   public void run() {
      CoherenceJobStore coherenceJobStore = new CoherenceJobStore(cacheFactory);
      SchedulerServiceConfig schedulerServiceConfig = this.cacheFactory.getResourceRegistry().getResource(SchedulerServiceConfig.class, SchedulerNamespaceHandler.RESOURCE_SCHEDULER_CONFIG);
      long networkTimeBuffer = Long.parseLong(SchedulerContantsEnum.NETWORK_TIME_BUFFER.getConstantValue());
      int batchSize = Integer.parseInt(SchedulerContantsEnum.MAX_BATCH_SIZE.getConstantValue());

      TaskProcessorPolicy taskProcessorPolicy = TaskProcessorPolicySelector.getTaskProcessorPolicy(this.cacheFactory);
      while (!Thread.currentThread().isInterrupted()) {
         try {
            Date startTime = ClusterInfoUtil.getCurrentClusterTime();
            coherenceJobStore.prepareTriggerInstance(startTime);

            Date upperLimitDate = new Date(startTime.getTime() + schedulerServiceConfig.getIdleWaitTime());
            TriggerListHolder triggerListHolder = coherenceJobStore.acquireNextTrigger(upperLimitDate, batchSize);
            if (!triggerListHolder.isEmpty()) {
               Map<TriggerKey, JobExecutionPayload> jobExecutionPayloadMap = coherenceJobStore.getJobExecutionPayloadList(triggerListHolder);
               if (jobExecutionPayloadMap != null) {

                  List<Trigger> sortedTriggerList = triggerListHolder.getSortedTriggerList();
                  for (Trigger triggerToExecute : sortedTriggerList) {
                     JobExecutionPayload executionPayload = jobExecutionPayloadMap.get(triggerToExecute.getKey());
                     TaskProcessorInstance taskProcessorInstance = taskProcessorPolicy.getNextAvailableProcessor();
                     if (executionPayload != null && taskProcessorInstance != null) {
                        taskProcessorInstance.enqueuePayload(executionPayload);
                     }
                  }
               }
            }
            long timeElapsed = ClusterInfoUtil.getCurrentClusterTime().getTime() - startTime.getTime();
            long waittime = schedulerServiceConfig.getIdleWaitTime() - (timeElapsed + networkTimeBuffer);
            Thread.sleep(waittime);
            if (restartTryCount.get() < NO_OF_RESTART_TRIES) {
               restartTryCount.set(NO_OF_RESTART_TRIES);
               // resetting the restart try count
            }
         }
         catch (Exception exception) {
            if (Thread.currentThread().isInterrupted()) {
               CacheFactory.log("Stop is requested. TimeThread is shutting down", CacheFactory.LOG_DEBUG);
               return;
            }
            else {
               if (restartTryCount.getAndDecrement() <= 0) {
                  CacheFactory.log("All tries failed to restart the TimeThread. Closing this instance of thread. Exception details = " + exception.getMessage(), CacheFactory.LOG_ERR);
                  return;
               }
               CacheFactory.log("Restart Try Number =" + restartTryCount.get() + ". Unexpected exception occured at TimerThread. Message " + exception.getMessage(), CacheFactory.LOG_ERR);
            }
         }

      }
   }
}
