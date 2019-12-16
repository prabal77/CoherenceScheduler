/**
 * 
 */
package org.prabal.scheduler.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.prabal.scheduler.listener.LifecycleInterceptor;
import org.prabal.scheduler.listener.TimerServiceLifecycleListener;
import org.prabal.scheduler.namespacehandler.SchedulerNamespaceHandler;
import org.prabal.scheduler.processor.builder.ServiceConfigBuilder;
import org.prabal.scheduler.processor.config.ServiceConfig;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.net.events.InterceptorRegistry;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.Builder;
import com.tangosol.util.DaemonThreadFactory;
import com.tangosol.util.RegistrationBehavior;

/**
 * @author Prabal Nandi
 *
 */
@XmlSimpleName(SchedulerNamespaceHandler.SERVICE_CONFIG)
public class ServiceConfigProcessor implements ElementProcessor<ServiceConfig> {

   @SuppressWarnings("unchecked")
   @Override
   public ServiceConfig process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
      ServiceConfigBuilder serviceConfigBuilder = new ServiceConfigBuilder();
      context.inject(serviceConfigBuilder, xmlElement);

      TimerServiceBuilder timerServiceBuilder = new TimerServiceBuilder();
      TimerServiceLifecycleListener serviceLifecycleListener = new TimerServiceLifecycleListener();

      context.getResourceRegistry().registerResource(ExecutorService.class, SchedulerNamespaceHandler.RESOURCE_TIMERSERVICE_THREAD, timerServiceBuilder, RegistrationBehavior.IGNORE,
            serviceLifecycleListener);
      serviceConfigBuilder.setTimerServiceExecutor(context.getResourceRegistry().getResource(ExecutorService.class, SchedulerNamespaceHandler.RESOURCE_TIMERSERVICE_THREAD));

      InterceptorRegistry interceptorRegistry = context.getResourceRegistry().getResource(InterceptorRegistry.class);
      interceptorRegistry.registerEventInterceptor(SchedulerNamespaceHandler.RESOURCE_TIMERSERVICE_LISTENER, new LifecycleInterceptor(), RegistrationBehavior.IGNORE);

      return serviceConfigBuilder.realize();
   }

   public class TimerServiceBuilder implements Builder<ExecutorService> {

      @Override
      public ExecutorService realize() {
         DaemonThreadFactory daemonThreadFactory = new DaemonThreadFactory(SchedulerNamespaceHandler.RESOURCE_TIMERSERVICE_THREAD + "_" + ClusterInfoUtil.getLocalMemberIdShort());
         ExecutorService executorService = Executors.newSingleThreadExecutor(daemonThreadFactory);
         return executorService;
      }
   }

}
