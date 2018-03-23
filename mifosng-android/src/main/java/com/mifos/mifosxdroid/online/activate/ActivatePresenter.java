package com.mifos.mifosxdroid.online.activate;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.ActivatePayload;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 09/02/17.
 */

public class ActivatePresenter extends BasePresenter<ActivateMvpView> {

    private final DataManagerClient dataManagerClient;
    private final DataManagerCenter dataManagerCenter;
    private final DataManagerGroups dataManagerGroups;
    private CompositeDisposable compositeDisposable;

    @Inject
    public ActivatePresenter(DataManagerClient dataManagerClient,
                             DataManagerCenter dataManagerCenter,
                             DataManagerGroups dataManagerGroups) {
        this.dataManagerClient = dataManagerClient;
        this.dataManagerCenter = dataManagerCenter;
        this.dataManagerGroups = dataManagerGroups;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(ActivateMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void activateClient(int clientId, ActivatePayload clientActivate) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerClient.activateClient(clientId, clientActivate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showActivatedSuccessfully(
                                R.string.client_activated_successfully);
                    }
                })
        );
    }

    public void activateCenter(int centerId, ActivatePayload activatePayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerCenter.activateCenter(centerId, activatePayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showActivatedSuccessfully(
                                R.string.center_activated_successfully);
                    }
                })
        );
    }

    public void activateGroup(int groupId, ActivatePayload activatePayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerGroups.activateGroup(groupId, activatePayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showActivatedSuccessfully(R.string.group_created_successfully);
                    }
                })
        );

    }
}
