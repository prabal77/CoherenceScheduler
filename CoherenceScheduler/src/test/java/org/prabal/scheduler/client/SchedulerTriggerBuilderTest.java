//package org.prabal.scheduler.client;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNotSame;
//import static org.junit.Assert.fail;
//
//import java.util.Calendar;
//import java.util.Map;
//import java.util.Properties;
//import java.util.TimeZone;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.prabal.scheduler.core.CronScheduleBuilder;
//import org.prabal.scheduler.core.ExecutionStatus;
//import org.prabal.scheduler.core.JobDetails;
//import org.prabal.scheduler.core.SimpleScheduleBuilder;
//import org.prabal.scheduler.core.Trigger;
//import org.prabal.scheduler.test.util.StartClusterServer;
//
//import com.tangosol.io.pof.annotation.Portable;
//import com.tangosol.net.CacheFactory;
//import com.tangosol.net.ExtensibleConfigurableCacheFactory;
//import com.tangosol.net.NamedCache;
//import com.tangosol.util.InvocableMap.Entry;
//import com.tangosol.util.processor.AbstractProcessor;
//
//public class SchedulerTriggerBuilderTest {
//
//   @BeforeClass
//   public static void setUpBeforeClass() throws Exception {
//      Properties properties = new Properties(System.getProperties());
//      properties.setProperty("tangosol.coherence.override", "/tangosol-coherence-override.xml");
//      properties.setProperty("tangosol.coherence.cacheconfig", "/test-scheduler-cache-config.xml");
//      properties.setProperty("tangosol.pof.config", "/test-scheduler-pof-config.xml");
//      properties.setProperty("tangosol.coherence.distributed.localstorage", "false");
//      System.setProperties(properties);
//   }
//
//   @AfterClass
//   public static void tearDownAfterClass() throws Exception {
//   }
//
//   @Before
//   public void setUp() throws Exception {
//   }
//
//   @After
//   public void tearDown() throws Exception {
//   }
//
//   @Test
//   public void test() {
//      try {
//         JobDetails dummyJob = CoherenceSchedulerFacadeTest.buildDummyJob(null, null);
//
//         SchedulerTriggerBuilder<Trigger> triggerBuilder = SchedulerTriggerBuilder.newTrigger();
//         triggerBuilder.forJob(dummyJob).withIdentity("TestTriggerName", "TestTriggerGroup").withPriority(4).startNow().withDescription("Dummy Trigger Description");
//         triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatMinutelyForever()).usingJobData("TestDataKey", "TestDataValue");
//
//         Trigger trigger = triggerBuilder.realize();
//
//         NamedCache cache = CacheFactory.getCache(StartClusterServer.Named_Cache);
//         cache.put(trigger.getKey(), trigger);
//         System.out.println("test: HashCode=" + trigger.getKey().hashCode() + " Return Data=" + cache.get(trigger.getKey()));
//         assertNotNull(cache.get(trigger.getKey()));
//         assertNotSame(cache.get(trigger.getKey()), trigger);
//      }
//      catch (Exception exception) {
//         exception.printStackTrace();
//         fail(exception.getMessage());
//      }
//   }
//   
//   @Test
//   public void testTimeZone(){
//      Properties properties = new Properties(System.getProperties());
//     // properties.setProperty("tangosol.coherence.override", "/tangosol-coherence-override.xml");
//      properties.setProperty("tangosol.coherence.cacheconfig", "/test-scheduler-cache-config.xml");
//      properties.setProperty("tangosol.pof.config", "/scheduler-pof-config.xml");
//      properties.setProperty("tangosol.coherence.distributed.localstorage", "true");
//      System.setProperties(properties);
//
//      ExtensibleConfigurableCacheFactory cacheFactory = new ExtensibleConfigurableCacheFactory(ExtensibleConfigurableCacheFactory.DependenciesHelper.newInstance("/test-scheduler-cache-config.xml"));
//      CacheFactory.setConfigurableCacheFactory(cacheFactory);
//      try{
//      NamedCache cache = CacheFactory.getCache("org.prabal.scheduler.JobSubmissionCache");
//      Calendar calendar = Calendar.getInstance();
//      calendar.setTimeInMillis(CacheFactory.getCluster().getTimeMillis());
//      System.out.println("cale "+calendar.getTime()+" timezone "+calendar.getTimeZone());
//      calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
//      System.out.println("After "+calendar.getTime()+" timezone "+calendar.getTimeZone());
//      cache.put("Test", TimeZone.getTimeZone("America/Los_Angeles"));
//      System.out.println(" "+cache.get("Test"));
//      }catch(Exception exception){
//         exception.printStackTrace();
//      }
//   }
//   
//   @Test
//   public void testCronJob(){
//      try{
//         JobDetails dummyJob = CoherenceSchedulerFacadeTest.buildDummyJob(null, null);
//         
//         SchedulerTriggerBuilder<Trigger> triggerBuilder = SchedulerTriggerBuilder.newTrigger();
//         triggerBuilder.forJob(dummyJob).withIdentity("TestTriggerName", "TestTriggerGroup").withPriority(4).startNow().withDescription("Dummy Trigger Description");
//         triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule("0 35 2 * * ?")).usingJobData("TestDataKey", "TestDataValue");
//         Trigger trigger = triggerBuilder.realize();
//         
//         System.out.println("Trigger "+trigger);
//         
//         NamedCache cache = CacheFactory.getCache(StartClusterServer.Named_Cache);
//         cache.put(trigger.getKey(), trigger);
//         System.out.println("INserterd");
//         Trigger trigger2 = (Trigger) cache.get(trigger.getKey());
//         System.out.println("test: HashCode=" + trigger.getKey().hashCode() + " Return Data=" + trigger2);
//        // assertNotNull(cache.get(trigger.getKey()));
//        // assertNotSame(cache.get(trigger.getKey()), trigger);
//         
//      }catch(Exception exception){
//         exception.printStackTrace();
//         fail(exception.getMessage());
//      }
//   }
//
//   @Test
//   public void TestLocking() {
//      final Trigger trigger = CoherenceSchedulerFacadeTest.buildDummyTrigger(CoherenceSchedulerFacadeTest.buildDummyJob(null, null));
//
//      final NamedCache cache = CacheFactory.getCache(StartClusterServer.Named_Cache);
//      cache.put(trigger.getKey(), trigger);
//
//      Runnable runnable = new Runnable() {
//
//         @Override
//         public void run() {
//            Map map = (Map) cache.invoke(trigger.getKey(), new LockProcessor());
//         }
//      };
//
//      for (int i = 0; i < 10; i++) {
//         Thread thread = new Thread(runnable);
//         thread.start();
//      }
//      System.out.println("AGain test: HashCode=" + trigger.getKey().hashCode() + " Return Data=" + cache.get(trigger.getKey()));
//   }
//
//   @Portable
//   public static class LockProcessor extends AbstractProcessor {
//
//      @Override
//      public Object process(Entry entry) {
//         Trigger trigger = (Trigger) entry.getValue();
//         System.out.println("Thread count " + trigger.getTriggerState());
//         if (trigger.getTriggerState() != ExecutionStatus.EXECUTING) {
//            System.out.println("Thread count inside= " + trigger.getTriggerState());
//            trigger.changeTriggerState(ExecutionStatus.EXECUTING);
//            entry.setValue(trigger);
//         }
//         return null;
//      }
//
//   }
//
//}
