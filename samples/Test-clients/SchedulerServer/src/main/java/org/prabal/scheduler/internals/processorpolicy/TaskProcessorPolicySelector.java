
/**
 * 
 */
package org.prabal.scheduler.internals.processorpolicy;

import org.prabal.scheduler.internals.SchedulerContantsEnum;
import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;

import com.tangosol.net.ConfigurableCacheFactory;

/**
 * @author Prabal Nandi
 *
 */
public class TaskProcessorPolicySelector {
   private static volatile TaskProcessorPolicy taskProcessorPolicy;

   public static TaskProcessorPolicy getTaskProcessorPolicy(ConfigurableCacheFactory cacheFactory) {
      if (taskProcessorPolicy == null) {
         synchronized (TaskProcessorPolicySelector.class) {
            if (taskProcessorPolicy == null) {
               SchedulerServiceConfig schedulerServiceConfig = cacheFactory.getResourceRegistry().getResource(SchedulerServiceConfig.class, SchedulerNamespaceHandler.RESOURCE_SCHEDULER_CONFIG);
               if (SchedulerContantsEnum.ROUND_ROBIN_POLICY.getConstantValue().equals(schedulerServiceConfig.getDispatcherpolicy())) {
                  taskProcessorPolicy = new RoundRobinPolicy(cacheFactory);
               }
            }
         }
      }
      return taskProcessorPolicy;
   }

}