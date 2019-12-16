/**
 * 
 */
package org.prabal.test.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.prabal.scheduler.core.Job;
import org.prabal.scheduler.core.JobContext;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Member;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class DummyJob implements Job<String> {
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS");

   @Override
   public String execute(JobContext jobContext) {
      Member member = CacheFactory.getCluster().getLocalMember();
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS");
      Date currentDate = new Date(System.currentTimeMillis());
      String dateString = dateFormat.format(currentDate);
      try {
         Thread.sleep(60000);
      }
      catch (InterruptedException exception) {
         // DO Nothing
      }
      return "RESULT: DummyJob Completed. Init Time = " + dateString;
   }

}