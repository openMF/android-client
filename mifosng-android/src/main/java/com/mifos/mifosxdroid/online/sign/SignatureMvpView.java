package com.mifos.mifosxdroid.online.sign;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Tarun on 29-06-2017.
 */

public interface SignatureMvpView extends MvpView {

    void showSignatureUploadedSuccessfully(GenericResponse response);

    void saveAndUploadSignature();

    void requestPermission();

    void checkPermissionAndRequest();

    void showError(int errorId);

    void getDocumentFromGallery();
}
