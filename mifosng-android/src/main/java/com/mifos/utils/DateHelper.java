/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ishankhanna on 30/05/14.
 * <p>This is a helper class that will be used to convert List<Interger> Type Dates
 * from MifosX into Simple Strings or Date Formats</p>
 */
public class DateHelper {


    public static final String DATE_FORMAT_VALUE = "dd MMM yyyy";
    public static final String DATE_TIME_FORMAT_VALUE = "dd MMMM yyyy HH:mm";
    public static final String TIME_FORMAT_VALUE = "HH:mm a";

    /**
     * @return current date formatted as day - month - year where month is a number from 1 to 12
     * (ex: 13 - 4 - 2014)
     */
    public static String getCurrentDateAsString() {

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + " - " + (month + 1) + " - " + year;

        return date;
    }

    /**
     * @return current date as [year-month-day] where month is a number from 1 to 12 (ex: [2014,
     * 4, 14])
     */
    public static List<Integer> getCurrentDateAsListOfIntegers() {

        List<Integer> date = new ArrayList<Integer>();
        Calendar calendar = Calendar.getInstance();
        date.add(calendar.get(Calendar.YEAR));
        date.add(calendar.get(Calendar.MONTH) + 1);
        date.add(calendar.get(Calendar.DAY_OF_MONTH));

        return date;
    }

    /**
     * @param date formatted as day-month-year where month is an integer from 1 to 12
     * @return replaces month with a string like Jan or Feb..etc
     */
    public static String getDateAsStringUsedForCollectionSheetPayload(String date) {
        StringBuilder builder = new StringBuilder();
        if (date != null) {
            String[] splittedDate = date.split("-");
            int month = Integer.parseInt(splittedDate[1]);
            builder.append(splittedDate[0]);
            builder.append('-');
            builder.append(getMonthName(month));
            builder.append('-');
            builder.append(splittedDate[2]);
        }
        return builder.toString();
        //Return as dd-mmm-yyyy

    }

    /**
     * @param date formatted as day-month-year where month is an integer from 1 to 12 (ex:
     *             14-4-2016)
     * @return replaces month with a string like Jan or Feb...etc (ex: 14-Apr-2016)
     */
    public static String getDateAsStringUsedForDateofBirth(String date) {
        final StringBuilder builder = new StringBuilder();
        if (date != null) {
            String[] splittedDate = date.split("-");
            int month = Integer.parseInt(splittedDate[1]);
            builder.append(splittedDate[0]);
            builder.append('-');
            builder.append(getMonthName(month));
            builder.append('-');
            builder.append(splittedDate[2]);
        }
        return builder.toString();
        //Return as dd-mmm-yyyy

    }

    /**
     * @return current date formatted as day month year where month is from 1 to 12 (ex 14 4 2016)
     */
    public static String getCurrentDateAsDateFormat() {

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + " " + (month + 1) + " " + year;


        return date;

    }

    /**
     * @return current date formatted as dd MMMM yyyy (ex: 14 April 2016)
     */
    public static String getCurrentDateAsNewDateFormat() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + " - " + (month + 1) + " - " + year;


        date = simpleDateFormat.format(calendar.getTime());

        return date;

    }

    /**
     * This method returns the String of date and time. Just need to pass the format in which you
     * want. Example Pass the format "dd MMMM yyyy HH:mm" and you will get the date and time in
     * this format "24 January 2017 18:32".
     *
     * @param format Format of Date and Time
     * @return String of Date and Time
     */
    public static String getCurrentDateTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return dateFormat.format(new Date());
    }


    /**
     * the result string uses the list given in a reverse order ([x, y, z] results in "z y x")
     *
     * @param integersOfDate [year-month-day] (ex [2016, 4, 14])
     * @return date in the format day month year (ex 14 Apr 2016)
     */
    public static String getDateAsString(List<Integer> integersOfDate) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(integersOfDate.get(2))
                .append(' ')
                .append(getMonthName(integersOfDate.get(1)))
                .append(' ')
                .append(integersOfDate.get(0));

        return stringBuilder.toString();

    }

    /**
     * @param date1 a list of 3 numbers [year, month, day] (Ex [2016, 4, 14])
     * @param date2 a list of 3 numbers [year, month, day] (Ex [2016, 3, 21])
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
        if (date1.get(0).equals(date2.get(0))) {

            //now that years are equal lets compare months

            if (date1.get(1).equals(date2.get(1))) {

                //now that months are also equal lets compare days

                if (date1.get(2).equals(date2.get(2))) {
                    return 0;
                } else if (date1.get(2) > date2.get(2)) {
                    return 1;
                } else {
                    return -1;
                }

            } else if (date1.get(1) > date2.get(1)) {
                return 1;
            } else {
                return -1;
            }

        } else if (date1.get(0) > date2.get(0)) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * @param month an integer from 1 to 12
     * @return string representation of the month like Jan or Feb..etc
     */
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


    /**
     * ex: date = 11,4,2016 separator = ,  result = [11, 4, 2016]
     *
     * @param date      string with tokken seperated by a seperator
     * @param separator the strings that separates the tokkens to be parsed
     */
    public static List<Integer> getDateList(String date, String separator) {
        String[] splittedDate = date.split(separator);
        List<Integer> dateList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dateList.add(Integer.parseInt(splittedDate[i]));
        }

        return dateList;
    }

    /**
     * Method to convert a given date in dd MMM YYYY format to [dd, mm, yyyy] format.
     *
     * @param date Date String. e.g. "20 Aug 2017"
     * @return List of Integers. e.g. [20, 08, 2017]
     */
    public static List<Integer> convertDateAsListOfInteger(String date) {
        String[] splitDate = date.split(" ");
        List<Integer> dateList = new ArrayList<>();
        dateList.add(Integer.parseInt(splitDate[0]));
        dateList.add(getMonthNumberFromName(splitDate[1]));
        dateList.add(Integer.parseInt(splitDate[2]));

        return dateList;
    }

    /**
     * Method to convert a date in dd MMM YYYY format to [yyyy, mm, dd] format.
     *
     * @param date Date String; e.g. "20 Aug 2017"
     * @return List of Integers in reverse order; e.g. [2017, 08, 20]
     */
    public static List<Integer> convertDateAsReverseInteger(String date) {
        String[] splitDate = date.split(" ");
        List<Integer> dateList = new ArrayList<>();
        dateList.add(Integer.parseInt(splitDate[2]));
        dateList.add(getMonthNumberFromName(splitDate[1]));
        dateList.add(Integer.parseInt(splitDate[0]));

        return dateList;
    }

    public static int getMonthNumberFromName(String month) {
        switch (month) {
            case "Jan":
                return 1;

            case "Feb":
                return 2;

            case "Mar":
                return 3;

            case "Apr":
                return 4;

            case "May":
                return 5;

            case "Jun":
                return 6;

            case "Jul":
                return 7;

            case "Aug":
                return 8;

            case "Sep":
                return 9;

            case "Oct":
                return 10;

            case "Nov":
                return 11;

            case "Dec":
                return 12;

            default:
                return -1;
        }
    }
}
