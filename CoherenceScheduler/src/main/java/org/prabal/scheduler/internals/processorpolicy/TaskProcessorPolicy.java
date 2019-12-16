/**
 * 
 */
package org.prabal.scheduler.internals.processorpolicy;

import org.prabal.scheduler.internals.TaskProcessorInstance;

/**
 * @author Prabal Nandi
 *
 */
public interface TaskProcessorPolicy {

   public TaskProcessorInstance getNextAvailableProcessor();

   public int getAvailableProcessorsCount();

}