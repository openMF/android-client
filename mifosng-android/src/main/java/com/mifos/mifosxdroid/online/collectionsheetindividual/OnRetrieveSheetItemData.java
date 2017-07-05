package com.mifos.mifosxdroid.online.collectionsheetindividual;

import com.mifos.api.model.BulkRepaymentTransactions;

/**
 * Created by Tarun on 18-07-2017.
 */

public interface OnRetrieveSheetItemData {

    void onShowSheetMandatoryItem(BulkRepaymentTransactions transaction, int position);

    void onSaveAdditionalItem(BulkRepaymentTransactions transaction, int position);
}
