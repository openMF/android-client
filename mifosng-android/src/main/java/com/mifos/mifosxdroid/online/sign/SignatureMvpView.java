package com.mifos.mifosxdroid.online.sign;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.DocumentRelatedResponse;

/**
 * Created by Tarun on 29-06-2017.
 */

public interface SignatureMvpView extends MvpView {

    void showSignatureUploadedSuccessfully(DocumentRelatedResponse response);

    void saveAndUploadSignature();

    void requestPermission();

    void checkPermissionAndRequest();

    void showError(int errorId);

    void getDocumentFromGallery();
}
