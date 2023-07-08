package com.mifos.mifosxdroid.activity.pinpointclient

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.ClientAddressResponse

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface PinPointClientMvpView : MvpView {
    fun showUserInterface()
    fun requestPermission(requestCode: Int)
    fun showClientPinpointLocations(clientAddressResponses: List<ClientAddressResponse>)
    fun showFailedToFetchAddress()
    fun showEmptyAddress()
    fun showPlacePiker(requestCode: Int)
    fun showProgressDialog(show: Boolean, message: Int?)
    fun showMessage(message: Int)
    fun updateClientAddress(message: Int)
}