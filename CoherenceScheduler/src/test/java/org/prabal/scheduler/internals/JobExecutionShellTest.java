package org.prabal.scheduler.internals;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prabal.scheduler.core.JobContext;
import org.prabal.scheduler.processor.config.SchedulerServiceConfig;

public class JobExecutionShellTest {
   private static JobExecutionShell executionShell = null;

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      SchedulerServiceConfig schedulerServiceConfig = new SchedulerServiceConfig();
      schedulerServiceConfig.setName("TestScheduler");
      schedulerServiceConfig.setMaxThreadCount(2);
      executionShell = new JobExecutionShell(schedulerServiceConfig);
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testStartShell() {
      try {
         executionShell.startShell();
         assertTrue(executionShell.isStartedFlag());
         testSubmitJob();
         while (executionShell.isStartedFlag())
            ;
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void testSubmitJob() {
      if (!executionShell.isStartedFlag())
         executionShell.startShell();
      for (int i = 0; i < 10; i++) {
         JobExecutionPayload executionPayload = new JobExecutionPayload();
         executionPayload.setJobContext(new TestJobContext("JobCOntext " + i));
         try {
            executionShell.submitJob(executionPayload);
         }
         catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   @Test
   public void testShutDown() {
      testSubmitJob();
      try {
         Thread.sleep(10000);
      }
      catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      Thread thread = new Thread() {
         @Override
         public void run() {
            for (int i = 0; i < 100; i++) {
               JobExecutionPayload executionPayload = new JobExecutionPayload();
               executionPayload.setJobContext(new TestJobContext("JobCOntext " + i));
               try {
                  executionShell.submitJob(executionPayload);
               }
               catch (Throwable e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }
         }
      };
      thread.start();
      executionShell.shutDown(false);
      assertFalse(executionShell.isStartedFlag());
   }

   @Test
   public void testShutDownForce() {
      testSubmitJob();
      try {
         Thread.sleep(10000);
      }
      catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      Thread thread = new Thread() {
         @Override
         public void run() {
            for (int i = 0; i < 100; i++) {
               JobExecutionPayload executionPayload = new JobExecutionPayload();
               executionPayload.setJobContext(new TestJobContext("JobCOntext " + i));
               try {
                  executionShell.submitJob(executionPayload);
               }
               catch (Throwable e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }
         }
      };
      thread.start();
      executionShell.shutDown(true);
      assertFalse(executionShell.isStartedFlag());
   }

   public class TestJobContext implements JobContext {
      private String name;

      public TestJobContext(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return "TestJobContext [name=" + name + "]";
      }

      @Override
      public void addToDataMap(Object dataKey, Object dataValue) {
         // TODO Auto-generated method stub

      }

      @Override
      public void addAllToDataMap(Map dataMap) {
         // TODO Auto-generated method stub

      }

      @Override
      public Map getDataMap() {
         // TODO Auto-generated method stub
         return null;
      }
   }

}
