package com.mifos.mifosxdroid.dialogfragments.documentdialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.DocumentRelatedResponse;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface DocumentDialogMvpView extends MvpView {

    void checkPermissionAndRequest();

    void requestPermission();

    void getExternalStorageDocument();

    void showDocumentedCreatedSuccessfully(DocumentRelatedResponse
                                                   documentCreationResponse);

    void showDocumentUpdatedSuccessfully(DocumentRelatedResponse
                                                 documentUpdateResponse);

    void showCreationError(String errorMessage);

    void showUpdationError(String errorMessage);
}
