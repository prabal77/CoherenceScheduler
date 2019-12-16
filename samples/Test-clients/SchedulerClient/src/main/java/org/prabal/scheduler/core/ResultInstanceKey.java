/**
 * 
 */
package org.prabal.scheduler.core;


/**
 * @author Prabal Nandi
 *
 */
public class ResultInstanceKey extends BaseKey {

   public ResultInstanceKey(String triggerName, String triggerGroup) {
      super(triggerName, triggerGroup, BaseKey.RESULT_KEY);
   }

   public ResultInstanceKey(TriggerKey triggerKey) {
      super(triggerKey.getName(), triggerKey.getGroup(), BaseKey.RESULT_KEY);
   }

   @Override
   public Object getAssociatedKey() {
      return getName() + "_" + getGroup() + "_" + KEY_ASSOCIATOR;
   }

   @Override
   public String toString() {
      return "ResultInstanceKey [getName()=" + getName() + ", getGroup()=" + getGroup() + ", getType()=" + getType() + "]";
   }

}