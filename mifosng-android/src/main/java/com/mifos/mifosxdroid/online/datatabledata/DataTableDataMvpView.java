package com.mifos.mifosxdroid.online.datatabledata;

import com.google.gson.JsonArray;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public interface DataTableDataMvpView extends MvpView {

    void showDataTableInfo(JsonArray jsonElements);

    void showDataTableDeletedSuccessfully();

    void showEmptyDataTable();

    void showFetchingError(int message);

    void showFetchingError(String s);
}
