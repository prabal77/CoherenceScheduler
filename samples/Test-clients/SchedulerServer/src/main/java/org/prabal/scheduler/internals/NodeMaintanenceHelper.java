/**
 * 
 */
package org.prabal.scheduler.internals;

import org.prabal.scheduler.internals.processors.AddRemoveMemberProcessor;
import org.prabal.scheduler.util.ClusterInfoUtil;

import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.DistributedCacheService;
import com.tangosol.net.Member;
import com.tangosol.net.MemberEvent;
import com.tangosol.net.MemberListener;
import com.tangosol.net.NamedCache;
import com.tangosol.util.ResourceRegistry;
import com.tangosol.util.filter.PresentFilter;
import com.tangosol.util.processor.ConditionalRemove;

/**
 * @author Prabal Nandi
 *
 */
public class NodeMaintanenceHelper {
   private ConfigurableCacheFactory cacheFactory;
   private NamedCache memberShipStoreCache;

   public NodeMaintanenceHelper(ConfigurableCacheFactory cacheFactory, NamedCache memberShipStoreCache) {
      this.cacheFactory = cacheFactory;
      this.memberShipStoreCache = memberShipStoreCache;
   }

   /**
    * This method add MemberListener to MemberShipCache and also
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void handleNodeStartup() {
      ResourceRegistry resourceRegistry = this.cacheFactory.getResourceRegistry();
      DistributedCacheService memberShipStoreService = (DistributedCacheService) this.memberShipStoreCache.getCacheService();
      Member localMember = memberShipStoreService.getCluster().getLocalMember();

      memberShipStoreService.addMemberListener(new JobStoreMemberListener(this.memberShipStoreCache));

      TaskProcessorKey taskProcessorKey = ClusterInfoUtil.generateTaskProcessorKey(localMember);
      resourceRegistry.registerResource(TaskProcessorKey.class, taskProcessorKey);

      this.memberShipStoreCache.invoke(SchedulerContantsEnum.PROCESSING_MEMEBER_SET.getConstantValue(), new AddRemoveMemberProcessor(AddRemoveMemberProcessor.ADD_PROCESSING_MEMBER, taskProcessorKey));
   }

   public class JobStoreMemberListener implements MemberListener {
      private NamedCache memberShipStoreCache;

      public JobStoreMemberListener(NamedCache memberShipStoreCache) {
         this.memberShipStoreCache = memberShipStoreCache;
      }

      @Override
      public void memberJoined(MemberEvent event) {
         // DO NOTHING AS OF NOW
      }

      @Override
      public void memberLeaving(MemberEvent event) {
         // DO NOTHING AS OF NOW

      }

      @Override
      public void memberLeft(MemberEvent event) {
         if (event.isLocal())
            return;

         TaskProcessorKey taskProcessorKey = ClusterInfoUtil.generateTaskProcessorKey(event.getMember());
         this.memberShipStoreCache.invoke(taskProcessorKey, new ConditionalRemove(PresentFilter.INSTANCE));
         this.memberShipStoreCache.invoke(SchedulerContantsEnum.PROCESSING_MEMEBER_SET.getConstantValue(), new AddRemoveMemberProcessor(AddRemoveMemberProcessor.DELETE_PROCESSING_MEMBER,
               taskProcessorKey));
      }
   }
}