///**
// * 
// */
//package org.prabal.scheduler.client;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNotSame;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.Properties;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.prabal.scheduler.core.HolidayCalendar;
//import org.prabal.scheduler.core.JobDetails;
//import org.prabal.scheduler.core.SimpleScheduleBuilder;
//import org.prabal.scheduler.core.Trigger;
//import org.prabal.test.jobs.PrintDataInvocable;
//
//import com.tangosol.net.CacheFactory;
//import com.tangosol.net.InvocationService;
//
///**
// * @author Prabal Nandi
// *
// */
//public class CoherenceSchedulerFacadeTest {
//
//   /**
//    * @throws java.lang.Exception
//    */
//   @BeforeClass
//   public static void setUpBeforeClass() throws Exception {
//      Properties properties = new Properties(System.getProperties());
//      // properties.setProperty("tangosol.coherence.override", "/tangosol-coherence-override.xml");
//
//      properties.setProperty("tangosol.coherence.cacheconfig", "/test-scheduler-cache-config.xml");
//      properties.setProperty("tangosol.pof.config", "/test-scheduler-pof-config.xml");
//      properties.setProperty("tangosol.coherence.distributed.localstorage", "false");
//      System.setProperties(properties);
//   }
//
//   /**
//    * @throws java.lang.Exception
//    */
//   @AfterClass
//   public static void tearDownAfterClass() throws Exception {
//   }
//
//   /**
//    * @throws java.lang.Exception
//    */
//   @Before
//   public void setUp() throws Exception {
//   }
//
//   /**
//    * @throws java.lang.Exception
//    */
//   @After
//   public void tearDown() throws Exception {
//   }
//
//   /**
//    * Test method for
//    * {@link org.prabal.scheduler.client.CoherenceSchedulerFacade#scheduleJob(org.prabal.scheduler.core.JobDetails, org.prabal.scheduler.core.Trigger)}
//    * .
//    */
//   @Test
//   public void testScheduleJobJobDetailsOfObjectObjectTrigger() {
//      CoherenceSchedulerFacade coherenceSchedulerFacade = new CoherenceSchedulerFacade();
//      JobDetails<Object, Object> jobDetails = buildDummyJob(null, null);
//      Trigger trigger = buildDummyTrigger(jobDetails);
//
//      coherenceSchedulerFacade.scheduleJob(buildDummyJob(null, null), buildDummyTrigger(buildDummyJob(null, null)));
//
//      assertNotNull(coherenceSchedulerFacade.getNamedCache().get(jobDetails.getJobKey()));
//      assertNotSame(coherenceSchedulerFacade.getNamedCache().get(jobDetails.getJobKey()), jobDetails);
//      assertNotNull(coherenceSchedulerFacade.getNamedCache().get(trigger.getKey()));
//      assertNotSame(coherenceSchedulerFacade.getNamedCache().get(trigger.getKey()), trigger);
//   }
//
//   /**
//    * Test method for
//    * {@link org.prabal.scheduler.client.CoherenceSchedulerFacade#scheduleJob(org.prabal.scheduler.core.Trigger)}
//    * .
//    */
//   @Test
//   public void testScheduleJobTrigger() {
//      CoherenceSchedulerFacade coherenceSchedulerFacade = new CoherenceSchedulerFacade();
//      Trigger trigger = buildDummyTrigger(buildDummyJob(null, null));
//
//      coherenceSchedulerFacade.scheduleJob(buildDummyTrigger(buildDummyJob(null, null)));
//
//      assertNotNull(coherenceSchedulerFacade.getNamedCache().get(trigger.getKey()));
//      assertNotSame(coherenceSchedulerFacade.getNamedCache().get(trigger.getKey()), trigger);
//   }
//
//   @Test
//   public void testKeyAssociation() {
//      CoherenceSchedulerFacade coherenceSchedulerFacade = new CoherenceSchedulerFacade();
//      // coherenceSchedulerFacade.scheduleJob(buildDummyTrigger(buildDummyJob(null, null)));
//      boolean flag = true;
//      for (int i = 0; i < 40; i++) {
//         JobDetails jobDetails = null;
//         if (flag) {
//            jobDetails = buildDummyJob("" + i, "" + i);
//            flag = false;
//         }
//         else {
//            jobDetails = buildDummyJob("" + i, "" + i, i);
//            flag = true;
//         }
//         Trigger trigger = buildDummyTrigger(jobDetails);
//         // if (i == 4 || i == 20) {
//         // try {
//         // Thread.sleep(10000);
//         // }
//         // catch (InterruptedException e) {
//         // // TODO Auto-generated catch block
//         // e.printStackTrace();
//         // }
//         // }
//         coherenceSchedulerFacade.scheduleJob(jobDetails, trigger);
//      }
//      // printDataNodeWise();
//   }
//
//   @Test
//   public void testStoreCalendar() {
//      CoherenceSchedulerFacade coherenceSchedulerFacade = new CoherenceSchedulerFacade();
//      HolidayCalendar holidayCalendar = new HolidayCalendar();
//      // fourth of July (July 4)
//      Calendar fourthOfJuly = new GregorianCalendar(2005, 6, 4);
//      Date date = new Date(fourthOfJuly.getTimeInMillis());
//      holidayCalendar.addExcludedDate(date);
//      // halloween (Oct 31)
//      Calendar halloween = new GregorianCalendar(2005, 9, 31);
//      date = new Date(halloween.getTimeInMillis());
//      holidayCalendar.addExcludedDate(date);
//      // christmas (Dec 25)
//      Calendar christmas = new GregorianCalendar(2005, 11, 25);
//      date = new Date(christmas.getTimeInMillis());
//      holidayCalendar.addExcludedDate(date);
//      coherenceSchedulerFacade.storeCalendar("Holiday", holidayCalendar, true);
//      printDataNodeWise();
//      Calendar myBday = new GregorianCalendar(2014, 07, 14);
//      date = new Date(myBday.getTimeInMillis());
//      holidayCalendar.addExcludedDate(date);
//
//      coherenceSchedulerFacade.storeCalendar("Holiday", holidayCalendar, true);
//      printDataNodeWise();
//   }
//
//   @Test
//   public void printDataNodeWise() {
//      PrintDataInvocable dataInvocable = new PrintDataInvocable();
//      InvocationService invocationService = (InvocationService) CacheFactory.getService("TestInvocationService");
//      invocationService.execute(dataInvocable, null, null);
//   }
//
//   public static JobDetails buildDummyJob(String name, String group) {
//      name = (name == null || name.equals("")) ? "TestName" : name;
//      group = (group == null || group.equals("")) ? "TestGroup" : group;
//
//      SchedulerJobBuilder jobBuilder = SchedulerJobBuilder.newJob();
//      jobBuilder.ofType(org.prabal.test.jobs.DummyJob.class).withIdentity(name, group).withDescription("TestDescription");
//      jobBuilder.usingJobData("" + name, "" + group);
//      return jobBuilder.realize();
//   }
//
//   public static JobDetails buildDummyJob(String name, String group, int second) {
//      name = (name == null || name.equals("")) ? "Second TestName" : name;
//      group = (group == null || group.equals("")) ? "Second TestGroup" : group;
//
//      SchedulerJobBuilder jobBuilder = SchedulerJobBuilder.newJob();
//      jobBuilder.ofType(org.prabal.test.jobs.AnotherDummyJob.class).withIdentity(name, group).withDescription("TestDescription");
//      jobBuilder.usingJobData("" + name, "" + group);
//      return jobBuilder.realize();
//   }
//
//   public static Trigger buildDummyTrigger(JobDetails dummyJob) {
//      SchedulerTriggerBuilder<Trigger> triggerBuilder = SchedulerTriggerBuilder.newTrigger();
//      triggerBuilder.forJob(dummyJob).withIdentity(dummyJob.getJobKey().getName(), dummyJob.getJobKey().getGroup()).withPriority(4).startNow().withDescription("Dummy Trigger Description");
//      triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatMinutelyForTotalCount(2, 1)).usingJobData("TestDataKey", "TestDataValue");
//      return triggerBuilder.realize();
//   }
//}