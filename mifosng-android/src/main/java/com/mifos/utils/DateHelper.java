/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.mifos.mifosxdroid.OfflineCenterInputActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ishankhanna on 30/05/14.
 * <p>This is a helper class that will be used to convert List<Interger> Type Dates
 * from MifosX into Simple Strings or Date Formats</p>
 */
public class DateHelper {


    public static final String DATE_FORMAT_VALUE = "dd MMM yyyy";

    public static String getCurrentDateAsString(){

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + " - " + (month + 1) + " - " + year;

        return date;
    }

    public static List<Integer> getCurrentDateAsListOfIntegers() {

        List<Integer> date = new ArrayList<Integer>();
        Calendar calendar = Calendar.getInstance();
        date.add(calendar.get(Calendar.YEAR));
        date.add(calendar.get(Calendar.MONTH)+1);
        date.add(calendar.get(Calendar.DAY_OF_MONTH));

        return date;
    }

    public static String getDateAsStringUsedForCollectionSheetPayload(String date) {
        final StringBuilder builder = new StringBuilder();
        if (date != null) {
            String[] splittedDate = date.split("-");
            int month = Integer.parseInt(splittedDate[1]);
            builder.append(splittedDate[0]);
            builder.append("-");
            builder.append(getMonthName(month));
            builder.append("-");
            builder.append(splittedDate[2]);
        }
        return builder.toString();
        //Return as dd-mmm-yyyy

    }

    //Currently supports on "dd MM yyyy"
    public static String getCurrentDateAsDateFormat() {

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + " " + (month + 1) + " " + year;


        return date;

    }

    /**
     *
     * @param integersOfDate
     * @return date in format dd MMM yyyy
     */

    public static String getDateAsString(List<Integer> integersOfDate) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(integersOfDate.get(2))
                .append(" ")
                .append(getMonthName(integersOfDate.get(1)))
                .append(" ")
                .append(integersOfDate.get(0));

        return stringBuilder.toString();

    }



    /**
     *
     * @return zero if both date1 and date2 are equal, positive int if date1 > date2
     * and negative int if date1 < date2
     */

    public static int dateComparator(List<Integer> date1, List<Integer> date2) {

        /*
         *  Each List contains 3 items
         *  index 0 = Year
         *  index 1 = Month
         *  index 2 = Day
         *
         *  Format is YYYY - MM - DD
        */

        //comparing years
        if(date1.get(0)== date2.get(0)) {

            //now that years are equal lets compare months

            if(date1.get(1) == date2.get(1)) {

                //now that months are also equal lets compare days

                if(date1.get(2) == date2.get(2)) {
                    return 0;
                } else if(date1.get(2) > date2.get(2)) {
                    return 1;
                } else {
                    return -1;
                }

            } else if(date1.get(1) > date2.get(1)) {
                return 1;
            } else {
                return -1;
            }

        }else if(date1.get(0) > date2.get(0)) {
            return 1;
        } else {
            return -1;
        }
    }

    public static String getMonthName(int month) {
        String monthName = "";
        switch (month) {
            case 1:
                monthName = "Jan";
                break;
            case 2:
                monthName = "Feb";
                break;
            case 3:
                monthName = "Mar";
                break;
            case 4:
                monthName = "Apr";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "Jun";
                break;
            case 7:
                monthName = "Jul";
                break;
            case 8:
                monthName = "Aug";
                break;
            case 9:
                monthName = "Sep";
                break;
            case 10:
                monthName = "Oct";
                break;
            case 11:
                monthName = "Nov";
                break;
            case 12:
                monthName = "Dec";
                break;
        }
        return monthName;
    }

    public static String getPayloadDate() {
        SharedPreferences preferences = Constants.applicationContext.getSharedPreferences(OfflineCenterInputActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
        String date = preferences.getString(OfflineCenterInputActivity.TRANSACTION_DATE_KEY, null);
        final StringBuilder builder = new StringBuilder();
        if (date != null) {
            String[] splittedDate = date.split("-");
            int month = Integer.parseInt(splittedDate[1]);
            builder.append(splittedDate[0]);
            builder.append(" ");
            builder.append(DateHelper.getMonthName(month));
            builder.append(" ");
            builder.append(splittedDate[2]);
        }
        return builder.toString();
    }
}
