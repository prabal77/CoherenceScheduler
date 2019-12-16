package org.prabal.scheduler.core;

import java.util.Date;
import java.util.TimeZone;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public abstract class BaseCoherenceCalendar implements java.lang.Cloneable {

   @PortableProperty(0)
   private String description;
   @PortableProperty(1)
   private TimeZone timeZone;
   // <p>A optional base calendar.</p>
   @PortableProperty(2)
   private BaseCoherenceCalendar baseCalendar;

   public BaseCoherenceCalendar() {
   }

   public BaseCoherenceCalendar(BaseCoherenceCalendar baseCalendar) {
      setBaseCalendar(baseCalendar);
   }

   public BaseCoherenceCalendar(TimeZone timeZone) {
      setTimeZone(timeZone);
   }

   public BaseCoherenceCalendar(BaseCoherenceCalendar baseCalendar, TimeZone timeZone) {
      setBaseCalendar(baseCalendar);
      setTimeZone(timeZone);
   }

   public void setBaseCalendar(BaseCoherenceCalendar baseCalendar) {
      this.baseCalendar = baseCalendar;
   }

   public BaseCoherenceCalendar getBaseCalendar() {
      return this.baseCalendar;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public TimeZone getTimeZone() {
      return timeZone;
   }

   public void setTimeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
   }

   protected java.util.Calendar createJavaCalendar(long timeStamp) {
      java.util.Calendar calendar = createJavaCalendar();
      calendar.setTime(new Date(timeStamp));
      return calendar;
   }

   protected java.util.Calendar createJavaCalendar() {
      java.util.Calendar calendar = java.util.Calendar.getInstance();
      calendar.setTime(new Date());
      if (getTimeZone() != null) {
         calendar.setTimeZone(getTimeZone());
      }
      return calendar;
   }

   protected java.util.Calendar getStartOfDayJavaCalendar(long timeInMillis) {
      java.util.Calendar startOfDay = createJavaCalendar(timeInMillis);
      startOfDay.set(java.util.Calendar.HOUR_OF_DAY, 0);
      startOfDay.set(java.util.Calendar.MINUTE, 0);
      startOfDay.set(java.util.Calendar.SECOND, 0);
      startOfDay.set(java.util.Calendar.MILLISECOND, 0);
      return startOfDay;
   }

   protected java.util.Calendar getEndOfDayJavaCalendar(long timeInMillis) {
      java.util.Calendar endOfDay = createJavaCalendar(timeInMillis);
      endOfDay.set(java.util.Calendar.HOUR_OF_DAY, 23);
      endOfDay.set(java.util.Calendar.MINUTE, 59);
      endOfDay.set(java.util.Calendar.SECOND, 59);
      endOfDay.set(java.util.Calendar.MILLISECOND, 999);
      return endOfDay;
   }

   public abstract boolean isTimeIncluded(long timeStamp);

   public abstract long getNextIncludedTime(long timeStamp);

   @Override
   public Object clone() {
      try {
         BaseCoherenceCalendar clone = (BaseCoherenceCalendar) super.clone();
         if (getBaseCalendar() != null) {
            clone.baseCalendar = (BaseCoherenceCalendar) getBaseCalendar().clone();
         }
         if (getTimeZone() != null)
            clone.timeZone = (TimeZone) getTimeZone().clone();
         return clone;
      }
      catch (CloneNotSupportedException ex) {
         throw new IncompatibleClassChangeError("Not Cloneable.");
      }
   }
}
