/**
 * 
 */
package org.prabal.scheduler.test.util;

import java.util.Properties;

import org.junit.Test;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.DefaultCacheServer;
import com.tangosol.net.ExtensibleConfigurableCacheFactory;

/**
 * @author praba_000
 *
 */
public class StartClusterServer {
   private static DefaultCacheServer cacheServer = null;
   public static final String Named_Cache = "org.prabal.scheduler.JobSubmissionCache";

   /**
    * @param args
    */
   @Test
   public void startTestCluster() {
      Properties properties = new Properties(System.getProperties());
     // properties.setProperty("tangosol.coherence.override", "/tangosol-coherence-override.xml");
      properties.setProperty("tangosol.coherence.cacheconfig", "/test-scheduler-cache-config.xml");
      properties.setProperty("tangosol.pof.config", "/scheduler-pof-config.xml");
      properties.setProperty("tangosol.coherence.distributed.localstorage", "true");
      System.setProperties(properties);

      ExtensibleConfigurableCacheFactory cacheFactory = new ExtensibleConfigurableCacheFactory(ExtensibleConfigurableCacheFactory.DependenciesHelper.newInstance("/test-scheduler-cache-config.xml"));
      CacheFactory.setConfigurableCacheFactory(cacheFactory);
      cacheServer = new DefaultCacheServer(cacheFactory);
      cacheServer.startAndMonitor(DefaultCacheServer.DEFAULT_WAIT_MILLIS);
   }

   @Test
   public void stopTestCluster() {
      cacheServer.shutdownServer();
   }

}
