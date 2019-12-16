package org.prabal.scheduler.util;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClusterInfoUtilTest {

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      Properties properties = new Properties(System.getProperties());
     // properties.setProperty("tangosol.coherence.override", "/tangosol-coherence-override.xml");
      properties.setProperty("tangosol.coherence.cacheconfig", "/test-scheduler-cache-config.xml");
      properties.setProperty("tangosol.pof.config", "/scheduler-pof-config.xml");
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
   public void testGetCurrentClusterTime() {
      Date date = ClusterInfoUtil.getCurrentClusterTime();
      assertNotNull(date);
      System.out.println("Date Returned "+date);
   }

   @Test
   public void testGetLocalMemberIdString() {
      fail("Not yet implemented");
   }

   @Test
   public void testGetLocalMemberIdShort() {
      fail("Not yet implemented");
   }

   @Test
   public void testGetOtherMemberIdShort() {
      fail("Not yet implemented");
   }

   @Test
   public void testGenerateTaskProcessorKey() {
      fail("Not yet implemented");
   }

   @Test
   public void testGenerateTaskInstanceId() {
      fail("Not yet implemented");
   }

   @Test
   public void testIsLocalStorageEnabledMember() {
      fail("Not yet implemented");
   }

   @Test
   public void testGetLocalMember() {
      fail("Not yet implemented");
   }

}
