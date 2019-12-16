package org.prabal.scheduler.core;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.tangosol.net.CacheFactory;

public class DateBuilder {

   public enum IntervalUnit {
      MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
   }

   public static final int SUNDAY = 1;

   public static final int MONDAY = 2;

   public static final int TUESDAY = 3;

   public static final int WEDNESDAY = 4;

   public static final int THURSDAY = 5;

   public static final int FRIDAY = 6;

   public static final int SATURDAY = 7;

   public static final int JANUARY = 1;

   public static final int FEBRUARY = 2;

   public static final int MARCH = 3;

   public static final int APRIL = 4;

   public static final int MAY = 5;

   public static final int JUNE = 6;

   public static final int JULY = 7;

   public static final int AUGUST = 8;

   public static final int SEPTEMBER = 9;

   public static final int OCTOBER = 10;

   public static final int NOVEMBER = 11;

   public static final int DECEMBER = 12;

   public static final long MILLISECONDS_IN_MINUTE = 60l * 1000l;

   public static final long MILLISECONDS_IN_HOUR = 60l * 60l * 1000l;

   public static final long SECONDS_IN_MOST_DAYS = 24l * 60l * 60L;

   public static final long MILLISECONDS_IN_DAY = SECONDS_IN_MOST_DAYS * 1000l;

   private int month;
   private int day;
   private int year;
   private int hour;
   private int minute;
   private int second;
   private TimeZone tz;
   private Locale lc;

   private DateBuilder() {
      Calendar cal = Calendar.getInstance();
      cal.setTime(getCurrentClusterTime());
      month = cal.get(Calendar.MONTH) + 1;
      day = cal.get(Calendar.DAY_OF_MONTH);
      year = cal.get(Calendar.YEAR);
      hour = cal.get(Calendar.HOUR_OF_DAY);
      minute = cal.get(Calendar.MINUTE);
      second = cal.get(Calendar.SECOND);
   }

   private DateBuilder(TimeZone tz) {
      Calendar cal = Calendar.getInstance(tz);
      cal.setTime(getCurrentClusterTime());
      this.tz = tz;
      month = cal.get(Calendar.MONTH) + 1;
      day = cal.get(Calendar.DAY_OF_MONTH);
      year = cal.get(Calendar.YEAR);
      hour = cal.get(Calendar.HOUR_OF_DAY);
      minute = cal.get(Calendar.MINUTE);
      second = cal.get(Calendar.SECOND);
   }

   private DateBuilder(Locale lc) {
      Calendar cal = Calendar.getInstance(lc);
      cal.setTime(getCurrentClusterTime());
      this.lc = lc;
      month = cal.get(Calendar.MONTH) + 1;
      day = cal.get(Calendar.DAY_OF_MONTH);
      year = cal.get(Calendar.YEAR);
      hour = cal.get(Calendar.HOUR_OF_DAY);
      minute = cal.get(Calendar.MINUTE);
      second = cal.get(Calendar.SECOND);
   }

   private DateBuilder(TimeZone tz, Locale lc) {
      Calendar cal = Calendar.getInstance(tz, lc);
      cal.setTime(getCurrentClusterTime());
      this.tz = tz;
      this.lc = lc;
      month = cal.get(Calendar.MONTH) + 1;
      day = cal.get(Calendar.DAY_OF_MONTH);
      year = cal.get(Calendar.YEAR);
      hour = cal.get(Calendar.HOUR_OF_DAY);
      minute = cal.get(Calendar.MINUTE);
      second = cal.get(Calendar.SECOND);
   }

   public static DateBuilder newDate() {
      return new DateBuilder();
   }

   public static DateBuilder newDateInTimezone(TimeZone tz) {
      return new DateBuilder(tz);
   }

   public static DateBuilder newDateInLocale(Locale lc) {
      return new DateBuilder(lc);
   }

   public static DateBuilder newDateInTimeZoneAndLocale(TimeZone tz, Locale lc) {
      return new DateBuilder(tz, lc);
   }

   public Date build() {
      Calendar cal;

      if (tz != null && lc != null)
         cal = Calendar.getInstance(tz, lc);
      else if (tz != null)
         cal = Calendar.getInstance(tz);
      else if (lc != null)
         cal = Calendar.getInstance(lc);
      else
         cal = Calendar.getInstance();

      cal.set(Calendar.YEAR, year);
      cal.set(Calendar.MONTH, month - 1);
      cal.set(Calendar.DAY_OF_MONTH, day);
      cal.set(Calendar.HOUR_OF_DAY, hour);
      cal.set(Calendar.MINUTE, minute);
      cal.set(Calendar.SECOND, second);
      cal.set(Calendar.MILLISECOND, 0);

      return cal.getTime();
   }

   /**
    * Set the hour (0-23) for the Date that will be built by this builder.
    */
   public DateBuilder atHourOfDay(int atHour) {
      validateHour(atHour);

      this.hour = atHour;
      return this;
   }

