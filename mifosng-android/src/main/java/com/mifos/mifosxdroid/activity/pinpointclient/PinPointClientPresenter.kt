package com.mifos.mifosxdroid.activity.pinpointclient

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.ClientAddressRequest
import com.mifos.objects.client.ClientAddressResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class PinPointClientPresenter @Inject constructor(private val dataManagerClient: DataManagerClient) :
    BasePresenter<PinPointClientMvpView>() {
    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        subscriptions.clear()
    }

    fun getClientPinpointLocations(clientId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscriptions.add(
            dataManagerClient.getClientPinpointLocations(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<ClientAddressResponse>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showMessage(R.string.failed_to_fetch_pinpoint_location)
                        mvpView?.showFailedToFetchAddress()
                    }

                    override fun onNext(clientAddressResponses: List<ClientAddressResponse>) {
                        mvpView?.showProgressbar(false)
                        if (clientAddressResponses.isEmpty()) {
                            mvpView?.showEmptyAddress()
                        } else {
                            mvpView?.showClientPinpointLocations(clientAddressResponses)
                        }
                    }
                })
        )
    }

    fun addClientPinpointLocation(clientId: Int, addressRequest: ClientAddressRequest?) {
        checkViewAttached()
        mvpView?.showProgressDialog(true, R.string.adding_client_address)
        subscriptions.add(
            dataManagerClient.addClientPinpointLocation(clientId, addressRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressDialog(false, null)
                        mvpView?.showMessage(R.string.failed_to_add_pinpoint_location)
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        mvpView?.showProgressDialog(false, null)
                        mvpView?.updateClientAddress(
                            R.string.address_added_successfully
                        )
                    }
                })
        )
    }

    fun deleteClientPinpointLocation(apptableId: Int, datatableId: Int) {
        checkViewAttached()
        mvpView?.showProgressDialog(true, R.string.deleting_client_address)
        subscriptions
            .add(
                dataManagerClient.deleteClientAddressPinpointLocation(apptableId, datatableId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Subscriber<GenericResponse>() {
                        override fun onCompleted() {}
                        override fun onError(e: Throwable) {
                            mvpView?.showProgressDialog(false, null)
                            mvpView?.showMessage(R.string.failed_to_delete_pinpoint_location)
                        }

                        override fun onNext(genericResponse: GenericResponse) {
                            mvpView?.showProgressDialog(false, null)
                            mvpView?.updateClientAddress(R.string.address_deleted_successfully)
                        }
                    })
            )
    }

    fun updateClientPinpointLocation(
        apptableId: Int, datatableId: Int,
        addressRequest: ClientAddressRequest?
    ) {
        checkViewAttached()
        mvpView?.showProgressDialog(true, R.string.updating_client_address)
        subscriptions.add(
            dataManagerClient.updateClientPinpointLocation(
                apptableId, datatableId, addressRequest
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressDialog(false, null)
                        mvpView?.showMessage(R.string.failed_to_update_pinpoint_location)
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        mvpView?.showProgressDialog(false, null)
                        mvpView?.updateClientAddress(R.string.address_updated_successfully)
                    }
                })
        )
    }
}