/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.prabal.scheduler.core.ExecutionStatus;
import org.prabal.scheduler.core.Job;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.internals.processors.ApplyMisFireTrigger;
import org.prabal.scheduler.internals.processors.GetExecutionPayloadProcessor;
import org.prabal.scheduler.internals.processors.UpdateTriggerAfterFireProcessor;
import org.prabal.scheduler.internals.processors.UpdateTriggerStatusProcessor;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.DistributedCacheService;
import com.tangosol.net.NamedCache;
import com.tangosol.net.partition.PartitionSet;
import com.tangosol.util.filter.AndFilter;
import com.tangosol.util.filter.AnyFilter;
import com.tangosol.util.filter.EqualsFilter;
import com.tangosol.util.filter.LessEqualsFilter;
import com.tangosol.util.filter.PartitionedFilter;

/**
 * @author Prabal Nandi
 *
 */
public class CoherenceJobStore {
   private NamedCache jobStoreCache;
   private NamedCache triggerStoreCache;
   private DistributedCacheService cacheService;
   private ConfigurableCacheFactory cacheFactory;

   public CoherenceJobStore(ConfigurableCacheFactory cacheFactory) {
      this.cacheFactory = cacheFactory;
      this.cacheService = (DistributedCacheService) CacheFactory.getService(SchedulerContantsEnum.JOB_STORE_SERVICE.getConstantValue());
      this.jobStoreCache = cacheFactory.ensureCache(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue(), cacheService.getContextClassLoader());
      this.triggerStoreCache = cacheFactory.ensureCache(SchedulerContantsEnum.TRIGGER_STORE_CACHE.getConstantValue(), cacheService.getContextClassLoader());
   }

   public TriggerListHolder acquireNextTrigger(Date highLimitDate, int maxCount) {
      TriggerListHolder triggerListHolder = new TriggerListHolder();

      EqualsFilter triggerStateFilter = new EqualsFilter("getTriggerState", ExecutionStatus.NORMAL);
      LessEqualsFilter fireTimeLessEqualsFilter = new LessEqualsFilter("getNextFireTime", highLimitDate);
      AndFilter triggerStateAndRangeFilter = new AndFilter(triggerStateFilter, fireTimeLessEqualsFilter);

      PartitionSet partitionSet = this.cacheService.getOwnedPartitions(ClusterInfoUtil.getLocalMember());
      PartitionedFilter localPartitionFilter = new PartitionedFilter(triggerStateAndRangeFilter, partitionSet);

      for (Map.Entry<TriggerKey, Trigger> entry : (Set<Map.Entry<TriggerKey, Trigger>>) this.triggerStoreCache.entrySet(localPartitionFilter)) {
         triggerListHolder.addTriggerToList(entry.getValue());
      }
      addRejectedTriggers(highLimitDate, triggerListHolder);

      if (triggerListHolder.isEmpty())
         return triggerListHolder;

      List<ExecutionStatus> fromStatusList = new ArrayList<ExecutionStatus>(2);
      fromStatusList.add(ExecutionStatus.NORMAL);
      fromStatusList.add(ExecutionStatus.REJECTED);

      Map<TriggerKey, Boolean> validTriggerMap = this.triggerStoreCache.invokeAll(triggerListHolder.getTriggerKeySet(), new UpdateTriggerStatusProcessor(fromStatusList, ExecutionStatus.EXECUTING));
      triggerListHolder.retainAllTriggers(validTriggerMap.keySet());
      return triggerListHolder;
   }

   public void prepareTriggerInstance(Date currentTime) {
      EqualsFilter stateNormalFilter = new EqualsFilter("getTriggerState", ExecutionStatus.NORMAL);
      EqualsFilter stateRejectedFilter = new EqualsFilter("getTriggerState", ExecutionStatus.REJECTED);
      AnyFilter triggerStateOrFilter = new AnyFilter(new EqualsFilter[] { stateNormalFilter, stateRejectedFilter });
      LessEqualsFilter fireTimeLessEqualsFilter = new LessEqualsFilter("getNextFireTime", currentTime);
      AndFilter triggerStateAndRangeFilter = new AndFilter(triggerStateOrFilter, fireTimeLessEqualsFilter);

      PartitionSet partitionSet = this.cacheService.getOwnedPartitions(ClusterInfoUtil.getLocalMember());
      PartitionedFilter localPartitionFilter = new PartitionedFilter(triggerStateAndRangeFilter, partitionSet);
      // Wait till entryProcessors Return
      Map returnMap = this.triggerStoreCache.invokeAll(localPartitionFilter, new ApplyMisFireTrigger());
      return;
   }

   /**
    * Checks caches for any Trigger Instances which were rejected by other Schedulers and returns
    * List of TriggerHolder
    * 
    * @param lowerLimitDate
    * @param upperLimitDate
    * @return List<TriggerHolder>
    */
   private void addRejectedTriggers(Date lowerLimitDate, TriggerListHolder triggerListHolder) {

      EqualsFilter triggerStateFilter = new EqualsFilter("getTriggerState", ExecutionStatus.REJECTED);
      LessEqualsFilter fireTimeLessEqualsFilter = new LessEqualsFilter("getNextFireTime", lowerLimitDate);
      AndFilter triggerStateAndRangeFilter = new AndFilter(triggerStateFilter, fireTimeLessEqualsFilter);

      for (Map.Entry<TriggerKey, Trigger> entry : (Set<Map.Entry<TriggerKey, Trigger>>) this.triggerStoreCache.entrySet(triggerStateAndRangeFilter)) {
         triggerListHolder.addTriggerToList(entry.getValue());
      }
   }

   public Map<TriggerKey, JobExecutionPayload> getJobExecutionPayloadList(TriggerListHolder triggerListHolder) {
      Map<TriggerKey, JobExecutionPayload> jobPayloadMap = this.triggerStoreCache.invokeAll(triggerListHolder.getTriggerKeySet(), new GetExecutionPayloadProcessor());
      triggerListHolder.retainAllTriggers(jobPayloadMap.keySet());

      Map<TriggerKey, JobExecutionPayload> payloadMap = new HashMap<TriggerKey, JobExecutionPayload>(triggerListHolder.size());
      payloadMap.putAll(jobPayloadMap);
      return jobPayloadMap;
   }

}