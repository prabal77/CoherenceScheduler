/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.prabal.scheduler.client.CoherenceSchedulerFacade;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.listener.TaskProcessorListener;
import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;
import org.prabal.scheduler.processor.config.StartupJobConfig;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.oracle.coherence.common.util.ObjectProxyFactory;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.GuardSupport;
import com.tangosol.net.Guardian.GuardContext;
import com.tangosol.net.NamedCache;
import com.tangosol.util.ResourceRegistry;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
public class SchedulerInfraHelper {
   private ConfigurableCacheFactory cacheConfig;
   private ConcurrentHashMap<String, NamedCache> schedulerCacheHolderMap;

   private static volatile boolean m_fStarted = false;
   private static ReentrantLock lock = new ReentrantLock();

   public SchedulerInfraHelper(ConfigurableCacheFactory cacheConfig) {
      this.cacheConfig = cacheConfig;
      this.schedulerCacheHolderMap = new ConcurrentHashMap<String, NamedCache>(3);
   }

   public static void ensureInfrastructureStarted(ConfigurableCacheFactory ccf) {

      if (m_fStarted)
         return;

      synchronized (lock) {
         if (!m_fStarted) {
            SchedulerInfraHelper helper = new SchedulerInfraHelper(ccf);
            ccf.getResourceRegistry().registerResource(SchedulerInfraHelper.class, helper);
            helper.setupInfrastrcture();
            m_fStarted = true;
         }
      }
   }

   private void setupInfrastrcture() {
      GuardContext guardContext = GuardSupport.getThreadContext();

      this.schedulerCacheHolderMap.put(SchedulerContantsEnum.DEFAULT_SUBMISSION_CACHE.getConstantValue(),
            this.cacheConfig.ensureCache(SchedulerContantsEnum.DEFAULT_SUBMISSION_CACHE.getConstantValue(), SchedulerInfraHelper.class.getClassLoader()));
      this.schedulerCacheHolderMap.put(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue(),
            this.cacheConfig.ensureCache(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue(), SchedulerInfraHelper.class.getClassLoader()));
      this.schedulerCacheHolderMap.put(SchedulerContantsEnum.TRIGGER_STORE_CACHE.getConstantValue(),
            this.cacheConfig.ensureCache(SchedulerContantsEnum.TRIGGER_STORE_CACHE.getConstantValue(), SchedulerInfraHelper.class.getClassLoader()));
      this.schedulerCacheHolderMap.put(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue(),
            this.cacheConfig.ensureCache(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue(), SchedulerInfraHelper.class.getClassLoader()));
      this.schedulerCacheHolderMap.put(SchedulerContantsEnum.RESULT_STORE_CACHE.getConstantValue(),
            this.cacheConfig.ensureCache(SchedulerContantsEnum.RESULT_STORE_CACHE.getConstantValue(), SchedulerInfraHelper.class.getClassLoader()));

      if (this.storageNotEnabled()) {
         CacheFactory.log("LocalStorage is  Disabled.Scheduler Services won't run on this node", CacheFactory.LOG_INFO);
         return;
      }

      ResourceRegistry resourceRegistry = this.cacheConfig.getResourceRegistry();

      if (guardContext != null)
         guardContext.heartbeat();
      NodeMaintanenceHelper helper = new NodeMaintanenceHelper(this.cacheConfig, this.schedulerCacheHolderMap.get(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue()));
      helper.handleNodeStartup();

      if (guardContext != null)
         guardContext.heartbeat();
      startSchedulerService(resourceRegistry);

      if (guardContext != null)
         guardContext.heartbeat();

      ExecutorService executorService = resourceRegistry.getResource(ExecutorService.class, SchedulerNamespaceHandler.RESOURCE_TIMERSERVICE_THREAD);
      TimerServiceRunnable runnable = new TimerServiceRunnable(this.cacheConfig);
      executorService.execute(runnable);
      CacheFactory.log("Started the Timerservice", CacheFactory.LOG_INFO);
   }

