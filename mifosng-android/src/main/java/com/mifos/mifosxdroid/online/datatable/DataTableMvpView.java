package com.mifos.mifosxdroid.online.datatable;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

/**
 * Created by Rajan Maurya on 12/02/17.
 */

public interface DataTableMvpView extends MvpView {

    void showUserInterface();

    void showDataTables(List<DataTable> dataTables);

    void showEmptyDataTables();

    void showResetVisibility();

    void showError(int message);
}
