package com.mifos.mifosxdroid.online.savingsaccountactivate;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.utils.MFErrorParser;

import java.util.HashMap;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tarun on 01/06/17.
 */
public class SavingsAccountActivatePresenter extends BasePresenter<SavingsAccountActivateMvpView> {

    private final DataManagerSavings mDataManagerSavings;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SavingsAccountActivatePresenter(DataManagerSavings dataManager) {
        mDataManagerSavings = dataManager;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SavingsAccountActivateMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }

    public void activateSavings(int savingsAccountId, HashMap<String, Object> request) {
        checkViewAttached();
        getMvpView().showProgressbar(false);
        mSubscriptions.add(mDataManagerSavings.activateSavings(savingsAccountId, request)
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
                        getMvpView().showSavingAccountActivatedSuccessfully(genericResponse);
                    }
                }));
    }

}
