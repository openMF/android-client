package com.mifos.mifosxdroid.online.collectionsheetindividualdetails;

import com.mifos.api.model.BulkRepaymentTransactions;

/**
 * Created by aksh on 20/6/18.
 */

public interface OnRetrieveSheetItemData {
    void onShowSheetMandatoryItem(BulkRepaymentTransactions transaction, int position);

    void onSaveAdditionalItem(BulkRepaymentTransactions transaction, int position);
}
