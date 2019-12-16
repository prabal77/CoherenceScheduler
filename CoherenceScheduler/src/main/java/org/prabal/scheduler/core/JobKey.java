
/**
 * 
 */
package org.prabal.scheduler.core;

/**
 * @author Prabal Nandi
 *
 */
public class JobKey extends BaseKey {

   public JobKey(String name, String group) {
      super(name, group, BaseKey.JOB_KEY);
   }

   public JobKey(String name) {
      super(name, null, BaseKey.JOB_KEY);
   }

   @Override
   public Object getAssociatedKey() {
      return getName() + "_" + getGroup() + "_" + KEY_ASSOCIATOR;
   }

   @Override
   public String toString() {
      return "JobKey [name=" + getName() + ", group=" + getGroup() + ", type=" + getType() + "]";
   }

}