package com.mifos.mifosxdroid.dialogfragments.identifierdialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.IdentifierCreationResponse;
import com.mifos.objects.noncore.IdentifierTemplate;

/**
 * Created by Rajan Maurya on 01/10/16.
 */

public interface IdentifierDialogMvpView extends MvpView {

    void showIdentifierSpinners();

    void showClientIdentifierTemplate(IdentifierTemplate identifierTemplate);

    void showIdentifierCreatedSuccessfully(IdentifierCreationResponse identifierCreationResponse);

    void showErrorMessage(String message);

    void showError(int errorMessage);
}
