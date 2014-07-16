package com.mifos.services.data;


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
