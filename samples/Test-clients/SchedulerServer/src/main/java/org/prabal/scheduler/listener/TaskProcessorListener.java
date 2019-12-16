/**
 * 
 */
package org.prabal.scheduler.listener;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.prabal.scheduler.core.ExecutionStatus;
import org.prabal.scheduler.core.ResultInstanceHolder;
import org.prabal.scheduler.core.ResultInstanceKey;
import org.prabal.scheduler.core.SubmissionResult;
import org.prabal.scheduler.internals.JobExecutionPayload;
import org.prabal.scheduler.internals.JobExecutionShell;
import org.prabal.scheduler.internals.SchedulerContantsEnum;
import org.prabal.scheduler.internals.TaskProcessorInstance;
import org.prabal.scheduler.internals.processors.UpdateTriggerAfterFireProcessor;
import org.prabal.scheduler.internals.processors.UpdateTriggerStatusProcessor;

import com.oracle.coherence.common.util.ObjectProxyFactory;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.DaemonThreadFactory;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

/**
 * @author Prabal Nandi
 *
 */
public class TaskProcessorListener<T> implements MapListener {
   private final JobExecutionShell<T> jobExecutionShell;
   private final TaskProcessorInstance taskProcessorInstanceProxy;
   private final DelayQueue<JobExecutionPayload> jobFeederQueue;
   private final ExecutorService jobResultFeederService;
   private final NamedCache triggerStoreCache;
   private final AtomicBoolean startedFlag = new AtomicBoolean(true);

   public TaskProcessorListener(JobExecutionShell<T> jobExecutionShell, TaskProcessorInstance taskProcessorInstanceProxy, NamedCache triggerStoreCache) {
      this.jobExecutionShell = jobExecutionShell;
      this.taskProcessorInstanceProxy = taskProcessorInstanceProxy;
      this.jobFeederQueue = new DelayQueue<JobExecutionPayload>();
      this.jobResultFeederService = Executors.newFixedThreadPool(2, new DaemonThreadFactory("CoherenceScheduler:TaskProcessor - "));
      this.triggerStoreCache = triggerStoreCache;
      initializeJobFeeder();
      initializeResultFeeder();
   }

   private void initializeJobFeeder() {
      this.jobResultFeederService.submit(new Runnable() {
         @Override
         public void run() {
            while (startedFlag.get()) {
               try {
                  JobExecutionPayload payload = jobFeederQueue.take();
                  jobExecutionShell.submitJob(payload);
                  triggerStoreCache.invoke(payload.getTigger().getKey(), new UpdateTriggerAfterFireProcessor());
               }
               catch (InterruptedException noActionExceptions) {
                  // No Action Required.
               }
            }
         }
      });
   }

   private void initializeResultFeeder() {
      this.jobResultFeederService.submit(new Runnable() {

         @Override
         public void run() {
            while (startedFlag.get()) {
               try {
                  ResultInstanceHolder<T> resultInstanceHolder = jobExecutionShell.getResultInstance();
                  if (resultInstanceHolder != null && resultInstanceHolder.getTriggerKey() != null) {
                     ResultInstanceKey instanceKey = new ResultInstanceKey(resultInstanceHolder.getTriggerKey());
                     SubmissionResult<T> submissionResultProxy = null;

                     ObjectProxyFactory<SubmissionResult> objectProxyFactory = new ObjectProxyFactory<SubmissionResult>(SchedulerContantsEnum.RESULT_STORE_CACHE.getConstantValue(),
                           SubmissionResult.class);
                     submissionResultProxy = objectProxyFactory.getProxy(instanceKey);
                     submissionResultProxy.addResultInstance(resultInstanceHolder);
                  }
               }
               catch (InterruptedException | ExecutionException noActionExceptions) {
                  // No Action Required.
               }
            }
         }
      });
   }

   public void shutDownServices(boolean force) {
      try {
         this.startedFlag.compareAndSet(true, false);
         this.jobResultFeederService.shutdownNow();
      }
      catch (Exception exception) {
         CacheFactory.log("Error while stopping Job and Result Feeder executorservice inside TaskProcessoreListner. " + exception.getMessage(), CacheFactory.LOG_ERR);
      }
      CacheFactory.log("TaskProcessor's services are shutdown", CacheFactory.LOG_DEBUG);
   }

   @Override
   public void entryDeleted(MapEvent event) {
   }

   @Override
   public void entryInserted(MapEvent event) {
   }

   @Override
   public void entryUpdated(MapEvent event) {
      if (this.jobExecutionShell.isStartedFlag()) {
         JobExecutionPayload payload = taskProcessorInstanceProxy.fetchNextPayload();
         if (payload != null && this.jobExecutionShell.getSemaphore().tryAcquire()) {
            this.jobFeederQueue.offer(payload);
            return;
         }
         else {
            if (payload != null) {
               this.triggerStoreCache.invoke(payload.getTigger().getKey(), new UpdateTriggerStatusProcessor(null, ExecutionStatus.REJECTED));
            }
         }
      }
      // Reject the Payload
   }

}