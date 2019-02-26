package com.mifos.mifosxdroid.online.collectionsheetindividualdetails;

import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by aksh on 20/6/18.
 */

public interface IndividualCollectionSheetDetailsMvpView extends MvpView {
    void showSuccess();

    void showError(String error);
}
