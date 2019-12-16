/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.prabal.scheduler.core.JobContext;
import org.prabal.scheduler.core.Trigger;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class JobExecutionPayload implements Delayed {
   @PortableProperty(0)
   private String jobClassName;
   @PortableProperty(1)
   private Trigger tigger;
   @PortableProperty(2)
   private boolean durable;
   @PortableProperty(3)
   private boolean shouldRecover;
   @PortableProperty(4)
   private JobContext jobContext;
   @PortableProperty(5)
   private Date fireTime;

   public JobExecutionPayload() {
      super();
   }

   public String getJobClassName() {
      return jobClassName;
   }

   public void setJobClassName(String jobClassName) {
      this.jobClassName = jobClassName;
   }

   public Trigger getTigger() {
      return tigger;
   }

   public void setTigger(Trigger tigger) {
      this.tigger = tigger;
   }

   public boolean isDurable() {
      return durable;
   }

   public void setDurable(boolean durable) {
      this.durable = durable;
   }

   public boolean isShouldRecover() {
      return shouldRecover;
   }

   public void setShouldRecover(boolean shouldRecover) {
      this.shouldRecover = shouldRecover;
   }

   public JobContext getJobContext() {
      return jobContext;
   }

   public void setJobContext(JobContext jobContext) {
      this.jobContext = jobContext;
   }

   public Date getFireTime() {
      return fireTime;
   }

   public void setFireTime(Date fireTime) {
      this.fireTime = fireTime;
   }

   @Override
   public int compareTo(Delayed obj) {
      return this.fireTime.compareTo(((JobExecutionPayload) obj).getFireTime());
   }

   @Override
   public long getDelay(TimeUnit unit) {
      long diff = this.fireTime.getTime() - System.currentTimeMillis();
      return unit.convert(diff, TimeUnit.NANOSECONDS);
   }

   @Override
   public String toString() {
      return "JobExecutionPayload [jobClassName=" + jobClassName + ", tigger=" + tigger + ", durable=" + durable + ", shouldRecover=" + shouldRecover + ", jobContext=" + jobContext + ", fireTime="
            + fireTime + "]";
   }

}
