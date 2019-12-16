/**
 * 
 */
package org.prabal.scheduler.namespacehandler;

import org.prabal.scheduler.processor.JobDataMapProcessor;
import org.prabal.scheduler.processor.SchedulerServiceProcessor;
import org.prabal.scheduler.processor.ServiceConfigProcessor;
import org.prabal.scheduler.processor.StartupJobListProcessor;
import org.prabal.scheduler.processor.StartupJobProcessor;
import org.prabal.scheduler.processor.StartupTriggerListProcessor;
import org.prabal.scheduler.processor.StartupTriggerListProcessor.CronTriggerProcessor;
import org.prabal.scheduler.processor.StartupTriggerListProcessor.SimpleTriggerProcessor;
import org.prabal.scheduler.processor.StartupTriggerProcessor;
import org.prabal.scheduler.processor.TriggerDataMapProcessor;

import com.tangosol.config.xml.AbstractNamespaceHandler;

/**
 * @author Prabal Nandi
 *
 */
public class SchedulerNamespaceHandler extends AbstractNamespaceHandler {
   public static final String SERVICE_CONFIG = "service-config";
   public static final String SCHEDULER_SERVICE = "schedulerservice";
   public static final String SCHEDULER_NAME = "name";
   public static final String SCHEDULER_MAX_THREAD = "maxthreacount";
   public static final String SCHEDULER_IDLE_WAIT_TIME = "idlewaittimeinmillis";
   public static final String SCHEDULER_DISPATCHER_POLICY = "dispatcherpolicy";
   public static final String SCHEDULER_SINGLE_SERVER_MODE = "singleServerMode";
   public static final String JOB_LIST = "jobs";
   public static final String JOB_INSTANCE = "job";
   // JOB DETAILS XML ELEMENTS
   public static final String JOB_NAME = "jobname";
   public static final String JOB_GROUP = "jobgroup";
   public static final String JOB_DESCRIPTION = "description";
   public static final String JOB_CLASS = "jobclass";
   public static final String JOB_DURABLE = "durable";
   public static final String JOB_RECOVERABLE = "recoverable";
   public static final String JOB_DATA_MAP = "jobdatamap";

   // TRIGGER DETAILS XML ELEMENTS
   public static final String JOB_TRIGGER_LIST = "triggers";
   public static final String JOB_TRIGGER_INSTANCE = "trigger";
   public static final String JOB_TRIGGER_NAME = "triggername";
   public static final String JOB_TRIGGER_GROUP = "triggergroup";
   public static final String JOB_TRIGGER_DESCRIPTION = "description";
   public static final String JOB_TRIGGER_START_TIME = "starttime";
   public static final String JOB_TRIGGER_END_TIME = "endtime";
   public static final String JOB_TRIGGER_REPEAT_COUNT = "repeatcount";
   public static final String JOB_TRIGGER_REPEAT_INTERVAL = "repeatinterval";
   public static final String JOB_TRIGGER_DATA_MAP = "tiggerdatamap";
   public static final String JOB_TRIGGER_TYPE_SIMPLE = "simpletrigger";
   public static final String JOB_TRIGGER_TYPE_CRON = "crontrigger";
   public static final String JOB_TRIGGER_CRON_EXP = "cronexpression";
   public static final String JOB_TRIGGER_MISFIRE_POLICY = "misfirepolicy";

   public static final String RESOURCE_TIMERSERVICE_THREAD = "TimerServiceThread";
   public static final String RESOURCE_TIMERSERVICE_LISTENER = "TimerServiceLifecycleListener";
   public static final String RESOURCE_SCHEDULER_CONFIG = "SCHEDULER_CONFIG";
   public static final String RESOURCE_STARTUP_JOB_LIST = "STARTUP_JOB_LIST";

   public static final String DATA_MAP_ATTRIBUTE_KEY = "key";

   public SchedulerNamespaceHandler() {
      registerProcessor(ServiceConfigProcessor.class);
      registerProcessor(SchedulerServiceProcessor.class);
      registerProcessor(JobDataMapProcessor.class);
      registerProcessor(TriggerDataMapProcessor.class);

      registerProcessor(SimpleTriggerProcessor.class);
      registerProcessor(CronTriggerProcessor.class);

      registerProcessor(StartupTriggerListProcessor.class);
      registerProcessor(StartupTriggerProcessor.class);

      registerProcessor(StartupJobListProcessor.class);
      registerProcessor(StartupJobProcessor.class);
   }
}