/**
 * 
 */
package org.prabal.scheduler.processor.builder;

import java.util.concurrent.ExecutorService;

import javax.naming.ConfigurationException;

import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;
import org.prabal.scheduler.processor.config.ServiceConfig;

import com.tangosol.config.annotation.Injectable;
import com.tangosol.util.Base;
import com.tangosol.util.Builder;

/**
 * @author Prabal Nandi
 *
 */
public class ServiceConfigBuilder implements Builder<ServiceConfig> {
   private volatile ExecutorService m_timerServiceExecutor = null;
   private volatile SchedulerServiceConfig m_SchedulerServiceConfig = null;

   public ServiceConfigBuilder() {
      super();
   }

   @Override
   public ServiceConfig realize() {

      if (m_SchedulerServiceConfig == null)
         throw Base.ensureRuntimeException(new ConfigurationException("Please provide Coherence Scheduler XML Configuration"));

      if (m_timerServiceExecutor == null)
         throw Base.ensureRuntimeException(new ConfigurationException("Unable to create Scheduler Timer Executor service"));

      return new ServiceConfig(m_timerServiceExecutor, m_SchedulerServiceConfig);
   }

   public void setTimerServiceExecutor(ExecutorService m_timerServiceExecutor) {
      this.m_timerServiceExecutor = m_timerServiceExecutor;
   }

   @Injectable(SchedulerNamespaceHandler.SCHEDULER_SERVICE)
   public void setSchedulerServiceConfig(SchedulerServiceConfig m_SchedulerServiceConfig) {
      this.m_SchedulerServiceConfig = m_SchedulerServiceConfig;
   }

}
