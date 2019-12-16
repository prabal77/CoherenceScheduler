
/**
 * 
 */
package org.prabal.scheduler.core;

import java.util.Date;
import java.util.List;

import com.oracle.coherence.common.util.ChangeIndication;
import com.tangosol.io.pof.PortableObject;

/**
 * @author Prabal Nandi
 *
 */
public interface SubmissionResult<T> extends PortableObject, ChangeIndication {

   final Date MAX_TIME = new Date(Long.MAX_VALUE);
   final Date MIN_TIME = new Date(0L);

   public List<ResultInstanceHolder<T>> getCompleteList();

   public ResultInstanceHolder<T> getLatestResult();

   public ResultInstanceHolder<T> getOldestResult();

   public List<ResultInstanceHolder<T>> getResultBetween(Date lowLimit, Date highLimit);

   public List<ResultInstanceHolder<T>> getResultToDate(Date highLimit);

   public List<ResultInstanceHolder<T>> getResultFromDate(Date lowLimit);

   public void addResultInstance(ResultInstanceHolder<T> resultInstanceHolder);

   public List<ResultInstanceHolder<T>> drainAllResult();
}