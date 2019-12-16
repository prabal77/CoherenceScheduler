/**
 * 
 */
package org.prabal.scheduler.listener;

import com.tangosol.util.ResourceRegistry;

/**
 * @author Prabal Nandi
 *
 */
public class TimerServiceLifecycleListener implements ResourceRegistry.ResourceLifecycleObserver {

   @Override
   public void onRelease(Object obj) {
      System.out.println("Releasing obj = " + obj);
   }

}
