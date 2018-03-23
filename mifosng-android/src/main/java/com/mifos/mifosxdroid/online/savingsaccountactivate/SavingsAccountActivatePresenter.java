package com.mifos.mifosxdroid.online.savingsaccountactivate;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.utils.MFErrorParser;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Tarun on 01/06/17.
 */
public class SavingsAccountActivatePresenter extends BasePresenter<SavingsAccountActivateMvpView> {

    private final DataManagerSavings mDataManagerSavings;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SavingsAccountActivatePresenter(DataManagerSavings dataManager) {
        mDataManagerSavings = dataManager;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(SavingsAccountActivateMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void activateSavings(int savingsAccountId, HashMap<String, Object> request) {
        checkViewAttached();
        getMvpView().showProgressbar(false);
        compositeDisposable.add(mDataManagerSavings.activateSavings(savingsAccountId, request)
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
                        getMvpView().showSavingAccountActivatedSuccessfully(genericResponse);
                    }
                }));
    }

}
