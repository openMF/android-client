package com.mifos.mifosxdroid.dialogfragments.datatablerowdialogfragment;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface DataTableRowDialogMvpView extends MvpView {

    void showDatatableRawCreationEntry(GenericResponse genericResponse);

    void ResponseCreationError(String s);
}