   /**
    * Set the minute (0-59) for the Date that will be built by this builder.
    */
   public DateBuilder atMinute(int atMinute) {
      validateMinute(atMinute);

      this.minute = atMinute;
      return this;
   }

   /**
    * Set the second (0-59) for the Date that will be built by this builder, and truncate the
    * milliseconds to 000.
    */
   public DateBuilder atSecond(int atSecond) {
      validateSecond(atSecond);

      this.second = atSecond;
      return this;
   }

   public DateBuilder atHourMinuteAndSecond(int atHour, int atMinute, int atSecond) {
      validateHour(atHour);
      validateMinute(atMinute);
      validateSecond(atSecond);

      this.hour = atHour;
      this.second = atSecond;
      this.minute = atMinute;
      return this;
   }

   /**
    * Set the day of month (1-31) for the Date that will be built by this builder.
    */
   public DateBuilder onDay(int onDay) {
      validateDayOfMonth(onDay);

      this.day = onDay;
      return this;
   }

   /**
    * Set the month (1-12) for the Date that will be built by this builder.
    */
   public DateBuilder inMonth(int inMonth) {
      validateMonth(inMonth);

      this.month = inMonth;
      return this;
   }

   public DateBuilder inMonthOnDay(int inMonth, int onDay) {
      validateMonth(inMonth);
      validateDayOfMonth(onDay);

      this.month = inMonth;
      this.day = onDay;
      return this;
   }

   /**
    * Set the year for the Date that will be built by this builder.
    */
   public DateBuilder inYear(int inYear) {
      validateYear(inYear);

      this.year = inYear;
      return this;
   }

   /**
    * Set the TimeZone for the Date that will be built by this builder (if "null", system default
    * will be used)
    */
   public DateBuilder inTimeZone(TimeZone timezone) {
      this.tz = timezone;
      return this;
   }

   /**
    * Set the Locale for the Date that will be built by this builder (if "null", system default will
    * be used)
    */
   public DateBuilder inLocale(Locale locale) {
      this.lc = locale;
      return this;
   }

   public static Date futureDate(int interval, IntervalUnit unit) {

      Calendar c = Calendar.getInstance();
      c.setTime(getCurrentClusterTime());
      c.setLenient(true);

      c.add(translate(unit), interval);

      return c.getTime();
   }

   private static int translate(IntervalUnit unit) {
      switch (unit) {
         case DAY:
            return Calendar.DAY_OF_YEAR;
         case HOUR:
            return Calendar.HOUR_OF_DAY;
         case MINUTE:
            return Calendar.MINUTE;
         case MONTH:
            return Calendar.MONTH;
         case SECOND:
            return Calendar.SECOND;
         case MILLISECOND:
            return Calendar.MILLISECOND;
         case WEEK:
            return Calendar.WEEK_OF_YEAR;
         case YEAR:
            return Calendar.YEAR;
         default:
            throw new IllegalArgumentException("Unknown IntervalUnit");
      }
   }

