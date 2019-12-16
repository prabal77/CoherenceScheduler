/**
 * 
 */
package org.prabal.scheduler.listener;

import org.prabal.scheduler.internals.SchedulerInfraHelper;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.events.EventInterceptor;
import com.tangosol.net.events.annotation.Interceptor;
import com.tangosol.net.events.annotation.Interceptor.Order;
import com.tangosol.net.events.application.LifecycleEvent;

/**
 * @author Prabal Nandi
 *
 */
@Interceptor(identifier = "ServiceLifecycleInterceptor", order = Order.HIGH)
public class LifecycleInterceptor implements EventInterceptor<LifecycleEvent> {

   @Override
   public void onEvent(LifecycleEvent event) {
      switch (event.getType()) {
         case ACTIVATED: {
            SchedulerInfraHelper.ensureInfrastructureStarted(event.getConfigurableCacheFactory());
            SchedulerInfraHelper infraHelper = event.getConfigurableCacheFactory().getResourceRegistry().getResource(SchedulerInfraHelper.class);
            infraHelper.submitStartupJobs();
            break;
         }
         case DISPOSING: {
            CacheFactory.log("Disposing "+event.toString(), CacheFactory.LOG_INFO);
            SchedulerInfraHelper infraHelper = event.getConfigurableCacheFactory().getResourceRegistry().getResource(SchedulerInfraHelper.class);
            infraHelper.cleanUpResources(true);
         }
      }
   }

}
