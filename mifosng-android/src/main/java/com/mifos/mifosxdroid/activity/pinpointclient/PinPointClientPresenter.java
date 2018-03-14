package com.mifos.mifosxdroid.activity.pinpointclient;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.ClientAddressRequest;
import com.mifos.objects.client.ClientAddressResponse;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class PinPointClientPresenter extends BasePresenter<PinPointClientMvpView> {

    private final DataManagerClient dataManagerClient;
    private CompositeSubscription subscriptions;

    @Inject
    public PinPointClientPresenter(DataManagerClient dataManagerClient) {
        this.dataManagerClient = dataManagerClient;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(PinPointClientMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscriptions.clear();
    }

    public void getClientPinpointLocations(int clientId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerClient.getClientPinpointLocations(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ClientAddressResponse>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.failed_to_fetch_pinpoint_location);
                        getMvpView().showFailedToFetchAddress();
                    }

                    @Override
                    public void onNext(List<ClientAddressResponse> clientAddressResponses) {
                        getMvpView().showProgressbar(false);
                        if (clientAddressResponses.size() == 0) {
                            getMvpView().showEmptyAddress();
                        } else {
                            getMvpView().showClientPinpointLocations(clientAddressResponses);
                        }
                    }
                })
        );
    }

    public void addClientPinpointLocation(int clientId, ClientAddressRequest addressRequest) {
        checkViewAttached();
        getMvpView().showProgressDialog(true, R.string.adding_client_address);
        subscriptions.add(dataManagerClient.addClientPinpointLocation(clientId, addressRequest)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<GenericResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                getMvpView().showProgressDialog(false, null);
                                getMvpView().showMessage(R.string.failed_to_add_pinpoint_location);
                            }

                            @Override
                            public void onNext(GenericResponse genericResponse) {
                                getMvpView().showProgressDialog(false, null);
                                getMvpView().updateClientAddress(
                                        R.string.address_added_successfully);
                            }
                        })

        );
    }

    public void deleteClientPinpointLocation(int apptableId, int datatableId) {
        checkViewAttached();
        getMvpView().showProgressDialog(true, R.string.deleting_client_address);
        subscriptions
                .add(dataManagerClient.deleteClientAddressPinpointLocation(apptableId, datatableId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressDialog(false, null);
                        getMvpView().showMessage(R.string.failed_to_delete_pinpoint_location);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressDialog(false, null);
                        getMvpView().updateClientAddress(R.string.address_deleted_successfully);
                    }
                })
        );
    }

    public void updateClientPinpointLocation(int apptableId, int datatableId,
                                             ClientAddressRequest addressRequest) {
        checkViewAttached();
        getMvpView().showProgressDialog(true, R.string.updating_client_address);
        subscriptions.add(dataManagerClient.updateClientPinpointLocation(
                apptableId, datatableId, addressRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressDialog(false, null);
                        getMvpView().showMessage(R.string.failed_to_update_pinpoint_location);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressDialog(false, null);
                        getMvpView().updateClientAddress(R.string.address_updated_successfully);
                    }
                })

        );
    }
}
