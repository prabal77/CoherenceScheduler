/**
 * 
 */
package org.prabal.scheduler.core;

import java.util.HashMap;
import java.util.Map;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class SimpleJobContext implements JobContext<Object, Object> {
   @PortableProperty(0)
   private Map<Object, Object> dataMap;

   public SimpleJobContext() {
      super();
      this.dataMap = new HashMap<Object, Object>();
   }

   @Override
   public void addToDataMap(Object dataKey, Object dataValue) {
      this.dataMap.put(dataKey, dataValue);
   }

   @Override
   public void addAllToDataMap(Map<Object, Object> dataMap) {
      this.dataMap.putAll(dataMap);
   }

   @Override
   public Map<Object, Object> getDataMap() {
      return this.dataMap;
   }

}