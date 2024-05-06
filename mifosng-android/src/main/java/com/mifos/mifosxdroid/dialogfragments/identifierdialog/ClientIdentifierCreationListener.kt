package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import com.mifos.core.objects.noncore.Identifier

/**
 * Created by Tarun on 07-08-17.
 */
interface ClientIdentifierCreationListener {
    fun onClientIdentifierCreationSuccess(identifier: Identifier)

    fun onClientIdentifierCreationFailure(errorMessage: String)
}