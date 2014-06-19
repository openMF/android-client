package com.mifos.utils;

import java.util.ArrayList;
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

    public static List<Integer> getCurrentDateAsListOfIntegers() {

        List<Integer> date = new ArrayList<Integer>();
        Calendar calendar = Calendar.getInstance();
        date.add(calendar.get(Calendar.YEAR));
        date.add(calendar.get(Calendar.MONTH)+1);
        date.add(calendar.get(Calendar.DAY_OF_MONTH));

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


}
