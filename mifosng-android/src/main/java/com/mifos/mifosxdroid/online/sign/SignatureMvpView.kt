package com.mifos.mifosxdroid.online.sign

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Tarun on 29-06-2017.
 */
interface SignatureMvpView : MvpView {
    fun showSignatureUploadedSuccessfully(response: GenericResponse?)
    fun saveAndUploadSignature()
    fun requestPermission()
    fun checkPermissionAndRequest()
    fun showError(errorId: Int)
    val documentFromGallery: Unit
}