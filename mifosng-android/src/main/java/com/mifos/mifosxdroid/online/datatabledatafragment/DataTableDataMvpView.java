package com.mifos.mifosxdroid.online.datatabledatafragment;

import com.google.gson.JsonArray;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface DataTableDataMvpView extends MvpView {

    void showDataTableData(JsonArray jsonElements);

    void ResponseErrorDataTable(String s);
}
