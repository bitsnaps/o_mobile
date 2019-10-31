package com.odoo.base.addons.abirex.util;

import com.odoo.core.utils.ODateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {


    private static SimpleDateFormat ymdFormart = new  SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dbFormart = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parseToYYDDMM(String date) throws ParseException {
        return ymdFormart.parse(date);
    }

    public static Date parseToYYDDMMHHMMSS(String date) throws ParseException {
        return dbFormart.parse(date);
    }

    public static String formatToLongDate(Date date) throws ParseException {
        return dbFormart.format(date);
    }

    public static Date parseFromDB(String date) throws ParseException {
        return dbFormart.parse(date);
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

    public static String parseToDB(Date date) throws ParseException {
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
}
