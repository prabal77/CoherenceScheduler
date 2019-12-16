/**
 * 
 */
package org.prabal.scheduler.internals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

/**
 * @author Prabal Nandi
 *
 */
public class DefaultTaskProcessorInstance<V> implements TaskProcessorInstance {
   private TaskProcessorKey taskProcessorKey = null;
   private LinkedList<JobExecutionPayload> submissionList = new LinkedList<JobExecutionPayload>();
   private AtomicLong jobSubmitted = new AtomicLong(0);
   private AtomicBoolean changeFlag = new AtomicBoolean(false);
   private ReentrantLock lock = new ReentrantLock();

   private static final int TASKPROCESSOR_KEY = 0;
   private static final int SUBMISSION_LIST = 1;
   private static final int SUBMITTED_JOB_COUNT = 2;
   private static final int CHANGE_FLAG = 3;

   public DefaultTaskProcessorInstance() {
      super();
   }

   public DefaultTaskProcessorInstance(TaskProcessorKey taskProcessorKey) {
      super();
      this.taskProcessorKey = taskProcessorKey;
   }

   @Override
   public void readExternal(PofReader reader) throws IOException {
      this.taskProcessorKey = (TaskProcessorKey) reader.readObject(TASKPROCESSOR_KEY);
      reader.readCollection(SUBMISSION_LIST, this.submissionList);
      this.jobSubmitted = new AtomicLong(reader.readLong(SUBMITTED_JOB_COUNT));
      this.changeFlag = new AtomicBoolean(reader.readBoolean(CHANGE_FLAG));
   }

   @Override
   public void writeExternal(PofWriter writer) throws IOException {
      writer.writeObject(TASKPROCESSOR_KEY, this.taskProcessorKey);
      writer.writeCollection(SUBMISSION_LIST, this.submissionList, JobExecutionPayload.class);
      writer.writeLong(SUBMITTED_JOB_COUNT, this.jobSubmitted.get());
      writer.writeBoolean(CHANGE_FLAG, this.changeFlag.get());
   }

   @Override
   public void beforeChange() {
      this.changeFlag.set(false);
   }

   @Override
   public boolean changed() {
      return this.changeFlag.get();
   }

   public boolean setChanged() {
      return this.changeFlag.compareAndSet(false, true);
   }

   @Override
   public boolean enqueuePayload(JobExecutionPayload payload) {
      lock.lock();
      boolean enqueueStatus = false;
      try {
         enqueueStatus = this.submissionList.add(payload);
         this.jobSubmitted.incrementAndGet();
         setChanged();
      }
      finally {
         lock.unlock();
      }
      return enqueueStatus;
   }

   @Override
   public JobExecutionPayload fetchNextPayload() {
      JobExecutionPayload executionPayload = null;
      if (this.submissionList != null && this.submissionList.size() > 0) {
         executionPayload = this.submissionList.removeFirst();
         setChanged();
      }
      return executionPayload;
   }

   @Override
   public List drainAllPayLoad() {
      LinkedList<JobExecutionPayload> returnList = new LinkedList<JobExecutionPayload>();
      lock.lock();
      try {
         returnList.addAll(this.submissionList);
         this.submissionList.clear();
      }
      finally {
         lock.unlock();
      }
      return returnList;
   }

   @Override
   public void clearPendingListWithoutProcessing() {
      lock.lock();
      try {
         this.submissionList.clear();
      }
      finally {
         lock.unlock();
      }
   }

   @Override
   public TaskProcessorKey getTaskProcessorKey() {
      return this.taskProcessorKey;
   }

}
