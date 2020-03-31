package com.mifos.mifosxdroid.dialogfragments.documentdialog;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface DocumentDialogMvpView extends MvpView {

    void checkPermissionAndRequest();

    void requestPermission();

    void getExternalStorageDocument();

    void showDocumentedCreatedSuccessfully(GenericResponse genericResponse);

    void showDocumentUpdatedSuccessfully();

    void showError(int errorMessage);

    void showUploadError(String errorMessage);
}
