/**
 * 
 */
package org.prabal.scheduler.client;

import org.prabal.scheduler.core.Job;
import org.prabal.scheduler.core.JobContext;

/**
 * @author Prabal Nandi
 *
 */
public class TestJob implements Job<String>{

   @Override
   public String execute(JobContext jobContext) {
      System.out.println("Executing");
      return "";
   }

}
