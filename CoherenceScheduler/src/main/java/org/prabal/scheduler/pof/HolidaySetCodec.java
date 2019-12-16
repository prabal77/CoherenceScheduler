/**
 * 
 */
package org.prabal.scheduler.pof;

import java.io.IOException;
import java.util.Date;
import java.util.TreeSet;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.reflect.Codec;

/**
 * @author Prabal Nandi
 *
 */
public class HolidaySetCodec implements Codec {

   @Override
   public Object decode(PofReader reader, int index) throws IOException {
      return reader.readCollection(index, new TreeSet<Date>());
   }

   @Override
   public void encode(PofWriter writer, int index, Object obj) throws IOException {
      @SuppressWarnings("unchecked")
      TreeSet<Date> treeSet = (TreeSet<Date>) obj;
      writer.writeCollection(index, treeSet);
   }
}
