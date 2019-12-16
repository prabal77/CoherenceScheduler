/**
 * 
 */
package org.prabal.scheduler.pof;

import java.io.IOException;

import org.prabal.scheduler.core.ExecutionStatus;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.reflect.Codec;

/**
 * @author Prabal Nandi
 *
 */
public class ExecutionStateCodec implements Codec {

   @Override
   public Object decode(PofReader reader, int index) throws IOException {
      String read = reader.readString(index);
      ExecutionStatus returnExecutionStatus = null;
      if (read != null) {
         returnExecutionStatus = ExecutionStatus.valueOf(read);
      }
      return returnExecutionStatus;
   }

   @Override
   public void encode(PofWriter writer, int index, Object obj) throws IOException {
      String enumName = null;
      if (obj != null) {
         enumName = ((ExecutionStatus) obj).name();
      }
      writer.writeString(index, enumName);
   }
}
