/**
 * 
 */
package org.prabal.scheduler.core;

import java.util.Map;

import org.prabal.scheduler.pof.ExecutionStateCodec;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class SimpleJobDetail<K, V> implements JobDetails<K, V> {
   @PortableProperty(0)
   private JobKey jobKey;
   @PortableProperty(1)
   private String description;
   @PortableProperty(2)
   private String jobClassName;
   @PortableProperty(3)
   private Map<K, V> jobDataMap;
   @PortableProperty(4)
   private boolean durable;
   @PortableProperty(5)
   private boolean shouldRecover;
   @PortableProperty(value = 6, codec = ExecutionStateCodec.class)
   private ExecutionStatus jobStatus = ExecutionStatus.NONE;

   public SimpleJobDetail() {
      super();
   }

   public void setJobKey(JobKey jobKey) {
      this.jobKey = jobKey;
   }

   @Override
   public JobKey getJobKey() {
      return this.jobKey;
   }

   @Override
   public String getDescription() {
      return this.description;
   }

   @Override
   public String getJobClassName() {
      return this.jobClassName;
   }

   @Override
   public Map<K, V> getJobDataMap() {
      return this.jobDataMap;
   }

   @Override
   public boolean isDurable() {
      return this.durable;
   }

   @Override
   public boolean requestsRecovery() {
      return this.shouldRecover;
   }

   public String getName() {
      return (this.jobKey != null) ? this.jobKey.getName() : null;
   }

   public String getGroup() {
      return (this.jobKey != null) ? this.jobKey.getGroup() : null;
   }

   public String getType() {
      return (this.jobKey != null) ? this.jobKey.getType() : null;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setJobClassName(String jobClassName) {
      this.jobClassName = jobClassName;
   }

   public void setJobDataMap(Map<K, V> jobDataMap) {
      this.jobDataMap = jobDataMap;
   }

   public void setDurable(boolean durable) {
      this.durable = durable;
   }

   public void setShouldRecover(boolean shouldRecover) {
      this.shouldRecover = shouldRecover;
   }

   public void setJobStatus(ExecutionStatus jobStatus) {
      this.jobStatus = jobStatus;
   }

   @Override
   public ExecutionStatus getJobStatus() {
      return this.jobStatus;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException("Cloning to JobDetails Not supported");
   }

   @Override
   public String toString() {
      return "SimpleJobDetail [jobKey=" + jobKey + ", description=" + description + ", jobClassName=" + jobClassName + ", jobDataMap=" + jobDataMap + ", durable=" + durable + ", shouldRecover="
            + shouldRecover + ", jobStatus=" + jobStatus + "]";
   }

}