   /**
    * Starts the Scheduler Service Worker Threads, create TaskProcessorInstance in the
    * MemberShipCache and also attach the MapListener
    * 
    * @param resourceRegistry
    */
   @SuppressWarnings("rawtypes")
   private void startSchedulerService(ResourceRegistry resourceRegistry) {
      SchedulerServiceConfig schedulerServiceConfig = resourceRegistry.getResource(SchedulerServiceConfig.class, SchedulerNamespaceHandler.RESOURCE_SCHEDULER_CONFIG);
      TaskProcessorKey taskProcessorKey = resourceRegistry.getResource(TaskProcessorKey.class);
      TaskProcessorInstance taskProcessorInstanceProxy = null;
      TaskProcessorListener taskProcessorListener = null;

      JobExecutionShell jobExecutionShell = new JobExecutionShell(schedulerServiceConfig);

      ObjectProxyFactory<TaskProcessorInstance> objectProxyFactory = new ObjectProxyFactory<TaskProcessorInstance>(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue(),
            TaskProcessorInstance.class);

      try {
         taskProcessorInstanceProxy = objectProxyFactory.createRemoteObjectIfNotExists(taskProcessorKey, DefaultTaskProcessorInstance.class, new Object[] { taskProcessorKey });

         taskProcessorListener = new TaskProcessorListener(jobExecutionShell, taskProcessorInstanceProxy,
               this.schedulerCacheHolderMap.get(SchedulerContantsEnum.TRIGGER_STORE_CACHE.getConstantValue()));
         this.schedulerCacheHolderMap.get(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue()).addMapListener(taskProcessorListener, taskProcessorKey, true);

         resourceRegistry.registerResource(TaskProcessorListener.class, SchedulerContantsEnum.TASK_PROCESSOR_LISTENER.getConstantValue(), taskProcessorListener);
      }
      catch (Throwable throwable) {
         throw new WrapperException("Error Creating TaskProcessorInstance. " + throwable.getMessage());
      }
      resourceRegistry.registerResource(JobExecutionShell.class, ClusterInfoUtil.getLocalMemberIdShort() + SchedulerContantsEnum.SCHEDULER_INSTANCE_SUFFIX, jobExecutionShell);
      jobExecutionShell.startShell();
      CacheFactory.log("Scheduler Instance for cluster member with id = " + ClusterInfoUtil.getLocalMemberIdString() + " started ", CacheFactory.LOG_INFO);
   }

   public void cleanUpResources(boolean force) {
      if (!m_fStarted)
         throw new WrapperException("Can't clean resources. Scheduler service is not yet started");

      synchronized (lock) {

         if (this.storageNotEnabled()) {
            CacheFactory.log("LocalStorage is  Disabled. Scheduler Service are not running on this node", CacheFactory.LOG_INFO);
            return;
         }

         ResourceRegistry resourceRegistry = this.cacheConfig.getResourceRegistry();
         JobExecutionShell jobExecutionShell = resourceRegistry.getResource(JobExecutionShell.class, ClusterInfoUtil.getLocalMemberIdShort() + SchedulerContantsEnum.SCHEDULER_INSTANCE_SUFFIX);
         jobExecutionShell.shutDown(force);

         // de-register the TimerThreadService
         ExecutorService timerThread = resourceRegistry.getResource(ExecutorService.class, SchedulerNamespaceHandler.RESOURCE_TIMERSERVICE_THREAD);
         try {
            if (force) {
               timerThread.shutdownNow();
            }
            else {
               timerThread.awaitTermination(Long.parseLong(SchedulerContantsEnum.DEFAULT_AWAIT_TIMEOUT.getConstantValue()), TimeUnit.MILLISECONDS);
            }
         }
         catch (InterruptedException e) {
            CacheFactory.log("TimerThreadService stopped forcefully", CacheFactory.LOG_WARN);
         }

         // De-Register the TaskProcessorListener
         TaskProcessorListener taskProcessorListener = resourceRegistry.getResource(TaskProcessorListener.class, SchedulerContantsEnum.TASK_PROCESSOR_LISTENER.getConstantValue());
         this.schedulerCacheHolderMap.get(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue()).removeMapListener(taskProcessorListener);
         taskProcessorListener.shutDownServices(force);

         resourceRegistry.unregisterResource(JobExecutionShell.class, ClusterInfoUtil.getLocalMemberIdShort() + SchedulerContantsEnum.SCHEDULER_INSTANCE_SUFFIX);
         resourceRegistry.unregisterResource(ExecutorService.class, SchedulerNamespaceHandler.RESOURCE_TIMERSERVICE_THREAD);
         resourceRegistry.unregisterResource(TaskProcessorListener.class, SchedulerContantsEnum.TASK_PROCESSOR_LISTENER.getConstantValue());

         m_fStarted = false;
      }
   }

