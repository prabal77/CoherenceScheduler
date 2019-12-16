package org.prabal.scheduler.client;

import org.prabal.scheduler.core.ResultInstanceKey;
import org.prabal.scheduler.core.TriggerKey;

/**
 * @author Prabal Nandi
 *
 */
public class SchedulerResultKeyBuilder {

   public static ResultInstanceKey build(TriggerKey triggerKey) {
      return new ResultInstanceKey(triggerKey);
   }

}