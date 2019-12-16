/**
 * 
 */
package org.prabal.scheduler.core;

import java.util.Map;

/**
 * Base interface
 * 
 * @author Prabal Nandi
 *
 */
public interface JobDetails<K, V> extends Cloneable {
   public JobKey getJobKey();

   /**
    * <p>
    * Return the description given to the <code>Job</code> instance by its creator (if any).
    * </p>
    * 
    * @return null if no description was set.
    */
   public String getDescription();

   /**
    * <p>
    * Get the fully qualified name of the <code>Job</code> that will be executed
    * </p>
    */
   public String getJobClassName();

   /**
    * <p>
    * Get the <code>JobDataMap</code> that is associated with the <code>Job</code>.
    * </p>
    */
   public Map<K, V> getJobDataMap();

   /**
    * <p>
    * Whether or not the <code>Job</code> should remain stored after it is orphaned (no
    * <code>{@link Trigger}s</code> point to it).
    * </p>
    * 
    * <p>
    * If not explicitly set, the default value is <code>false</code>.
    * </p>
    * 
    * @return <code>true</code> if the Job should remain persisted after being orphaned.
    */
   public boolean isDurable();

   /**
    * <p>
    * Instructs the <code>Scheduler</code> whether or not the <code>Job</code> should be re-executed
    * if a 'recovery' or 'fail-over' situation is encountered.
    * </p>
    * 
    * <p>
    * If not explicitly set, the default value is <code>false</code>.
    * </p>
    * 
    * @see JobExecutionEnvironment#isRecovering()
    */
   public boolean requestsRecovery();

   public ExecutionStatus getJobStatus();

   public void setJobStatus(ExecutionStatus status);

   public Object clone() throws CloneNotSupportedException;
}
