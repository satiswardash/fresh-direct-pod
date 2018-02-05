package com.incture.cherrywork.freshdirect.Utils;

/**
 * Created by Arun on 27-09-2016.
 */
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {


    public static String getDateMonthFromDate(String inputDate) {
        if (inputDate == null || inputDate.length() < 24) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date = null;
        try {
            date = formatter1.parse(inputDate.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("dd MMM");
        String processedDate = formatter2.format(date);
        return processedDate;
    }



    public static String getMonth(String inputDate) {
        if (inputDate == null || inputDate.length() < 24) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date = null;
        try {
            date = formatter1.parse(inputDate.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("MMMM");
        String processedDate = formatter2.format(date);
        return processedDate;
    }


    public static String getMonthNumber(String inputDate){
        if (inputDate == null || inputDate.length() < 24) {
            if(inputDate == null || inputDate.length() < 10) {
                return "";
            }
            else{
                SimpleDateFormat formatter1, formatter2;
                formatter1 = new SimpleDateFormat("yyyy-MM-dd");

                Date date = null;
                try {
                    date = formatter1.parse(inputDate.substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formatter2 = new SimpleDateFormat("M");
                String processedDate = formatter2.format(date);
                return processedDate;
            }
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date = null;
        try {
            date = formatter1.parse(inputDate.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("M");
        String processedDate = formatter2.format(date);
        return processedDate;
    }

    public static String getYear(String inputDate) {
        if (inputDate == null || inputDate.length() < 24) {
            if(inputDate == null || inputDate.length() < 10) {
                return "";
            }
            else{
                SimpleDateFormat formatter1, formatter2;
                formatter1 = new SimpleDateFormat("yyyy-MM-dd");

                Date date = null;
                try {
                    date = formatter1.parse(inputDate.substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formatter2 = new SimpleDateFormat("yyyy");
                String processedDate = formatter2.format(date);
                return processedDate;
            }
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date = null;
        try {
            date = formatter1.parse(inputDate.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("yyyy");
        String processedDate = formatter2.format(date);
        return processedDate;
    }


    public static String getDate(String inputDate) {
        if (inputDate == null || inputDate.length() < 24) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date = null;
        try {
            date = formatter1.parse(inputDate.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("dd MMMM,yyyy");
        String processedDate = formatter2.format(date);
        return processedDate;
    }
    public static String getDay(String inputDate) {
        if (inputDate == null || inputDate.length() < 24) {
            if(inputDate == null || inputDate.length() < 10) {
                return "";
            }
            else{
                SimpleDateFormat formatter1, formatter2;
                formatter1 = new SimpleDateFormat("yyyy-MM-dd");

                Date date = null;
                try {
                    date = formatter1.parse(inputDate.substring(0, 10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formatter2 = new SimpleDateFormat("dd");
                String processedDate = formatter2.format(date);
                return processedDate;
            }
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date = null;
        try {
            date = formatter1.parse(inputDate.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("dd");
        String processedDate = formatter2.format(date);
        return processedDate;
    }

    public static String getWeekday(String inputDate) {
        if (inputDate == null || inputDate.length() < 24) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date = null;
        try {
            date = formatter1.parse(inputDate.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("EEE");
        String processedDate = formatter2.format(date);
        return processedDate;
    }


    public static String[] getPreviousWeekDates(String startDate,String endDate){
        if(startDate.isEmpty()||endDate.isEmpty())
            return null;

        SimpleDateFormat formatter1;
        formatter1 = new SimpleDateFormat("ddMMyyyy");
        formatter1.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        Date start = null;
        Date end = null;
        try {
            start = formatter1.parse(startDate);
            end= formatter1.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar startCal=Calendar.getInstance();
        Calendar endCal=Calendar.getInstance();
        startCal.setTime(start);
        endCal.setTime(end);
        startCal.add(Calendar.DATE, -7);
        endCal.add(Calendar.DATE, -7);

        String[] dates={formatter1.format(startCal.getTime()),formatter1.format(endCal.getTime())};
        return dates;
    }

    public static String getTimeString(Date fromdate,String time) {

        long then;
        then = fromdate.getTime();
        Date date = new Date(then);

        StringBuffer dateStr = new StringBuffer();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar now = Calendar.getInstance();

        int days = daysBetween(calendar.getTime(), now.getTime());
        int minutes = hoursBetween(calendar.getTime(), now.getTime());
        int hours = minutes / 60;
        if (days == 0) {

            int second = minuteBetween(calendar.getTime(), now.getTime());
            if (minutes > 60) {

                if (hours >= 1 && hours <= 24) {
                    if(hours==1) {
                        dateStr.append(hours).append(" hr ago");
                    }else{
                        dateStr.append(hours).append(" hrs ago");
                    }
                }

            } else {

                if (second <= 10) {
                    dateStr.append("Now");
                } else if (second > 10 && second <= 30) {
                    dateStr.append("few secs ago");
                } else if (second > 30 && second <= 60) {
                    dateStr.append(second).append(" secs ago");
                } else if (second >= 60 && minutes <= 60) {
                    if(minutes==1) {
                        dateStr.append(minutes).append(" min ago");
                    }else{
                        dateStr.append(minutes).append(" mins ago");
                    }
                }
            }
        } else

        if (hours > 24 && days <= 7) {
            if(days==1) {
                dateStr.append(days).append(" day ago");
            }else{
                dateStr.append(days).append(" days ago");
            }
        } else {
            dateStr.append(time);
        }

        return dateStr.toString();
    }



    public static int minuteBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
    }

    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
    }


    public static String getDateFromWeeks(String inputDate) {
        if (inputDate == null ||inputDate.isEmpty()) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("ddMMyyyy");
        formatter1.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        Date date = null;
        try {
            date = formatter1.parse(inputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("dd MMM");
        String processedDate = formatter2.format(date);
        return processedDate;
    }

    public static String getEachDay(String inputDate) {
        if (inputDate == null ||inputDate.isEmpty()) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("ddMMyyyy");
        formatter1.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        Date date = null;
        try {
            date = formatter1.parse(inputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("EEEE, dd MMM");
        String processedDate = formatter2.format(date);
        return processedDate;
    }

    //Get day of the week. eg: Monday, Tuesay

    public static String getDayOfWeek(String inputDate) {
        if (inputDate == null ||inputDate.isEmpty()) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("ddMMyyyy");
        formatter1.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        Date date = null;
        try {
            date = formatter1.parse(inputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);

        formatter2 = new SimpleDateFormat("EEEE");
        String processedDate = formatter2.format(date);
        return processedDate;
    }

    public static String getTime(String time){

        if (time == null ||time.isEmpty()) {
            return "";
        }

        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter1.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        Date date = null;
        try {
            date = formatter1.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("hh:mm a");
        String formattedTime = formatter2.format(date);
        return formattedTime;
    }

    public static String getTimeAMPM(String inputDate) {
        if (inputDate == null ||inputDate.isEmpty()) {
            return "";
        }
        SimpleDateFormat formatter1, formatter2;
        formatter1 = new SimpleDateFormat("hhmmss");


        Date date = null;
        try {
            date = formatter1.parse(inputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter2 = new SimpleDateFormat("hh:mm a");
        String processedDate = formatter2.format(date);
        return processedDate;
    }

    public static String changeCalendarDate(String inputDate){
        {
            if (inputDate == null || inputDate.length() < 24) {
                if(inputDate == null || inputDate.length() < 10) {
                    return "";
                }
                else{
                    SimpleDateFormat formatter1, formatter2;
                    formatter1 = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = null;
                    try {
                        date = formatter1.parse(inputDate.substring(0, 10));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String processedDate = formatter2.format(date);
                    return processedDate;
                }
            }
            SimpleDateFormat formatter1;
            SimpleDateFormat formatter2;
            //	formatter1 = ISODateTimeFormat.dateTimeNoMillis();

            formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

            Date date = null;
            try {
                date = formatter1.parse(inputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String processedDate = formatter2.format(date);
            return processedDate;
        }
    }
    public  static String getCurrentTime(){
        TimeZone tz = TimeZone.getTimeZone("Etc/UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return  nowAsISO;
    }
}
