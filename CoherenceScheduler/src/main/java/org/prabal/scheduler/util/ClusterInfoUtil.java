/**
 * 
 */
package org.prabal.scheduler.util;

import java.util.Date;
import java.util.UUID;

import org.prabal.scheduler.internals.TaskProcessorKey;

import com.oracle.coherence.common.identifiers.Identifier;
import com.oracle.coherence.common.identifiers.StringBasedIdentifier;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.DistributedCacheService;
import com.tangosol.net.Member;
import com.tangosol.net.NamedCache;
import com.tangosol.util.UID;

/**
 * @author Prabal Nandi
 *
 */
public class ClusterInfoUtil {

   public static Date getCurrentClusterTime() {
      CacheFactory.ensureCluster();
      long currentTimeinLong = CacheFactory.getCluster().getTimeMillis();
      Date currentDate = new Date(System.currentTimeMillis());
      if (currentTimeinLong != 0L)
         currentDate.setTime(currentTimeinLong);
      return currentDate;
   }

   public static String getLocalMemberIdString() {
      Member localMember = CacheFactory.getCluster().getLocalMember();
      StringBuilder memberInfoBuilder = new StringBuilder();
      memberInfoBuilder.append(localMember.getClusterName()).append("_");
      memberInfoBuilder.append(localMember.getSiteName()).append("_");
      memberInfoBuilder.append(localMember.getRackName()).append("_");
      memberInfoBuilder.append(localMember.getRoleName()).append("_");
      memberInfoBuilder.append(localMember.getMachineId()).append("_");
      memberInfoBuilder.append(localMember.getUid()).append("_");
      memberInfoBuilder.append(localMember.getId());
      return memberInfoBuilder.toString();
   }

   public static String getLocalMemberIdShort() {
      Member localMember = CacheFactory.getCluster().getLocalMember();
      StringBuilder memberInfoBuilder = new StringBuilder();
      memberInfoBuilder.append(localMember.getUid()).append("_").append(localMember.getId());
      return memberInfoBuilder.toString();
   }

   public static String getOtherMemberIdShort(Member member) {
      StringBuilder memberInfoBuilder = new StringBuilder();
      memberInfoBuilder.append(member.getUid()).append("_").append(member.getId());
      return memberInfoBuilder.toString();
   }

   public static TaskProcessorKey generateTaskProcessorKey(Member member) {
      Identifier identifier = StringBasedIdentifier.newInstance(member.getClusterName() + "_" + member.getMemberName() + "_" + member.getId());
      return new TaskProcessorKey(identifier, member.getId(), member.getUid());
   }

   public static Identifier generateTaskInstanceId(UID uid) {
      return StringBasedIdentifier.newInstance(uid.toString());
   }

   public static boolean isLocalStorageEnabledMember(NamedCache cache) {
      DistributedCacheService cacheService = (DistributedCacheService) cache.getCacheService();
      Member localMember = cacheService.getCluster().getLocalMember();
      return cacheService.getOwnershipEnabledMembers().contains(localMember);
   }

   public static Member getLocalMember() {
      return CacheFactory.getCluster().getLocalMember();
   }

}
