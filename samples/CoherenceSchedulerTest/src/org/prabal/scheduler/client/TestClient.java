/**
 * 
 */
package org.prabal.scheduler.client;

import java.text.SimpleDateFormat;
import java.util.Properties;

import org.prabal.scheduler.core.ResultInstanceHolder;
import org.prabal.scheduler.core.ResultInstanceKey;
import org.prabal.scheduler.core.SubmissionResult;

/**
 * @author Prabal Nandi
 *
 */
public class TestClient {

   /**
    * @param args
    */
   public static void main(String[] args) {
      Properties properties = new Properties(System.getProperties());
      properties.setProperty("tangosol.coherence.cacheconfig", "/custom-cache-config.xml");
      properties.setProperty("tangosol.pof.config", "/custom-pof-config.xml");
      properties.setProperty("tangosol.coherence.distributed.localstorage", "false");
      System.setProperties(properties);
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY hh:ss:SSS");

      CoherenceSchedulerFacade coherenceSchedulerFacade = new CoherenceSchedulerFacade();
      int count = 0;

      while (true) {
         if (count >= 6)
            break;
         System.out.println("***************** Starting new Iteration***************");
         SubmissionResult<String> submissionResult = coherenceSchedulerFacade.getResultInstanceReadOnly(new ResultInstanceKey("{Job-1 - Trigger 1}", "DEFAULT"));
         for (ResultInstanceHolder<String> resultInstanceHolder : submissionResult.getCompleteList()) {
            System.out.println("Key = {Job-1 - Trigger 1} . Value = TimeStamp " + dateFormat.format(resultInstanceHolder.getExecutionTimeDate()) + " ResultStatus "
                  + resultInstanceHolder.getResultStatus() + " result " + resultInstanceHolder.getResult());
         }
         System.out.println("+++++++++ Second Object");
         SubmissionResult<String> submissionResult2 = coherenceSchedulerFacade.getResultInstanceReadOnly(new ResultInstanceKey("TestTrigger- 2", "DEFAULT"));
         for (ResultInstanceHolder<String> resultInstanceHolder2 : submissionResult2.getCompleteList()) {
            System.out.println("Key = TestTrigger- 2 . Value = TimeStamp " + dateFormat.format(resultInstanceHolder2.getExecutionTimeDate()) + " ResultStatus "
                  + resultInstanceHolder2.getResultStatus() + " result " + resultInstanceHolder2.getResult());
         }
         System.out.println("+++++++++ Third Object");
         SubmissionResult<String> submissionResult3 = coherenceSchedulerFacade.getResultInstanceReadOnly(new ResultInstanceKey("{Job-1 - Trigger 2}", "DEFAULT"));
         for (ResultInstanceHolder<String> resultInstanceHolder3 : submissionResult3.getCompleteList()) {
            System.out.println("Key = {Job-1 - Trigger 2} . Value = TimeStamp " + dateFormat.format(resultInstanceHolder3.getExecutionTimeDate()) + " ResultStatus "
                  + resultInstanceHolder3.getResultStatus() + " result " + resultInstanceHolder3.getResult());
         }
         ++count;
         try {
            Thread.currentThread().sleep(60000);
         }
         catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         submissionResult2.drainAllResult();
      }
   }
}