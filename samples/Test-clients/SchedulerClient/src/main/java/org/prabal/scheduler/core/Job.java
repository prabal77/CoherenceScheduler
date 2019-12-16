/**
 * 
 */
package org.prabal.scheduler.core;

import com.tangosol.io.pof.annotation.Portable;


/**
 * @author Prabal Nandi
 *
 */
@Portable
public interface Job<V> extends Cloneable{

   public V execute(JobContext jobContext);
}
