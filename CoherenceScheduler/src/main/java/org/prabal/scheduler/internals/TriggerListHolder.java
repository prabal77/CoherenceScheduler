/**
 * 
 */
package org.prabal.scheduler.internals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;

/**
 * @author Prabal Nandi
 *
 */
public class TriggerListHolder {
   private final HashMap<TriggerKey, Trigger> triggerMap;

   public TriggerListHolder() {
      super();
      this.triggerMap = new HashMap<TriggerKey, Trigger>();
   }

   public void addTriggerToList(Trigger triggerInstance) {
      this.triggerMap.put(triggerInstance.getKey(), triggerInstance);
   }

   public void removeTriggerFromList(Trigger triggerInstance) {
      this.triggerMap.remove(triggerInstance.getKey());
   }

   public void removeTriggerFromList(TriggerKey triggerKey) {
      this.triggerMap.remove(triggerKey);
   }

   public void retainAllTriggers(Set<TriggerKey> triggerKeySet) {
      this.triggerMap.keySet().retainAll(triggerKeySet);
   }

   public void retainAllTriggers(List<Trigger> triggerInstanceList) {
      this.triggerMap.values().retainAll(triggerInstanceList);
   }

   public boolean isEmpty() {
      return this.triggerMap.isEmpty();
   }

   public List<Trigger> getEligibleTriggerList() {
      List<Trigger> triggerList = new ArrayList<Trigger>(this.triggerMap.size());
      triggerList.addAll(this.triggerMap.values());
      return triggerList;
   }

   public int size() {
      return this.triggerMap.size();
   }

   public Set<TriggerKey> getTriggerKeySet() {
      return this.triggerMap.keySet();
   }

   public List<Trigger> getSortedTriggerList() {
      List<Trigger> triggerList = new ArrayList<Trigger>(this.triggerMap.size());
      triggerList.addAll(this.triggerMap.values());
      Collections.sort(triggerList, new Trigger.TriggerTimeComparator());
      return triggerList;
   }

   @Override
   public String toString() {
      return "TriggerListHolder [triggerMap=" + triggerMap + "]";
   }

   public String toStringForTest() {
      StringBuilder returnString = new StringBuilder("TriggerListHolder [ ");
      int count = 0;
      for (Map.Entry<TriggerKey, Trigger> entry : this.triggerMap.entrySet()) {
         returnString.append("\nElement " + (++count) + " { " + entry.getKey() + " " + entry.getValue() + " } ");
      }
      returnString.append("\n ]");
      return returnString.toString();
   }

}
