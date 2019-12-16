/**
 * 
 */
package org.prabal.scheduler.processor.builder;

import org.prabal.scheduler.internals.SchedulerContantsEnum;
import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;

import com.tangosol.config.annotation.Injectable;
import com.tangosol.util.Builder;

/**
 * @author Prabal Nandi
 *
 */
public class SchedulerServiceBuilder implements Builder<SchedulerServiceConfig> {
   private volatile String name;
   private volatile int maxThreadCount;
   private volatile long idlewaittimeinmillis;
   private volatile String dispatcherPolicy;

   @Override
   public SchedulerServiceConfig realize() {
      SchedulerServiceConfig schedulerServiceConfig = new SchedulerServiceConfig();
      schedulerServiceConfig.setName(this.name);
      schedulerServiceConfig.setMaxThreadCount(this.maxThreadCount);
      schedulerServiceConfig.setIdleWaitTIme(this.idlewaittimeinmillis);
      schedulerServiceConfig.setDispatcherpolicy(this.dispatcherPolicy);
      return schedulerServiceConfig;
   }

   @Injectable(SchedulerNamespaceHandler.SCHEDULER_NAME)
   public void setName(String name) {
      this.name = name;
   }

   @Injectable(SchedulerNamespaceHandler.SCHEDULER_MAX_THREAD)
   public void setMaxThreadCount(int maxThreadCount) {
      this.maxThreadCount = maxThreadCount;
   }

   @Injectable(SchedulerNamespaceHandler.SCHEDULER_IDLE_WAIT_TIME)
   public void setIdlewaittimeinmillis(String idlewaittimeinmillis) {
      long waitTime = SchedulerServiceConfig.DEFAULT_IDLE_WAIT_TIME;

      try {
         if (idlewaittimeinmillis != null && !idlewaittimeinmillis.equals(""))
            waitTime = Long.parseLong(idlewaittimeinmillis);
      }
      catch (NumberFormatException exception) {
         // Do nothing default value will be used
      }
      this.idlewaittimeinmillis = waitTime;
   }

   @Injectable(SchedulerNamespaceHandler.SCHEDULER_DISPATCHER_POLICY)
   public void setDispatcherPolicy(String dispatcherPolicy) {
      this.dispatcherPolicy = SchedulerServiceConfig.DEFAULT_DISPATCHER_POLICY;
      if (dispatcherPolicy != null && !dispatcherPolicy.equals(""))
         this.dispatcherPolicy = dispatcherPolicy;
   }

}
