/**
 * 
 */
package org.prabal.scheduler.processor.config;

import java.util.concurrent.ExecutorService;

/**
 * @author Prabal Nandi
 *
 */
public class ServiceConfig {
   private ExecutorService m_timerServiceExecutor = null;
   private SchedulerServiceConfig m_SchedulerServiceConfig = null;

   public ServiceConfig(ExecutorService timerServiceExecutor, SchedulerServiceConfig m_SchedulerServiceConfig) {
      super();
      this.m_timerServiceExecutor = timerServiceExecutor;
      this.m_SchedulerServiceConfig = m_SchedulerServiceConfig;
   }

   public ServiceConfig() {
      super();
   }

   public ExecutorService getM_timerServiceExecutor() {
      return m_timerServiceExecutor;
   }

   public void setM_timerServiceExecutor(ExecutorService m_timerServiceExecutor) {
      this.m_timerServiceExecutor = m_timerServiceExecutor;
   }

   public SchedulerServiceConfig getM_SchedulerServiceConfig() {
      return m_SchedulerServiceConfig;
   }

   public void setM_SchedulerServiceConfig(SchedulerServiceConfig m_SchedulerServiceConfig) {
      this.m_SchedulerServiceConfig = m_SchedulerServiceConfig;
   }

   @Override
   public String toString() {
      return "ServiceConfig [m_timerServiceExecutor=" + m_timerServiceExecutor + ", m_SchedulerServiceConfig=" + m_SchedulerServiceConfig + "]";
   }

}
