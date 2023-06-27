package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import com.mifos.api.model.BulkRepaymentTransactions

/**
 * Created by aksh on 20/6/18.
 */
interface OnRetrieveSheetItemData {
    fun onShowSheetMandatoryItem(transaction: BulkRepaymentTransactions, position: Int)
    fun onSaveAdditionalItem(transaction: BulkRepaymentTransactions, position: Int)
}