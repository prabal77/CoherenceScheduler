package org.prabal.scheduler.client;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.prabal.scheduler.core.ResultInstanceHolder;
import org.prabal.scheduler.core.ResultInstanceKey;
import org.prabal.scheduler.core.SubmissionResult;

public class TestClientDrain {

   public static void main(String[] args) {
      Properties properties = new Properties(System.getProperties());
      properties.setProperty("tangosol.coherence.cacheconfig", "/custom-cache-config.xml");
      properties.setProperty("tangosol.pof.config", "/custom-pof-config.xml");
      properties.setProperty("tangosol.coherence.distributed.localstorage", "false");
      System.setProperties(properties);
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY hh:ss:SSS");

      CoherenceSchedulerFacade coherenceSchedulerFacade = new CoherenceSchedulerFacade();
      SubmissionResult<String> submissionResult = coherenceSchedulerFacade.getResultInstance(new ResultInstanceKey("TestTrigger- 2", "DEFAULT"));
      for (ResultInstanceHolder<String> resultInstanceHolder2 : submissionResult.drainAllResult()) {
         System.out.println("Key = TestTrigger- 2 . Value = TimeStamp " + dateFormat.format(resultInstanceHolder2.getExecutionTimeDate()) + " ResultStatus " + resultInstanceHolder2.getResultStatus()
               + " result " + resultInstanceHolder2.getResult());
      }
      System.out.println("After Drain the size = " + submissionResult.getCompleteList().size());
   }

}