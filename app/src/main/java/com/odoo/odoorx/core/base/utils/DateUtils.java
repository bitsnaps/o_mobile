package com.odoo.odoorx.core.base.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {


    private static final String TAG = "core.utils.DateUtils";
    private static SimpleDateFormat ymdFormart = new  SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dbFormart = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parseToYYDDMM(String date) throws ParseException {
        return ymdFormart.parse(date);
    }

    public static Date parseToYYDDMMHHMMSS(String date) throws ParseException {
        return dbFormart.parse(date);
    }

    public static String formatToYYDDMMHHMMSS(Date date) {
        return dbFormart.format(date);
    }

    public static String formatToLongDate(Date date) throws ParseException {
        return dbFormart.format(date);
    }

    public static Date parseFromDB(String date){
        if(date.equals("false")) return null;
        try {
            return dbFormart.parse(date);
        }catch (ParseException pe){
            try{
                return parseToYYDDMM(date);
            }catch (ParseException ex){
                Log.e(TAG, "Unable to parse date format from DB value (" + date + ")");
                return null;
            }
        }
    }

    public static Date now () {
        try {
            return DateUtils.parseToYYDDMMHHMMSS(ODateUtils.getDate());
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static int nowY (){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now());
        return calendar.get(Calendar.YEAR);
    }

    public static int nowM (){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now());
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int nowD (){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now());
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String parseToDB(Date date) {
        return dbFormart.format(date);
    }

    public static Date beginningOfTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1900);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return  calendar.getTime();
    }

    public static Date beginningOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return  calendar.getTime();
    }



    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getLowerBound(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return  format.format(calendar.getTime());
    }

    public static String getUpperBound(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return format.format(calendar.getTime());
    }

    public static String stringDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        String diffString;
        if(elapsedDays > 0){
            diffString =String.format(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        }else if (elapsedHours > 0){
            diffString =String.format(
                    "%d hours, %d minutes, %d seconds%n",
                    elapsedHours, elapsedMinutes, elapsedSeconds);
        }else if (elapsedMinutes > 0){
            diffString =String.format(
                    "%d minutes, %d seconds%n",
                     elapsedMinutes, elapsedSeconds);
        }else {
            diffString = String.format(
                    " %d seconds%n",
                    elapsedSeconds);
        }

        return diffString;
    }
}
