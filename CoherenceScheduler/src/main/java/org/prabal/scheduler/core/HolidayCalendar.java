/**
 * 
 */
package org.prabal.scheduler.core;

import java.util.Collections;
import java.util.Date;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.prabal.scheduler.pof.HolidaySetCodec;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Prabal Nandi
 *
 */
@Portable
public class HolidayCalendar extends BaseCoherenceCalendar{
   
   // A sorted set to store the holidays
   @PortableProperty(value=10,codec=HolidaySetCodec.class)
   private TreeSet<Date> dates = new TreeSet<Date>();

   public HolidayCalendar() {
   }

   public HolidayCalendar(BaseCoherenceCalendar baseCalendar) {
       super(baseCalendar);
   }

   public HolidayCalendar(TimeZone timeZone) {
       super(timeZone);
   }

   public HolidayCalendar(BaseCoherenceCalendar baseCalendar, TimeZone timeZone) {
       super(baseCalendar, timeZone);
   }

   @Override
   public Object clone() {
       HolidayCalendar clone = (HolidayCalendar) super.clone();
       clone.dates = new TreeSet<Date>(dates);
       return clone;
   }
   
   @Override
   public boolean isTimeIncluded(long timeStamp) {
       Date lookFor = getStartOfDayJavaCalendar(timeStamp).getTime();
       return !(dates.contains(lookFor));
   }

   @Override
   public long getNextIncludedTime(long timeStamp) {

       // Get timestamp for 00:00:00
       java.util.Calendar day = getStartOfDayJavaCalendar(timeStamp);
       while (isTimeIncluded(day.getTime().getTime()) == false) {
           day.add(java.util.Calendar.DATE, 1);
       }
       return day.getTime().getTime();
   }

   public void addExcludedDate(Date excludedDate) {
       Date date = getStartOfDayJavaCalendar(excludedDate.getTime()).getTime();
       this.dates.add(date);
   }

   public void removeExcludedDate(Date dateToRemove) {
       Date date = getStartOfDayJavaCalendar(dateToRemove.getTime()).getTime();
       dates.remove(date);
   }

   public SortedSet<Date> getExcludedDates() {
       return Collections.unmodifiableSortedSet(dates);
   }

   @Override
   public String toString() {
      return "HolidayCalendar [dates=" + dates + "]";
   }
   
}
