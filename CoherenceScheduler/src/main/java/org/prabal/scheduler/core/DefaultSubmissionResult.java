package org.prabal.scheduler.core;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

/**
 * @author Prabal Nandi
 *
 */
public class DefaultSubmissionResult<T> implements SubmissionResult<T> {

   private LinkedList<ResultInstanceHolder<T>> submissionResultList;
   private AtomicBoolean changeFlag = new AtomicBoolean(false);

   private static final int RESULT_LIST = 0;
   private static final int CHANGE_FLAG = 1;

   public DefaultSubmissionResult() {
      this.submissionResultList = new LinkedList<ResultInstanceHolder<T>>();
   }

   public List<ResultInstanceHolder<T>> getCompleteList() {
      Collections.sort(submissionResultList);
      return this.submissionResultList;
   }

   public ResultInstanceHolder<T> getLatestResult() {
      Collections.sort(submissionResultList);
      return this.submissionResultList.peekLast();
   }

   public ResultInstanceHolder<T> getOldestResult() {
      Collections.sort(submissionResultList);
      return this.submissionResultList.peekFirst();
   }

   public List<ResultInstanceHolder<T>> getResultBetween(Date lowLimit, Date highLimit) {
      List<ResultInstanceHolder<T>> resultList = new LinkedList<ResultInstanceHolder<T>>();

      lowLimit = (lowLimit == null) ? MIN_TIME : lowLimit;
      highLimit = (highLimit == null) ? MAX_TIME : highLimit;

      if (lowLimit.after(highLimit))
         return null;

      Collections.sort(submissionResultList);
      Iterator<ResultInstanceHolder<T>> iterator = this.submissionResultList.iterator();
      for (; iterator.hasNext();) {
         ResultInstanceHolder<T> cursor = iterator.next();
         if (cursor.getExecutionTimeDate().equals(lowLimit) || (cursor.getExecutionTimeDate().after(lowLimit) && cursor.getExecutionTimeDate().before(highLimit)))
            resultList.add(cursor);
      }
      return resultList;
   }

   public List<ResultInstanceHolder<T>> getResultToDate(Date highLimit) {
      return getResultBetween(null, highLimit);
   }

   public List<ResultInstanceHolder<T>> getResultFromDate(Date lowLimit) {
      return getResultBetween(lowLimit, null);
   }

   public void addResultInstance(ResultInstanceHolder<T> resultInstanceHolder) {
      if (this.submissionResultList != null) {
         this.submissionResultList.addLast(resultInstanceHolder);
         setChanged();
      }
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
   public List<ResultInstanceHolder<T>> drainAllResult() {
      LinkedList<ResultInstanceHolder<T>> submissionResultListCopy = new LinkedList<ResultInstanceHolder<T>>();
      submissionResultListCopy.addAll(this.submissionResultList);
      this.submissionResultList = new LinkedList<ResultInstanceHolder<T>>();
      setChanged();
      return submissionResultListCopy;
   }

   @Override
   public void readExternal(PofReader reader) throws IOException {
      reader.readCollection(RESULT_LIST, this.submissionResultList);
      this.changeFlag.set(reader.readBoolean(CHANGE_FLAG));
   }

   @Override
   public void writeExternal(PofWriter writer) throws IOException {
      writer.writeCollection(RESULT_LIST, this.submissionResultList);
      writer.writeBoolean(CHANGE_FLAG, this.changeFlag.get());
   }

}