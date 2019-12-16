package org.prabal.scheduler.client;

import java.util.Properties;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.test.util.StartClusterServer;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

public class SchedulerJobBuilderTest {

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      Properties properties = new Properties(System.getProperties());
      properties.setProperty("tangosol.coherence.override", "/tangosol-coherence-override.xml");
      properties.setProperty("tangosol.coherence.cacheconfig", "/test-scheduler-cache-config.xml");
      properties.setProperty("tangosol.pof.config", "/test-scheduler-pof-config.xml");
      properties.setProperty("tangosol.coherence.distributed.localstorage", "false");
      System.setProperties(properties);
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
   public void test() {
      try {
         SchedulerJobBuilder jobBuilder = SchedulerJobBuilder.newJob();
         jobBuilder.ofType(TestJob.class).withIdentity("TestName", "TestGroup").withDescription("TestDescription");
         jobBuilder.usingJobData("TestKey1", "TestValue1").usingJobData("TestKey2", "TestValue2");
         JobDetails jobDetails = jobBuilder.realize();
         NamedCache cache = CacheFactory.getCache(StartClusterServer.Named_Cache);
         cache.put(jobDetails.getJobKey(), jobDetails);
         System.out.println("test: HashCode="+jobDetails.getJobKey().hashCode()+" Return Data=" + cache.get(jobDetails.getJobKey()));
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Test
   public void testDifferentKey() {
      SchedulerJobBuilder jobBuilder = SchedulerJobBuilder.newJob();
      jobBuilder.ofType(TestJob.class).withIdentity("TestName", "TestGroup").withDescription("TestDescription");
      jobBuilder.usingJobData("TestKey1", "TestValue1").usingJobData("TestKey2", "TestValue2");
      JobDetails jobDetails = jobBuilder.realize();
      NamedCache cache = CacheFactory.getCache(StartClusterServer.Named_Cache);
      cache.put(jobDetails.getJobKey(), jobDetails);

      SchedulerJobBuilder jobBuilder2 = SchedulerJobBuilder.newJob();
      jobBuilder2.ofType(TestJob.class).withIdentity("TestName", "TestGroup2").withDescription("TestDescription");
      jobBuilder2.usingJobData("TestKey1", "TestValue1").usingJobData("TestKey2", "TestValue2");
      JobDetails jobDetails2 = jobBuilder2.realize();
      System.out.println("testDifferentKey: HashCode="+jobDetails2.getJobKey().hashCode()+" Return Data=" + cache.get(jobDetails2.getJobKey()));
   }

}
