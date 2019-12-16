/**
 * 
 */
package org.prabal.scheduler.pof;

import java.io.IOException;
import java.text.ParseException;
import java.util.TimeZone;

import org.prabal.scheduler.triggers.CronExpression;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.Base;

/**
 * @author Prabal Nandi
 *
 */
public class CronExpressionSerializer implements PofSerializer {
   private static final int CRON_EXPERSSION = 0;
   private static final int TIME_ZONE = 1;

   @Override
   public Object deserialize(PofReader reader) throws IOException {
      String cronExperssionString = reader.readString(CRON_EXPERSSION);
      String timeZoneString = reader.readString(TIME_ZONE);
      reader.readRemainder();
      CronExpression cronExpression = null;
      try {
         cronExpression = new CronExpression(cronExperssionString);
         cronExpression.setTimeZone(TimeZone.getTimeZone(timeZoneString));
      }
      catch (ParseException exception) {
         Base.ensureRuntimeException(exception, "Error reading cronexperssion from POF reader");
      }
      return cronExpression;
   }

   @Override
   public void serialize(PofWriter writer, Object cronexpression) throws IOException {
      CronExpression expression = (CronExpression) cronexpression;
      writer.writeString(CRON_EXPERSSION, expression.getCronExpression());
      writer.writeString(TIME_ZONE, expression.getTimeZone().getID());
      writer.writeRemainder(null);
   }

}
