package com.mifos.mifosxdroid.online.activateclient;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.ClientActivate;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 09/02/17.
 */

public class ActivateClientPresenter extends BasePresenter<ActivateClientMvpView> {

    private final DataManagerClient dataManagerClient;
    private CompositeSubscription subscriptions;

    @Inject
    public ActivateClientPresenter(DataManagerClient dataManagerClient) {
        this.dataManagerClient = dataManagerClient;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(ActivateClientMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscriptions.unsubscribe();
    }

    public void activateClient(int clientId, ClientActivate clientActivate) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerClient.activateClient(clientId, clientActivate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientActivatedSuccessfully();
                    }
                })
        );
    }
}
