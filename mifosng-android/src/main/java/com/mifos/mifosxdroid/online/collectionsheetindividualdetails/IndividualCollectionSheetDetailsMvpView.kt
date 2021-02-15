package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by aksh on 20/6/18.
 */
interface IndividualCollectionSheetDetailsMvpView : MvpView {
    fun showSuccess()
    fun showError(error: String?)
}