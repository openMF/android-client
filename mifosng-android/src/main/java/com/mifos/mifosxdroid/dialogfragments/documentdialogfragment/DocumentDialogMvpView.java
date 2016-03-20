package com.mifos.mifosxdroid.dialogfragments.documentdialogfragment;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface DocumentDialogMvpView extends MvpView {

    void showDocumentCreationResult(GenericResponse genericResponse);

    void ResponseError(String s);
}
