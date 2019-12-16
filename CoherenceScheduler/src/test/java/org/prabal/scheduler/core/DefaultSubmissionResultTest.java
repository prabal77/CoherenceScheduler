package org.prabal.scheduler.core;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prabal.scheduler.core.DefaultSubmissionResult;
import org.prabal.scheduler.core.ResultInstanceHolder;

public class DefaultSubmissionResultTest {
   private static DefaultSubmissionResult<String> testObject;
   private static Timestamp lowLimit;
   private static Timestamp highLimit;
   private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss:SSS");

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      testAddResultInstance();
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

   public static void testAddResultInstance() {
      System.out.println("\n");
      testObject = new DefaultSubmissionResult<String>();
      for (int i = 0; i < 10; i++) {
         Timestamp time = new Timestamp((long) (10000000 * (i + 1) * Math.random()));
         if (i == 3) {
            lowLimit = time;
         }
         else if (i == 8) {
            highLimit = time;
         }
         testObject.addResultInstance(new ResultInstanceHolder<String>(ResultInstanceHolder.SUCCESS, time.toString(), time.getTime(), new TriggerKey("TestName", null)));
      }
      for (ResultInstanceHolder<String> obj : testObject.getCompleteList()) {
         System.out.println("testAddResultInstance " + dateFormat.format(obj.getExecutionTimeDate()) + " instance " + obj.getResult());
      }
      System.out.println("Min Time " + lowLimit + " High Limit " + highLimit);
   }

   @Test
   public void testGetCompleteList() {
      System.out.println("\n");
      for (ResultInstanceHolder<String> obj : testObject.getCompleteList()) {
         System.out.println("obj " + dateFormat.format(obj.getExecutionTimeDate()) + " instance " + obj.getResult());
      }
   }

   @Test
   public void testGetLatestResult() {
      System.out.println("\n");
      System.out.println("Lastest Result " + testObject.getLatestResult().getResult());
   }

   @Test
   public void testGetOldestResult() {
      System.out.println("\n");
      System.out.println("Oldest Result " + testObject.getOldestResult().getResult());
   }

   @Test
   public void testGetResultBetweenBothNull() {
      System.out.println("\n");
      for (ResultInstanceHolder<String> obj : testObject.getResultBetween(null, null)) {
         System.out.println("testGetResultBetweenBothNull " + dateFormat.format(obj.getExecutionTimeDate()) + " instance " + obj.getResult());
      }
   }

   @Test
   public void testGetResultBetweenLowNull() {
      System.out.println("\n");

      for (ResultInstanceHolder<String> obj : testObject.getResultBetween(null, highLimit)) {
         System.out.println("testGetResultBetweenLowNull " + dateFormat.format(obj.getExecutionTimeDate()) + " instance " + obj.getResult());
      }
   }

   @Test
   public void testGetResultBetweenHighNull() {
      System.out.println("\n");
      for (ResultInstanceHolder<String> obj : testObject.getResultBetween(lowLimit, null)) {
         System.out.println("testGetResultBetweenHighNull " + dateFormat.format(obj.getExecutionTimeDate()) + " instance " + obj.getResult());
      }
   }

   @Test
   public void testGetResultBetween() {
      System.out.println("\n");
      for (ResultInstanceHolder<String> obj : testObject.getResultBetween(lowLimit, highLimit)) {
         System.out.println("testGetResultBetween " + dateFormat.format(obj.getExecutionTimeDate()) + " instance " + obj.getResult());
      }
   }

   // public static class TestResult implements ResultInstance<String> {
   // private String name;
   //
   // public TestResult(String name) {
   // this.name = name;
   // }
   //
   // @Override
   // public void readExternal(PofReader arg0) throws IOException {
   // // TODO Auto-generated method stub
   //
   // }
   //
   // @Override
   // public void writeExternal(PofWriter arg0) throws IOException {
   // // TODO Auto-generated method stub
   //
   // }
   //
   // @Override
   // public String getResult() {
   // return this.name;
   // }
   //
   // }

}