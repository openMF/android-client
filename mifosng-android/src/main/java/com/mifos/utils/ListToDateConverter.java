package com.mifos.utils;

import java.util.List;

/**
 * Created by ishankhanna on 30/05/14.
 * <p>This is a helper class that will be used to convert List<Interger> Type Dates
 * from MifosX into Simple Strings or Date Formats</p>
 */
public class ListToDateConverter {



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
