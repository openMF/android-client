package com.mifos.mifosxdroid.dialogfragments.identifierdialog;

import com.mifos.objects.noncore.Identifier;

/**
 * Created by Tarun on 07-08-17.
 */

public interface ClientIdentifierCreationListener {

    void onClientIdentifierCreationSuccess(Identifier identifier);

    void onClientIdentifierCreationFailure(String errorMessage);
}
