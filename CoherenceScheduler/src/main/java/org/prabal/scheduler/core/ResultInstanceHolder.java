package org.prabal.scheduler.core;

import java.io.IOException;
import java.sql.Date;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

/**
 * @author Prabal Nandi
 *
 */
public class ResultInstanceHolder<T> implements Comparable<ResultInstanceHolder<T>>, PortableObject {
   public transient static final String SUCCESS = "SUCCESS";
   public transient static final String FAILED = "FAILED";

   private String resultStatus;
   private T resultObject = null;
   private long executionTimeLong;
   private transient TriggerKey triggerKey = null;

   private transient static final int STATUS = 0;
   private transient static final int RESULT = 1;
   private transient static final int EXECUTION_TIME = 2;

   public ResultInstanceHolder() {
      super();
   }

   public ResultInstanceHolder(String status, T resultObject, long executionTimeLong, TriggerKey triggerKey) {
      this.resultStatus = status;
      this.resultObject = resultObject;
      this.executionTimeLong = executionTimeLong;
      this.triggerKey = triggerKey;
   }

   public T getResult() {
      return this.resultObject;
   }

   public String getResultStatus() {
      return this.resultStatus;
   }

   public Date getExecutionTimeDate() {
      return new Date(this.executionTimeLong);
   }

   public long getExecutionTimeLong() {
      return this.executionTimeLong;
   }

   public TriggerKey getTriggerKey() {
      return this.triggerKey;
   }

   @Override
   public void readExternal(PofReader reader) throws IOException {
      this.resultStatus = reader.readString(STATUS);
      this.resultObject = (T) reader.readObject(RESULT);
      this.executionTimeLong = reader.readLong(EXECUTION_TIME);
   }

   @Override
   public void writeExternal(PofWriter writer) throws IOException {
      writer.writeString(STATUS, this.resultStatus);
      writer.writeObject(RESULT, resultObject);
      writer.writeLong(EXECUTION_TIME, this.executionTimeLong);
   }

   @Override
   public int compareTo(ResultInstanceHolder o) {
      return this.getExecutionTimeDate().compareTo(o.getExecutionTimeDate());
   }

   @Override
   public String toString() {
      return "ResultInstanceHolder [resultStatus=" + resultStatus + ", resultObject=" + resultObject + ", executionTimeLong=" + executionTimeLong + "]";
   }

}