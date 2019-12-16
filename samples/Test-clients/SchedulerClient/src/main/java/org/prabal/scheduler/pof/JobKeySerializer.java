/**
 * 
 */
package org.prabal.scheduler.pof;

import java.io.IOException;

import org.prabal.scheduler.core.BaseKey;
import org.prabal.scheduler.core.JobKey;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

/**
 * @author Prabal Nandi
 *
 */
public class JobKeySerializer implements PofSerializer {
   private static int NAME = 0;
   private static int GROUP = 1;
   private static int TYPE = 2;

   @Override
   public Object deserialize(PofReader reader) throws IOException {
      String name = reader.readString(NAME);
      String group = reader.readString(GROUP);
      String type = reader.readString(TYPE);
      reader.readRemainder();
      if (type == null || !type.equalsIgnoreCase(BaseKey.JOB_KEY))
         throw new IOException("Invalid Key Type passed");
      return new JobKey(name, group);
   }

   @Override
   public void serialize(PofWriter writer, Object obj) throws IOException {
      JobKey jobKey = (JobKey) obj;
      writer.writeString(NAME, jobKey.getName());
      writer.writeString(GROUP, jobKey.getGroup());
      writer.writeString(TYPE, jobKey.getType());
      writer.writeRemainder(null);
   }

}
