package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface DataTableRowDialogMvpView extends MvpView {

    void showDataTableEntrySuccessfully(GenericResponse genericResponse);

    void showError(String s, Response response);
}
