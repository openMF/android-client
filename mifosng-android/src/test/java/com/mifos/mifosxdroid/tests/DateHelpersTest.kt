/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.tests

import com.mifos.utils.DateHelper.getDateAsString
import com.mifos.utils.DateHelper.getDateAsStringUsedForCollectionSheetPayload
import com.mifos.utils.DateHelper.getMonthName
import org.junit.Assert
import org.junit.Test

/**
 * Created by ahmed on 4/14/2016.
 */
class DateHelpersTest {
    @Test
    fun testGetDateAsStringUsedForCollectionSheetPayload1() {
        val date = "11-1-2002"
        val expected = "11-Jan-2002"
        val result = getDateAsStringUsedForCollectionSheetPayload(date)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun testGetDateAsStringUsedForCollectionSheetPayload2() {
        val date = "20-2-2016"
        val expected = "20-Feb-2016"
        val result = getDateAsStringUsedForCollectionSheetPayload(date)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun testGetDateAsString1() {
        val l = listOf(2016, 4, 14)
        val expected = "14 Apr 2016"
        val result = getDateAsString(l)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun testGetDateAsString2() {
        val l = listOf(2012, 1, 10)
        val expected = "10 Jan 2012"
        val result = getDateAsString(l)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun testGetMonthName() {
        val month = 1
        val expected = "Jan"
        val result = getMonthName(month)
        Assert.assertEquals(expected, result)
    }
}