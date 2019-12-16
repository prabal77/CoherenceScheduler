/**
 * 
 */
package org.prabal.scheduler.internals;

import java.io.IOException;

import com.oracle.coherence.common.identifiers.Identifier;
import com.oracle.coherence.common.identifiers.StringBasedIdentifier;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.UID;

/**
 * @author Prabal Nandi
 *
 */
public class TaskProcessorKey implements PortableObject, Comparable<TaskProcessorKey> {
   private Identifier taskProcessorIdentifier;
   private int memberId;
   private UID uniqueId;
   private boolean nextAvailableProcessor = false;

   public TaskProcessorKey() {
      super();
   }

   public TaskProcessorKey(Identifier taskProcessorIdentifier, int memberId, UID uniqueId) {
      super();
      this.taskProcessorIdentifier = taskProcessorIdentifier;
      this.memberId = memberId;
      this.uniqueId = uniqueId;
   }

   public Identifier getTaskProcessorIdentifier() {
      return taskProcessorIdentifier;
   }

   public int getMemberId() {
      return memberId;
   }

   public UID getUniqueId() {
      return uniqueId;
   }

   public boolean isNextAvailableProcessor() {
      return nextAvailableProcessor;
   }

   public void setNextAvailableProcessor(boolean nextAvailableProcessor) {
      this.nextAvailableProcessor = nextAvailableProcessor;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + memberId;
      result = prime * result + ((taskProcessorIdentifier == null) ? 0 : taskProcessorIdentifier.hashCode());
      result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());

      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      TaskProcessorKey other = (TaskProcessorKey) obj;

      if (memberId != other.memberId) {
         return false;
      }

      if (taskProcessorIdentifier == null) {
         if (other.taskProcessorIdentifier != null) {
            return false;
         }
      }
      else if (!taskProcessorIdentifier.equals(other.taskProcessorIdentifier)) {
         return false;
      }

      if (uniqueId == null) {
         if (other.uniqueId != null) {
            return false;
         }
      }
      else if (!uniqueId.equals(other.uniqueId)) {
         return false;
      }

      return true;
   }

   @Override
   public int compareTo(TaskProcessorKey otherKey) {
      String thisStringIdentifier = ((StringBasedIdentifier) this.taskProcessorIdentifier).getString();
      String otherStringIdentifier = ((StringBasedIdentifier) otherKey.taskProcessorIdentifier).getString();

      int compair = thisStringIdentifier.compareTo(otherStringIdentifier);
      if (compair != 0)
         return compair;

      compair = this.uniqueId.compareTo(otherKey.getUniqueId());
      if (compair != 0)
         return compair;

      return (new Integer(this.memberId)).compareTo(new Integer(otherKey.getMemberId()));
   }

   public void readExternal(PofReader reader) throws IOException {
      this.taskProcessorIdentifier = (Identifier) reader.readObject(0);
      this.memberId = reader.readInt(1);
      this.uniqueId = (UID) reader.readObject(2);
      this.nextAvailableProcessor = reader.readBoolean(3);
   }

   /**
    * {@inheritDoc}
    */
   public void writeExternal(PofWriter writer) throws IOException {
      writer.writeObject(0, taskProcessorIdentifier);
      writer.writeInt(1, memberId);
      writer.writeObject(2, uniqueId);
      writer.writeBoolean(3, this.nextAvailableProcessor);
   }

   @Override
   public String toString() {
      return "TaskProcessorKey [taskProcessorIdentifier=" + taskProcessorIdentifier + ", memberId=" + memberId + ", uniqueId=" + uniqueId + ", nextAvailableProcessor=" + nextAvailableProcessor + "]";
   }

}
