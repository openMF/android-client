/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api.model;


import com.google.gson.Gson;


public class CollectionSheetPayload extends Payload
{
    public String actualDisbursementDate;
    public int[] bulkDisbursementTransactions;
    public BulkRepaymentTransactions[] bulkRepaymentTransactions;
    public String[] clientsAttendance;
     @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

}
