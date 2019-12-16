/**
 * 
 */
package org.prabal.scheduler.internals;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prabal.scheduler.client.SchedulerTriggerBuilder;
import org.prabal.scheduler.core.JobDetails;
import org.prabal.scheduler.core.SimpleScheduleBuilder;
import org.prabal.scheduler.core.Trigger;
import org.prabal.scheduler.core.TriggerKey;

/**
 * @author Prabal Nandi
 *
 */
public class TriggerListHolderTest {
   private TriggerListHolder triggerListHolder;
   private List<Trigger> triggerInstanceList = new ArrayList<Trigger>();

   /**
    * @throws java.lang.Exception
    */
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   /**
    * @throws java.lang.Exception
    */
   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      triggerListHolder = buildTriggerListHolder();
   }

   /**
    * @throws java.lang.Exception
    */
   @After
   public void tearDown() throws Exception {
   }

   /**
    * Test method for
    * {@link org.prabal.scheduler.internals.TriggerListHolder#addTriggerToList(org.prabal.scheduler.core.Trigger)}
    * .
    */
   @Test
   public void testAddTriggerToList() {
      for (int i = 0; i < 10; i++) {
         Trigger triggerInstance = buildDummyTrigger("Name = " + i, "Group = " + i);
         triggerListHolder.addTriggerToList(triggerInstance);
      }

      System.out.println("Priniting ListHolder " + triggerListHolder.toStringForTest());
   }

   /**
    * Test method for
    * {@link org.prabal.scheduler.internals.TriggerListHolder#removeTriggerFromList(org.prabal.scheduler.core.Trigger)}
    * .
    */
   @Test
   public void testRemoveTriggerFromListTrigger() {
      triggerListHolder.removeTriggerFromList(buildDummyTrigger("Name = 3", "Group = 3"));
      System.out.println("Printing data " + triggerListHolder.toStringForTest());
   }

   /**
    * Test method for
    * {@link org.prabal.scheduler.internals.TriggerListHolder#removeTriggerFromList(org.prabal.scheduler.core.TriggerKey)}
    * .
    */
   @Test
   public void testRemoveTriggerFromListTriggerKey() {
      triggerListHolder.removeTriggerFromList(buildDummyTrigger("Name = 4", "Group = 4").getKey());
      System.out.println("Printing data " + triggerListHolder.toStringForTest());
   }

   /**
    * Test method for
    * {@link org.prabal.scheduler.internals.TriggerListHolder#testRetainAllTriggersKey(java.util.Set)}
    * .
    */
   @Test
   public void testRetainAllTriggersKey() {
      HashSet<TriggerKey> inputSet = new HashSet<TriggerKey>();
      inputSet.add(buildDummyTrigger("Name = 4", "Group = 4").getKey());
      inputSet.add(buildDummyTrigger("Name = 6", "Group = 6").getKey());
      inputSet.add(buildDummyTrigger("Name = 8", "Group = 8").getKey());
      this.triggerListHolder.retainAllTriggers(inputSet);
      System.out.println("" + this.triggerListHolder.toStringForTest());
   }

   /**
    * Test method for
    * {@link org.prabal.scheduler.internals.TriggerListHolder#retainAllTriggers(java.util.Set)}.
    */
   @Test
   public void testRetainAllTriggers() {
      this.triggerListHolder.retainAllTriggers(triggerInstanceList.subList(4, 8));
      System.out.println("" + this.triggerListHolder.toStringForTest());
   }

   /**
    * Test method for {@link org.prabal.scheduler.internals.TriggerListHolder#isEmpty()}.
    */
   @Test
   public void testIsEmpty() {
      fail("Not yet implemented");
   }

   /**
    * Test method for
    * {@link org.prabal.scheduler.internals.TriggerListHolder#getEligibleTriggerList()}.
    */
   @Test
   public void testGetEligibleTriggerList() {
      System.out.println("" + triggerListHolder.getEligibleTriggerList());
   }

   /**
    * Test method for {@link org.prabal.scheduler.internals.TriggerListHolder#getTriggerKeySet()}.
    */
   @Test
   public void testGetTriggerKeySet() {
      System.out.println("" + triggerListHolder.getTriggerKeySet());
   }

   /**
    * Test method for {@link org.prabal.scheduler.internals.TriggerListHolder#sortByNextFireTime()}.
    */
   @Test
   public void testSortByNextFireTime() {
      fail("Not yet implemented");
   }

   private TriggerListHolder buildTriggerListHolder() {
      TriggerListHolder listHolder = new TriggerListHolder();

      for (int i = 0; i < 10; i++) {
         Trigger triggerInstance = buildDummyTrigger("Name = " + i, "Group = " + i);
         triggerInstanceList.add(triggerInstance);
         listHolder.addTriggerToList(triggerInstance);
      }
      return listHolder;
   }

   public Trigger buildDummyTrigger(String name, String group) {
      SchedulerTriggerBuilder<Trigger> triggerBuilder = SchedulerTriggerBuilder.newTrigger();
      triggerBuilder.forJob(name, group).withIdentity(name, group).withPriority(4).startNow().withDescription("Dummy Trigger Description");
      triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(60000)).usingJobData("TestDataKey", "TestDataValue");
      return triggerBuilder.realize();
   }

}
