/**
 * 
 */
package org.prabal.scheduler.triggers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.prabal.scheduler.core.BaseCoherenceCalendar;
import org.prabal.scheduler.core.ExecutionStatus;
import org.prabal.scheduler.core.JobKey;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.pof.ExecutionStateCodec;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.tangosol.util.WrapperException;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public abstract class AbstractTrigger implements Trigger {
   @PortableProperty(0)
   private TriggerKey key = null;
   @PortableProperty(1)
   private JobKey jobKey = null;
   @PortableProperty(2)
   private String description;
   @PortableProperty(3)
   private Map<Object, Object> jobDataMap = new HashMap<Object, Object>();
   @PortableProperty(4)
   private String calendarName = null;
   @PortableProperty(5)
   private String fireInstanceId = null;
   @PortableProperty(6)
   private int misfireInstruction = MISFIRE_INSTRUCTION_SMART_POLICY;
   @PortableProperty(7)
   private int priority = DEFAULT_PRIORITY;
   @PortableProperty(value = 8, codec = ExecutionStateCodec.class)
   private ExecutionStatus currentTriggerState = ExecutionStatus.NONE;

   public AbstractTrigger() {
   }

   @Override
   public TriggerKey getKey() {
      return this.key;
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
   public String getCalendarName() {
      return this.calendarName;
   }

   @Override
   public Map<Object, Object> getJobDataMap() {
      return this.jobDataMap;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   @Override
   public abstract boolean mayFireAgain();

   @Override
   public abstract Date getStartTime();

   public abstract void setStartTime(Date startTime);

   @Override
   public abstract Date getEndTime();

   public abstract void setEndTime(Date endTime);

   @Override
   public abstract Date getNextFireTime();

   @Override
   public abstract Date getPreviousFireTime();

   @Override
   public abstract Date getFireTimeAfter(Date afterTime);

   @Override
   public abstract Date getFinalFireTime();

   @Override
   public int getMisfireInstruction() {
      return misfireInstruction;
   }

   public void setMisfireInstruction(int misfireInstruction) {
      if (!validateMisfireInstruction(misfireInstruction)) {
         throw new IllegalArgumentException("The misfire instruction code is invalid for this type of trigger.");
      }
      this.misfireInstruction = misfireInstruction;
   }

   @Override
   public ExecutionStatus getTriggerState() {
      return this.currentTriggerState;
   }

   @Override
   public void changeTriggerState(ExecutionStatus triggerState) {
      this.currentTriggerState = triggerState;
   }

   protected abstract boolean validateMisfireInstruction(int candidateMisfireInstruction);

   @Override
   public abstract void triggered(BaseCoherenceCalendar calendar);

   @Override
   public abstract Date computeFirstFireTime(BaseCoherenceCalendar calendar);

   @Override
   public abstract void updateAfterMisfire(BaseCoherenceCalendar cal, Date currentDate);

   @Override
   public abstract void updateWithNewCalendar(BaseCoherenceCalendar cal, long misfireThreshold);

   @Override
   public void validate() throws WrapperException {
      if (this.key.getName() == null) {
         throw new WrapperException("Trigger's name cannot be null");
      }

      if (this.key.getGroup() == null) {
         throw new WrapperException("Trigger's group cannot be null");
      }

      if (this.jobKey.getName() == null) {
         throw new WrapperException("Trigger's related Job's name cannot be null");
      }

      if (this.jobKey.getGroup() == null) {
         throw new WrapperException("Trigger's related Job's group cannot be null");
      }
   }

   @Override
   public void setFireInstanceId(String id) {
      this.fireInstanceId = id;
   }

   @Override
   public String getFireInstanceId() {
      return fireInstanceId;
   }

   @Override
   public abstract void setNextFireTime(Date nextFireTime);

   @Override
   public abstract void setPreviousFireTime(Date previousFireTime);

   @Override
   public int compareTo(Trigger other) {
      if (other.getKey() == null && getKey() == null)
         return 0;
      if (other.getKey() == null)
         return -1;
      if (getKey() == null)
         return 1;

      return getKey().compareTo(other.getKey());
   }

   public void setKey(TriggerKey key) {
      this.key = key;
   }

   public void setJobKey(JobKey jobKey) {
      this.jobKey = jobKey;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setJobDataMap(Map<Object, Object> jobDataMap) {
      this.jobDataMap = jobDataMap;
   }

   public void setCalendarName(String calendarName) {
      this.calendarName = calendarName;
   }

   public void setPriority(int priority) {
      this.priority = priority;
   }

}
