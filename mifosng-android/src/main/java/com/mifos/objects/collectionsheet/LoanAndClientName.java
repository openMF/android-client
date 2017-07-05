package com.mifos.objects.collectionsheet;

/**
 * Created by Tarun on 17-07-2017.
 */

public class LoanAndClientName {

    private LoanCollectionSheet loan;

    private String clientName;

    public LoanAndClientName(LoanCollectionSheet loan, String clientName) {
        this.loan = loan;
        this.clientName = clientName;
    }

    public LoanCollectionSheet getLoan() {
        return loan;
    }

    public String getClientName() {
        return clientName;
    }
}
