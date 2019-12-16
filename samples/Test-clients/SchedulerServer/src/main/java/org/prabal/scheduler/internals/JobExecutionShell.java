/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.prabal.scheduler.core.Job;
import org.prabal.scheduler.core.ResultInstanceHolder;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;

import com.oracle.coherence.common.threading.ThreadFactories;
import com.tangosol.net.CacheFactory;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
public class JobExecutionShell<T> {
   private final ThreadPoolExecutor threadPool;
   private final ThreadGroup threadGroup;
   private final ExecutorCompletionService<ResultInstanceHolder<T>> completionService;
   private volatile boolean startedFlag = false;
   private volatile ReentrantLock lock = new ReentrantLock();
   private AtomicLong noOfTaskSubmited = new AtomicLong(0);
   private final Semaphore semaphore;

   public JobExecutionShell(SchedulerServiceConfig schedulerServiceConfig) {
      this.threadGroup = new ThreadGroup(SchedulerContantsEnum.SCHEDULDER_WORKER_THREAD_GROUP.getConstantValue());
      this.threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool(ThreadFactories.newThreadFactory(false, schedulerServiceConfig.getName(), threadGroup));
      this.completionService = new ExecutorCompletionService<ResultInstanceHolder<T>>(this.threadPool);
      this.semaphore = new Semaphore((schedulerServiceConfig.getMaxThreadCount() == 0) ? 5 : schedulerServiceConfig.getMaxThreadCount());
   }

   public void startShell() {
      if (startedFlag)
         return;
      lock.lock();
      try {
         // Check again
         if (startedFlag)
            return;
         this.threadPool.prestartAllCoreThreads();
         startedFlag = true;
      }
      finally {
         lock.unlock();
      }
      CacheFactory.log("Scheduler Started", CacheFactory.LOG_INFO);
   }

   public boolean shutDown(boolean force) {
      if (!startedFlag)
         return true;
      lock.lock();
      try {
         // Block until the payLoadQueue is not empty
         if (!force) {
            while (!this.threadPool.getQueue().isEmpty())
               ;
            this.threadPool.awaitTermination(Long.parseLong(SchedulerContantsEnum.DEFAULT_AWAIT_TIMEOUT.getConstantValue()), TimeUnit.MILLISECONDS);
         }
         else {
            this.threadPool.shutdownNow();
         }
      }
      catch (InterruptedException interruptedException) {
         CacheFactory.log("Shutdown waiting interrupted " + interruptedException.getMessage(), CacheFactory.LOG_WARN);
      }
      finally {
         this.startedFlag = false;
         lock.unlock();
      }
      CacheFactory.log("Scheduler Shutdown complete", CacheFactory.LOG_INFO);
      return true;
   }

   public void submitJob(JobExecutionPayload jobExecutionPayload) {
      if (lock.isLocked()) {
         throw new WrapperException("Shutdown is in progress. Can't submit job =" + jobExecutionPayload);
      }
      noOfTaskSubmited.incrementAndGet();
      this.completionService.submit(new JobRunnable(jobExecutionPayload));
   }

   private class JobRunnable implements Callable<ResultInstanceHolder<T>> {
      private JobExecutionPayload executionPayload;

      public JobRunnable(JobExecutionPayload jobExecutionPayload) {
         this.executionPayload = jobExecutionPayload;
      }

      @Override
      public ResultInstanceHolder<T> call() {
         String status = "";
         T resultObj = null;
         ResultInstanceHolder<T> resultInstanceHolder = null;
         try {
            Class<Job> executableJobClass = (Class<Job>) Class.forName(this.executionPayload.getJobClassName());
            Job<T> jobInstance = executableJobClass.newInstance();
            resultObj = jobInstance.execute(this.executionPayload.getJobContext());
            status = ResultInstanceHolder.SUCCESS;
         }
         catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
            if (exception instanceof ClassNotFoundException) {
               CacheFactory.log("Exception while creating jobObject : Class Not found = " + exception.getMessage(), CacheFactory.LOG_ERR);
            }
            else {
               CacheFactory.log("Exception while creating jobObject : Error Details = " + exception.getMessage(), CacheFactory.LOG_ERR);
            }
            status = ResultInstanceHolder.FAILED;
         }
         catch (Exception exception) {
            CacheFactory.log("Exception while executing the job: JobClass " + this.executionPayload.getJobClassName() + " : Error Details = " + exception.getMessage(), CacheFactory.LOG_ERR);
            status = ResultInstanceHolder.FAILED;
         }
         finally {
            resultInstanceHolder = new ResultInstanceHolder<T>(status, resultObj, this.executionPayload.getFireTime().getTime(), this.executionPayload.getTigger().getKey());
            semaphore.release();
         }
         return resultInstanceHolder;
      }
   }

   public Semaphore getSemaphore() {
      return semaphore;
   }

   public long getTotalJobSubmitted() {
      return noOfTaskSubmited.get();
   }

   public boolean isStartedFlag() {
      return startedFlag;
   }

   public ResultInstanceHolder<T> getResultInstance() throws InterruptedException, ExecutionException {
      return this.completionService.take().get();
   }

}