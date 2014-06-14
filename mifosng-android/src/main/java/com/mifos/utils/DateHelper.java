package com.mifos.utils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ishankhanna on 30/05/14.
 * <p>This is a helper class that will be used to convert List<Interger> Type Dates
 * from MifosX into Simple Strings or Date Formats</p>
 */
public class DateHelper {

    public static String getCurrentDateAsString(){

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + " - " + (month + 1) + " - " + year;

        return date;
    }

    public static String getDateAsString(List<Integer> integersOfDate) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(integersOfDate.get(0))
                .append("-")
                .append(integersOfDate.get(1))
                .append("-")
                .append(integersOfDate.get(2));

        return stringBuilder.toString();

    }


}
