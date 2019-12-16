/**
 * 
 */
package org.prabal.scheduler.processor.config;

import org.prabal.scheduler.internals.SchedulerContantsEnum;

/**
 * @author Prabal Nandi
 *
 */
public class SchedulerServiceConfig {
   public static long DEFAULT_IDLE_WAIT_TIME = 30L * 1000L;
   public static String DEFAULT_DISPATCHER_POLICY = SchedulerContantsEnum.ROUND_ROBIN_POLICY.getConstantValue();

   private String name;
   private int maxThreadCount;
   private long idleWaitTime;
   private String dispatcherpolicy;
   private boolean isSingleServerMode = false;

   public SchedulerServiceConfig() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getMaxThreadCount() {
      return maxThreadCount;
   }

   public void setMaxThreadCount(int maxThreadCount) {
      this.maxThreadCount = maxThreadCount;
   }

   public long getIdleWaitTime() {
      return idleWaitTime;
   }

   public void setIdleWaitTIme(long idleWaitTime) {
      this.idleWaitTime = idleWaitTime;
   }

   public String getDispatcherpolicy() {
      return dispatcherpolicy;
   }

   public void setDispatcherpolicy(String dispatcherpolicy) {
      this.dispatcherpolicy = dispatcherpolicy;
   }

   public boolean isSingleServerMode() {
      return isSingleServerMode;
   }

   public void setSingleServerMode(boolean isSingleServerMode) {
      this.isSingleServerMode = isSingleServerMode;
   }

   @Override
   public String toString() {
      return "SchedulerServiceConfig [name=" + name + ", maxThreadCount=" + maxThreadCount + ", idleWaitTime=" + idleWaitTime + ", dispatcherpolicy=" + dispatcherpolicy + ", isSingleServerMode="
            + isSingleServerMode + "]";
   }

}
