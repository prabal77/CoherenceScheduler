/**
 * 
 */
package org.prabal.scheduler.internals.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.prabal.scheduler.core.ExecutionStatus;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.SimpleJobContext;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.internals.JobExecutionPayload;
import org.prabal.scheduler.internals.SchedulerContantsEnum;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.net.BackingMapContext;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.Converter;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class GetExecutionPayloadProcessor extends AbstractProcessor {

   @Override
   public Object process(Entry entry) {
      Trigger trigger = (Trigger) entry.getValue();
      JobExecutionPayload jobExecutionPayload = null;

      if (trigger.getTriggerState() != ExecutionStatus.EXECUTING)
         return jobExecutionPayload;

      BackingMapManagerContext context = ((BinaryEntry) entry).getContext();

      Converter keyToInternal = context.getKeyToInternalConverter();
      Converter valueFromInternal = context.getValueFromInternalConverter();

      BackingMapContext backingMapContext = context.getBackingMapContext(SchedulerContantsEnum.JOB_STORE_CACHE.getConstantValue());
      BinaryEntry jobDetailEntry = (BinaryEntry) backingMapContext.getBackingMapEntry(keyToInternal.convert(trigger.getJobKey()));
      JobDetails jobDetails = (JobDetails) valueFromInternal.convert(jobDetailEntry.getBinaryValue());

      if (jobDetails == null) {
         trigger.changeTriggerState(ExecutionStatus.ERROR);
         entry.setValue(trigger, true);
         return jobExecutionPayload;
      }

      if (jobDetails.getJobStatus() == ExecutionStatus.BLOCKED) {
         trigger.changeTriggerState(ExecutionStatus.BLOCKED);
         entry.setValue(trigger, true);
      }
      else {

         jobDetails.setJobStatus(ExecutionStatus.EXECUTING);
         SimpleJobContext jobContext = new SimpleJobContext();
         jobContext.addAllToDataMap(jobDetails.getJobDataMap());
         jobContext.addAllToDataMap(trigger.getJobDataMap());

         jobExecutionPayload = new JobExecutionPayload();
         jobExecutionPayload.setJobContext(jobContext);
         jobExecutionPayload.setDurable(jobDetails.isDurable());
         jobExecutionPayload.setShouldRecover(jobDetails.requestsRecovery());
         jobExecutionPayload.setTigger(trigger);
         jobExecutionPayload.setJobClassName(jobDetails.getJobClassName());
         jobExecutionPayload.setFireTime(trigger.getNextFireTime());
      }
      return jobExecutionPayload;
   }

   @Override
   public Map processAll(Set entrySet) {
      Map<TriggerKey, JobExecutionPayload> jobPayloadMap = new HashMap<TriggerKey, JobExecutionPayload>();
      for (InvocableMap.Entry entry : (Set<InvocableMap.Entry>) entrySet) {
         JobExecutionPayload jobExecutionPayload = (JobExecutionPayload) process(entry);
         if (jobExecutionPayload != null) {
            jobPayloadMap.put((TriggerKey) entry.getKey(), jobExecutionPayload);
         }
      }
      return jobPayloadMap;
   }
}