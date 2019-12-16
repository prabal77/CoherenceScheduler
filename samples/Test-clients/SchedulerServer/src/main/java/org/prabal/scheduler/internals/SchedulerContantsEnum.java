/**
 * 
 */
package org.prabal.scheduler.internals;

/**
 * @author Prabal Nandi
 *
 */
public enum SchedulerContantsEnum {
   PAUSED_JOB_GROUP_PREFIX("PAUSED_JOB_GROUP_"), 
   PAUSED_TRIGGER_PREFIX("PAUSED_TRIGGER_"), 
   BLOCKED_JOB_GROUP_PREFIX("BLOCKED_JOB_GROUP_"), 
   STORAGE_MEMBER_LIST("STORAGE_MEMBER_LIST_KEY"), 
   DEFAULT_SUBMISSION_CACHE("org.prabal.scheduler.JobSubmissionCache"),
   JOB_STORE_CACHE("org.prabal.scheduler.JobStoreCache"), 
   TRIGGER_STORE_CACHE("org.prabal.scheduler.TriggerStoreCache"),
   RESULT_STORE_CACHE("org.prabal.scheduler.JobResultCache"),
   MEMBERSHIP_STORE_CACHE("org.prabal.scheduler.MembershipStore"), 
   SCHEDULDER_WORKER_THREAD_GROUP("Scheduler_Worker"), 
   SCHEDULER_INSTANCE_SUFFIX("_SCHEDULER_INSTANCE"), 
   PAYLOAD_QUEUE_SUFFIX("_PAYLOAD_QUEUE"), 
   DEFAULT_AWAIT_TIMEOUT("10000"), 
   JOB_STORE_SERVICE("Job-Store-Service"), 
   PROCESSING_MEMEBER_SET("PROCESSING_MEMBER_SET"), 
   NETWORK_TIME_BUFFER("5000"), 
   MAX_BATCH_SIZE("10"), 
   ROUND_ROBIN_POLICY("RoundRobin"), 
   START_END_DATE_FORMAT("MM/dd/yyyy HH:mm:ss:SSS"),
   TRIGGER_TYPE_SIMPLE("SIMPLE_TRIGGER"),
   TRIGGER_TYPE_CRON("CRON_TRIGGER"),
   TASK_PROCESSOR_LISTENER("TASK_PROCESSOR_LISTENER"),
   MISFIRE_THRESHOLD("5000");

   private String contantValue;

   private SchedulerContantsEnum(String value) {
      this.contantValue = value;
   }

   public String getConstantValue() {
      return this.contantValue;
   }
}