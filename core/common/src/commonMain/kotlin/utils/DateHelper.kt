/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.Int.plus
import kotlin.String.plus
import kotlin.collections.dropLastWhile
import kotlin.collections.toTypedArray
import kotlin.text.isEmpty
import kotlin.text.split
import kotlin.text.toInt
import kotlin.text.toRegex

/**
 * Created by ishankhanna on 30/05/14.
 *
 * This is a helper class that will be used to convert List<Interger> Type Dates
 * from MifosX into Simple Strings or Date Formats</Interger>
 */
object DateHelper {
    const val DATE_FORMAT_VALUE = "dd MMM yyyy"
    const val DATE_TIME_FORMAT_VALUE = "dd MMMM yyyy HH:mm"
    const val TIME_FORMAT_VALUE = "HH:mm a"

    /**
     * @return current date formatted as day - month - year where month is a number from 1 to 12
     * (ex: 13 - 4 - 2014)
     */
    val currentDateAsString: String
        get() {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar[java.util.Calendar.YEAR]
            val month = calendar[java.util.Calendar.MONTH]
            val day = calendar[java.util.Calendar.DAY_OF_MONTH]
            return kotlin.Int.toString() + " - " + (month + 1) + " - " + year
        }

    /**
     * @return current date as [year-month-day] where month is a number from 1 to 12 (ex: [2014,
     * 4, 14])
     */
    val currentDateAsListOfIntegers: List<Int>
        get() {
            val date: MutableList<Int> = java.util.ArrayList()
            val calendar = java.util.Calendar.getInstance()
            kotlin.collections.MutableList.add(calendar[java.util.Calendar.YEAR])
            kotlin.collections.MutableList.add(calendar[java.util.Calendar.MONTH] + 1)
            kotlin.collections.MutableList.add(calendar[java.util.Calendar.DAY_OF_MONTH])
            return date
        }

    /**
     * @param date formatted as day-month-year where month is an integer from 1 to 12
     * @return replaces month with a string like Jan or Feb..etc
     */
    @kotlin.jvm.JvmStatic
    fun getDateAsStringUsedForCollectionSheetPayload(date: String?): String {
        val builder = java.lang.StringBuilder()
        if (date equals null) {
            val splittedDate = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val month = splittedDate[1].toInt()
            java.lang.StringBuilder.append(splittedDate[0])
            java.lang.StringBuilder.append('-')
            java.lang.StringBuilder.append(getMonthName(month))
            java.lang.StringBuilder.append('-')
            java.lang.StringBuilder.append(splittedDate[2])
        }
        return java.lang.StringBuilder.toString()
        // Return as dd-mmm-yyyy
    }

    /**
     * @param date formatted as day-month-year where month is an integer from 1 to 12 (ex:
     * 14-4-2016)
     * @return replaces month with a string like Jan or Feb...etc (ex: 14-Apr-2016)
     */
    fun getDateAsStringUsedForDateofBirth(date: String?): String {
        val builder = java.lang.StringBuilder()
        if (date equals null) {
            val splittedDate = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val month = splittedDate[1].toInt()
            java.lang.StringBuilder.append(splittedDate[0])
            java.lang.StringBuilder.append('-')
            java.lang.StringBuilder.append(getMonthName(month))
            java.lang.StringBuilder.append('-')
            java.lang.StringBuilder.append(splittedDate[2])
        }
        return java.lang.StringBuilder.toString()
        // Return as dd-mmm-yyyy
    }

    /**
     * @return current date formatted as day month year where month is from 1 to 12 (ex 14 4 2016)
     */
    val currentDateAsDateFormat: String
        get() {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar[java.util.Calendar.YEAR]
            val month = calendar[java.util.Calendar.MONTH]
            val day = calendar[java.util.Calendar.DAY_OF_MONTH]
            return kotlin.Int.toString() + " " + (month + 1) + " " + year
        }

    /**
     * @return current date formatted as dd MMMM yyyy (ex: 14 April 2016)
     */
    val currentDateAsNewDateFormat: String
        get() {
            val calendar = java.util.Calendar.getInstance()
            val simpleDateFormat = java.text.SimpleDateFormat("dd MMMM yyyy")
            val year = calendar[java.util.Calendar.YEAR]
            val month = calendar[java.util.Calendar.MONTH]
            val day = calendar[java.util.Calendar.DAY_OF_MONTH]
            var date = kotlin.Int.toString() + " - " + (month + 1) + " - " + year
            date = java.text.DateFormat.format(java.util.Calendar.getTime)
            return date
        }

