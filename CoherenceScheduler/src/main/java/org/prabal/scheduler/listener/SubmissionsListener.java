/**
 * 
 */
package org.prabal.scheduler.listener;

import org.prabal.scheduler.core.BaseCoherenceCalendar;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.JobKey;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;
import org.prabal.scheduler.internals.ProcessSubmissions;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.GuardSupport;
import com.tangosol.net.Guardian.GuardContext;
import com.tangosol.net.events.Event;
import com.tangosol.net.events.EventInterceptor;
import com.tangosol.net.events.annotation.Interceptor;
import com.tangosol.net.events.partition.cache.EntryEvent;
import com.tangosol.net.events.partition.cache.EntryProcessorEvent;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.Converter;

/**
 * @author Prabal Nandi
 *
 */
@Interceptor(identifier = "SubmissionsInterceptor", entryEvents = { EntryEvent.Type.INSERTED, EntryEvent.Type.UPDATED, EntryEvent.Type.REMOVED }, entryProcessorEvents = EntryProcessorEvent.Type.EXECUTED)
public class SubmissionsListener implements EventInterceptor {

   @Override
   public void onEvent(Event event) {
      if (event instanceof EntryEvent) {
         processEntryEvents((EntryEvent) event);
      }
      else if (event instanceof EntryProcessorEvent) {
         processEntryProcessorEvents((EntryProcessorEvent) event);
      }
   }

   /**
    * All operation as post commit events, hence will be asynchronous
    * 
    * @param event
    */
   private void processEntryEvents(EntryEvent event) {
      GuardContext guardContext = GuardSupport.getThreadContext();
      for (BinaryEntry entry : event.getEntrySet()) {
         Converter keyConverter = entry.getContext().getKeyFromInternalConverter();
         Converter valueConverter = entry.getContext().getValueFromInternalConverter();
         try {
            if (guardContext != null) {
               guardContext.heartbeat();
            }
            Object keyObj = keyConverter.convert(entry.getBinaryKey());
            Object valuObj = valueConverter.convert(entry.getBinaryValue());
            if (valuObj instanceof Trigger) {
               ProcessSubmissions.processTriggers((TriggerKey) keyObj, (Trigger) valuObj);
            }
            else if (valuObj instanceof JobDetails) {
               ProcessSubmissions.processJobDetails((JobKey) keyObj, (JobDetails) valuObj);
            }
            else if (valuObj instanceof BaseCoherenceCalendar) {
               ProcessSubmissions.processCalendar((String) keyObj, (BaseCoherenceCalendar) valuObj);
            }
         }
         catch (Exception exception) {
            CacheFactory.log("Error submiting to cache: " + exception.getMessage(), CacheFactory.LOG_ERR);
         }
      }
   }

   private void processEntryProcessorEvents(EntryProcessorEvent event) {
      // As of now no functionality is needed here
   }

}
