/**
 * 
 */
package org.prabal.scheduler.core;

import java.util.Map;

import com.tangosol.io.pof.annotation.Portable;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public interface JobContext<K, V> {

   public void addToDataMap(K dataKey, V dataValue);

   public void addAllToDataMap(Map<K, V> dataMap);

   public Map<K, V> getDataMap();

}
