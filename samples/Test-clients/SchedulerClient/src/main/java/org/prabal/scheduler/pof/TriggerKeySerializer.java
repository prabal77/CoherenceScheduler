
/**
 * 
 */
package org.prabal.scheduler.pof;

import java.io.IOException;

import org.prabal.scheduler.core.BaseKey;
import org.prabal.scheduler.core.JobKey;
import org.prabal.scheduler.core.TriggerKey;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

/**
 * @author Prabal Nandi
 *
 */
public class TriggerKeySerializer implements PofSerializer {
   private static int NAME = 0;
   private static int GROUP = 1;
   private static int TYPE = 2;
   private static int JOB_KEY = 3;

   @Override
   public Object deserialize(PofReader reader) throws IOException {
      String name = reader.readString(NAME);
      String group = reader.readString(GROUP);
      String type = reader.readString(TYPE);
      JobKey jobKey = (JobKey) reader.readObject(JOB_KEY);
      reader.readRemainder();
      if (type == null || !type.equalsIgnoreCase(BaseKey.TRIGGER_KEY))
         throw new IOException("Invalid Key Type passed");
      if (jobKey == null) {
         throw new IOException("No JobKey associated with this trigger");
      }
      return new TriggerKey(name, group, jobKey);
   }

   @Override
   public void serialize(PofWriter writer, Object obj) throws IOException {
      TriggerKey triggerKey = (TriggerKey) obj;
      writer.writeString(NAME, triggerKey.getName());
      writer.writeString(GROUP, triggerKey.getGroup());
      writer.writeString(TYPE, triggerKey.getType());
      writer.writeObject(JOB_KEY, triggerKey.getJobKey());
      writer.writeRemainder(null);
   }

}
