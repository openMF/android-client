package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.noncore.IdentifierCreationResponse
import com.mifos.objects.noncore.IdentifierTemplate

/**
 * Created by Rajan Maurya on 01/10/16.
 */
interface IdentifierDialogMvpView : MvpView {
    fun showIdentifierSpinners()
    fun showClientIdentifierTemplate(identifierTemplate: IdentifierTemplate)
    fun showIdentifierCreatedSuccessfully(identifierCreationResponse: IdentifierCreationResponse)
    fun showErrorMessage(message: String)
    fun showError(errorMessage: Int)
}