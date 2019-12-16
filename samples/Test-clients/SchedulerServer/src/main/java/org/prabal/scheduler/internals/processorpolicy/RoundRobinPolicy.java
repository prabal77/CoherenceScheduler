/**
 * 
 */
package org.prabal.scheduler.internals.processorpolicy;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.prabal.scheduler.internals.SchedulerContantsEnum;
import org.prabal.scheduler.internals.DefaultTaskProcessorInstance;
import org.prabal.scheduler.internals.TaskProcessorInstance;
import org.prabal.scheduler.internals.TaskProcessorKey;
import org.prabal.scheduler.internals.processors.FetchNextTaskProcessor;

import com.oracle.coherence.common.util.ObjectProxyFactory;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.NamedCache;

/**
 * @author Prabal Nandi
 *
 */
public class RoundRobinPolicy implements TaskProcessorPolicy {
   private ConfigurableCacheFactory cacheFactory;
   private NamedCache namedCache;

   public RoundRobinPolicy(ConfigurableCacheFactory cacheFactory) {
      super();
      this.cacheFactory = cacheFactory;
      this.namedCache = null;
      ensureNamedCache();
   }

   private void ensureNamedCache() {
      if (this.namedCache == null) {
      //   CacheFactory.setConfigurableCacheFactory(this.cacheFactory);
         this.namedCache = CacheFactory.getCache(SchedulerContantsEnum.MEMBERSHIP_STORE_CACHE.getConstantValue());
      }
   }

   @Override
   public TaskProcessorInstance getNextAvailableProcessor() {
      ensureNamedCache();
      TaskProcessorKey targetProcessorKey = (TaskProcessorKey) this.namedCache.invoke(SchedulerContantsEnum.PROCESSING_MEMEBER_SET.getConstantValue(), new FetchNextTaskProcessor());
      targetProcessorKey.setNextAvailableProcessor(false);
      ObjectProxyFactory<TaskProcessorInstance> objectProxyFactory = new ObjectProxyFactory<TaskProcessorInstance>(this.namedCache.getCacheName(), TaskProcessorInstance.class);
      return objectProxyFactory.getProxy(targetProcessorKey);
   }

   @Override
   public int getAvailableProcessorsCount() {
      return ((ArrayList<TaskProcessorKey>) this.namedCache.get(SchedulerContantsEnum.PROCESSING_MEMEBER_SET.getConstantValue())).size();
   }

}
