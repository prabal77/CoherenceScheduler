/**
 * 
 */
package org.prabal.scheduler.pof;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.prabal.scheduler.core.ExecutionStatus;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.reflect.Codec;

/**
 * @author Prabal Nandi
 *
 */
public class ExecutionStatusListCodec implements Codec {

   @SuppressWarnings("unchecked")
   @Override
   public Object decode(PofReader reader, int index) throws IOException {
      return (List<ExecutionStatus>) reader.readCollection(index, new ArrayList<ExecutionStatus>());
   }

   @SuppressWarnings("unchecked")
   @Override
   public void encode(PofWriter writer, int index, Object obj) throws IOException {
      writer.writeCollection(index, (List<ExecutionStatus>) obj);
   }

}