   public static Date tomorrowAt(int hour, int minute, int second) {
      validateSecond(second);
      validateMinute(minute);
      validateHour(hour);

      Date date = getCurrentClusterTime();

      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.setLenient(true);

      // advance one day
      c.add(Calendar.DAY_OF_YEAR, 1);

      c.set(Calendar.HOUR_OF_DAY, hour);
      c.set(Calendar.MINUTE, minute);
      c.set(Calendar.SECOND, second);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date todayAt(int hour, int minute, int second) {
      return dateOf(hour, minute, second);
   }

   public static Date dateOf(int hour, int minute, int second) {
      validateSecond(second);
      validateMinute(minute);
      validateHour(hour);

      Date date = getCurrentClusterTime();

      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.setLenient(true);

      c.set(Calendar.HOUR_OF_DAY, hour);
      c.set(Calendar.MINUTE, minute);
      c.set(Calendar.SECOND, second);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date dateOf(int hour, int minute, int second, int dayOfMonth, int month) {
      validateSecond(second);
      validateMinute(minute);
      validateHour(hour);
      validateDayOfMonth(dayOfMonth);
      validateMonth(month);

      Date date = getCurrentClusterTime();

      Calendar c = Calendar.getInstance();
      c.setTime(date);

      c.set(Calendar.MONTH, month - 1);
      c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      c.set(Calendar.HOUR_OF_DAY, hour);
      c.set(Calendar.MINUTE, minute);
      c.set(Calendar.SECOND, second);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date dateOf(int hour, int minute, int second, int dayOfMonth, int month, int year) {
      validateSecond(second);
      validateMinute(minute);
      validateHour(hour);
      validateDayOfMonth(dayOfMonth);
      validateMonth(month);
      validateYear(year);

      Date date = getCurrentClusterTime();

      Calendar c = Calendar.getInstance();
      c.setTime(date);

      c.set(Calendar.YEAR, year);
      c.set(Calendar.MONTH, month - 1);
      c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      c.set(Calendar.HOUR_OF_DAY, hour);
      c.set(Calendar.MINUTE, minute);
      c.set(Calendar.SECOND, second);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date evenHourDateAfterNow() {
      return evenHourDate(null);
   }

   public static Date evenHourDate(Date date) {
      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.setLenient(true);

      c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date evenHourDateBefore(Date date) {
      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);

      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date evenMinuteDateAfterNow() {
      return evenMinuteDate(null);
   }

   public static Date evenMinuteDate(Date date) {
      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.setLenient(true);

      c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date evenMinuteDateBefore(Date date) {
      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);

      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date evenSecondDateAfterNow() {
      return evenSecondDate(null);
   }

   public static Date evenSecondDate(Date date) {
      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.setLenient(true);

      c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 1);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date evenSecondDateBefore(Date date) {
      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);

      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
   }

   public static Date nextGivenMinuteDate(Date date, int minuteBase) {
      if (minuteBase < 0 || minuteBase > 59) {
         throw new IllegalArgumentException("minuteBase must be >=0 and <= 59");
      }

      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.setLenient(true);

      if (minuteBase == 0) {
         c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
         c.set(Calendar.MINUTE, 0);
         c.set(Calendar.SECOND, 0);
         c.set(Calendar.MILLISECOND, 0);

         return c.getTime();
      }

      int minute = c.get(Calendar.MINUTE);

      int arItr = minute / minuteBase;

      int nextMinuteOccurance = minuteBase * (arItr + 1);

      if (nextMinuteOccurance < 60) {
         c.set(Calendar.MINUTE, nextMinuteOccurance);
         c.set(Calendar.SECOND, 0);
         c.set(Calendar.MILLISECOND, 0);

         return c.getTime();
      }
      else {
         c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
         c.set(Calendar.MINUTE, 0);
         c.set(Calendar.SECOND, 0);
         c.set(Calendar.MILLISECOND, 0);

         return c.getTime();
      }
   }

   public static Date nextGivenSecondDate(Date date, int secondBase) {
      if (secondBase < 0 || secondBase > 59) {
         throw new IllegalArgumentException("secondBase must be >=0 and <= 59");
      }

      if (date == null) {
         date = getCurrentClusterTime();
      }

      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.setLenient(true);

      if (secondBase == 0) {
         c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
         c.set(Calendar.SECOND, 0);
         c.set(Calendar.MILLISECOND, 0);

         return c.getTime();
      }

      int second = c.get(Calendar.SECOND);

      int arItr = second / secondBase;

      int nextSecondOccurance = secondBase * (arItr + 1);

      if (nextSecondOccurance < 60) {
         c.set(Calendar.SECOND, nextSecondOccurance);
         c.set(Calendar.MILLISECOND, 0);

         return c.getTime();
      }
      else {
         c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
         c.set(Calendar.SECOND, 0);
         c.set(Calendar.MILLISECOND, 0);

         return c.getTime();
      }
   }

   public static Date translateTime(Date date, TimeZone src, TimeZone dest) {

      Date newDate = getCurrentClusterTime();
      int offset = (dest.getOffset(date.getTime()) - src.getOffset(date.getTime()));
      newDate.setTime(date.getTime() - offset);
      return newDate;
   }

   public static void validateDayOfWeek(int dayOfWeek) {
      if (dayOfWeek < SUNDAY || dayOfWeek > SATURDAY) {
         throw new IllegalArgumentException("Invalid day of week.");
      }
   }

   public static void validateHour(int hour) {
      if (hour < 0 || hour > 23) {
         throw new IllegalArgumentException("Invalid hour (must be >= 0 and <= 23).");
      }
   }

   public static void validateMinute(int minute) {
      if (minute < 0 || minute > 59) {
         throw new IllegalArgumentException("Invalid minute (must be >= 0 and <= 59).");
      }
   }

   public static void validateSecond(int second) {
      if (second < 0 || second > 59) {
         throw new IllegalArgumentException("Invalid second (must be >= 0 and <= 59).");
      }
   }

   public static void validateDayOfMonth(int day) {
      if (day < 1 || day > 31) {
         throw new IllegalArgumentException("Invalid day of month.");
      }
   }

   public static void validateMonth(int month) {
      if (month < 1 || month > 12) {
         throw new IllegalArgumentException("Invalid month (must be >= 1 and <= 12.");
      }
   }

   private static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) + 100;

   public static void validateYear(int year) {
      if (year < 0 || year > MAX_YEAR) {
         throw new IllegalArgumentException("Invalid year (must be >= 0 and <= " + MAX_YEAR);
      }
   }

   public static Date getCurrentClusterTime() {
      CacheFactory.ensureCluster();
      long currentTimeinLong = CacheFactory.getCluster().getTimeMillis();
      Date currentDate = new Date(System.currentTimeMillis());
      if (currentTimeinLong != 0L)
         currentDate.setTime(currentTimeinLong);
      return currentDate;
   }

}
