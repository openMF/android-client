/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.content.Intent;

import com.mifos.utils.DateHelper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ahmed on 4/14/2016.
 */
public class DateHelpersTest {

    @Test
    public void testGetDateAsStringUsedForCollectionSheetPayload1() {
        String date = "11-1-2002";
        String expected = "11-Jan-2002";
        String result = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date);

        assertEquals(expected, result);
    }

    @Test
    public void testGetDateAsStringUsedForCollectionSheetPayload2() {
        String date = "20-2-2016";
        String expected = "20-Feb-2016";
        String result = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date);

        assertEquals(expected, result);
    }


    @Test
    public void testGetDateAsString1() {
        List<Integer> l = Arrays.asList(2016, 4, 14);
        String expected = "14 Apr 2016";
        String result = DateHelper.getDateAsString(l);

        assertEquals(expected, result);
    }

    @Test
    public void testGetDateAsString2() {
        List<Integer> l = Arrays.asList(2012, 1, 10);
        String expected = "10 Jan 2012";
        String result = DateHelper.getDateAsString(l);

        assertEquals(expected, result);
    }


    @Test
    public void testGetMonthName() {
        int month = 1;
        String expected = "Jan";
        String result = DateHelper.getMonthName(month);

        assertEquals(expected, result);
    }
}