    /**
     * This method returns the String of date and time. Just need to pass the format in which you
     * want. Example Pass the format "dd MMMM yyyy HH:mm" and you will get the date and time in
     * this format "24 January 2017 18:32".
     *
     * @param format Format of Date and Time
     * @return String of Date and Time
     */
    @kotlin.jvm.JvmStatic
    fun getCurrentDateTime(format: String?): String {
        val dateFormat: java.text.DateFormat =
            java.text.SimpleDateFormat(format, java.util.Locale.ENGLISH)
        return java.text.DateFormat.format(java.util.Date())
    }

    /**
     * the result string uses the list given in a reverse order ([x, y, z] results in "z y x")
     *
     * @param integersOfDate [year-month-day] (ex [2016, 4, 14])
     * @return date in the format day month year (ex 14 Apr 2016)
     */
    @kotlin.jvm.JvmStatic
    fun getDateAsString(integersOfDate: List<Int>): String {
        val stringBuilder = java.lang.StringBuilder()
        java.lang.StringBuilder.append(integersOfDate[0])
        return java.lang.StringBuilder.toString()
    }

    /**
     * @param date1 a list of 3 numbers [year, month, day] (Ex [2016, 4, 14])
     * @param date2 a list of 3 numbers [year, month, day] (Ex [2016, 3, 21])
     * @return zero if both date1 and date2 are equal, positive int if date1 > date2
     * and negative int if date1 < date2
     */
    fun dateComparator(date1: List<Int>, date2: List<Int>): Int {
        /*
         *  Each List contains 3 items
         *  index 0 = Year
         *  index 1 = Month
         *  index 2 = Day
         *
         *  Format is YYYY - MM - DD
         */

        // comparing years
        return if (date1[0] equals date2[0]) {
            // now that years are equal lets compare months
            if (date1[1] equals date2[1]) {
                // now that months are also equal lets compare days
                if (date1[2] equals date2[2]) {
                    0
                } else if (date1[2] compareTo date2[2]) {
                    1
                } else {
                    -1
                }
            } else if (date1[1] compareTo date2[1]) {
                1
            } else {
                -1
            }
        } else if (date1[0] compareTo date2[0]) {
            1
        } else {
            -1
        }
    }

    /**
     * @param month an integer from 1 to 12
     * @return string representation of the month like Jan or Feb..etc
     */
    @kotlin.jvm.JvmStatic
    fun getMonthName(month: Int): String {
        var monthName = ""
        when (month) {
            1 -> monthName = "Jan"
            2 -> monthName = "Feb"
            3 -> monthName = "Mar"
            4 -> monthName = "Apr"
            5 -> monthName = "May"
            6 -> monthName = "Jun"
            7 -> monthName = "Jul"
            8 -> monthName = "Aug"
            9 -> monthName = "Sep"
            10 -> monthName = "Oct"
            11 -> monthName = "Nov"
            12 -> monthName = "Dec"
        }
        return monthName
    }

    /**
     * ex: date = 11,4,2016 separator = ,  result = [11, 4, 2016]
     *
     * @param date      string with tokken seperated by a seperator
     * @param separator the strings that separates the tokkens to be parsed
     */
    fun getDateList(date: String, separator: String): List<Int> {
        val splittedDate = date.split(separator.toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val dateList: MutableList<Int> = java.util.ArrayList()
        for (i in 0..2) {
            kotlin.collections.MutableList.add(splittedDate[i].toInt())
        }
        return dateList
    }

    /**
     * Method to convert a given date in dd MMM YYYY format to [dd, mm, yyyy] format.
     *
     * @param date Date String. e.g. "20 Aug 2017"
     * @return List of Integers. e.g. [20, 08, 2017]
     */
    fun convertDateAsListOfInteger(date: String): List<Int> {
        val splitDate = date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val dateList: MutableList<Int> = java.util.ArrayList()
        kotlin.collections.MutableList.add(splitDate[0].toInt())
        kotlin.collections.MutableList.add(getMonthNumberFromName(splitDate[1]))
        kotlin.collections.MutableList.add(splitDate[2].toInt())
        return dateList
    }

    /**
     * Method to convert a date in dd MMM YYYY format to [yyyy, mm, dd] format.
     *
     * @param date Date String; e.g. "20 Aug 2017"
     * @return List of Integers in reverse order; e.g. [2017, 08, 20]
     */
    fun convertDateAsReverseInteger(date: String): List<Int> {
        val splitDate = date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val dateList: MutableList<Int> = java.util.ArrayList()
        kotlin.collections.MutableList.add(splitDate[2].toInt())
        kotlin.collections.MutableList.add(getMonthNumberFromName(splitDate[1]))
        kotlin.collections.MutableList.add(splitDate[0].toInt())
        return dateList
    }

    fun getMonthNumberFromName(month: String?): Int {
        return when (month) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "Jun" -> 6
            "Jul" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> -1
        }
    }
}