   @SuppressWarnings("unchecked")
   public boolean submitStartupJobs() {

      if (eligibleToSubmit()) {

         List<StartupJobConfig> startupJobs = this.cacheConfig.getResourceRegistry().getResource(List.class, SchedulerNamespaceHandler.RESOURCE_STARTUP_JOB_LIST);
         if (startupJobs == null || startupJobs.isEmpty()) {
            return true;
         }
         List<JobAndTriggerHolder> jobAndTriggerHolders = new ArrayList<SchedulerInfraHelper.JobAndTriggerHolder>(startupJobs.size());

         for (StartupJobConfig jobConfig : startupJobs) {

            JobDetails jobDetails = StartupJobsBuilder.buildStartupJobDetails(jobConfig);

            List<Trigger> triggerList = StartupJobsBuilder.buildStartupTriggerList(jobConfig, jobDetails);

            jobAndTriggerHolders.add(new JobAndTriggerHolder(jobDetails, triggerList));
         }
         CoherenceSchedulerFacade facade = new CoherenceSchedulerFacade();

         for (JobAndTriggerHolder holder : jobAndTriggerHolders) {

            facade.scheduleJobUnique(holder.getJobDetails(), holder.getTriggerList().get(0));

            if (holder.getTriggerList().size() > 1) {
               for (int i = 1; i < holder.getTriggerList().size(); i++) {
                  facade.scheduleJobUnique(holder.getTriggerList().get(i));
               }
            }
         }
         return true;
      }
      return false;
   }

   public boolean eligibleToSubmit() {
      boolean isEligibleToSubmit = false;

      if (ClusterInfoUtil.isLocalStorageEnabledMember(this.schedulerCacheHolderMap.get(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue()))) {

         SchedulerServiceConfig schedulerServiceConfig = this.cacheConfig.getResourceRegistry().getResource(SchedulerServiceConfig.class, SchedulerNamespaceHandler.RESOURCE_SCHEDULER_CONFIG);

         if (!schedulerServiceConfig.isSingleServerMode()) {
            NamedCache memberShipCache = this.schedulerCacheHolderMap.get(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue());
            if (((List<TriggerKey>) memberShipCache.get(SchedulerContantsEnum.PROCESSING_MEMEBER_SET.getConstantValue())).size() >= 2) {
               isEligibleToSubmit = true;
            }
         }
         else {
            CacheFactory.log("Running in a Single Server Mode. Considering there will be only one storage node in entire cluster", CacheFactory.LOG_DEBUG);
            isEligibleToSubmit = true;
         }
      }
      return isEligibleToSubmit;
   }

   public boolean storageNotEnabled() {
      return !(ClusterInfoUtil.isLocalStorageEnabledMember(this.schedulerCacheHolderMap.get(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue())));
   }

   public class JobAndTriggerHolder {
      private JobDetails jobDetails;
      private List<Trigger> triggerList = new ArrayList<Trigger>(1);

      public JobAndTriggerHolder(JobDetails jobDetails, List<Trigger> triggerList) {
         super();
         this.jobDetails = jobDetails;
         this.triggerList = triggerList;
      }

      public JobDetails getJobDetails() {
         return jobDetails;
      }

      public List<Trigger> getTriggerList() {
         return triggerList;
      }

   }

}