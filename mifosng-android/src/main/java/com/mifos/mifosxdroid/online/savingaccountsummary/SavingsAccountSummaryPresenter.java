package com.mifos.mifosxdroid.online.savingaccountsummary;

import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.utils.Constants;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class SavingsAccountSummaryPresenter extends BasePresenter<SavingsAccountSummaryMvpView> {

    private final DataManagerSavings mDataManagerSavings;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SavingsAccountSummaryPresenter(DataManagerSavings dataManagerSavings) {
        mDataManagerSavings = dataManagerSavings;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(SavingsAccountSummaryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    //This Method will hit end point ?associations=transactions
    public void loadSavingAccount(String type, int accountId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerSavings
                .getSavingsAccount(type, accountId, Constants.TRANSACTIONS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<SavingsAccountWithAssociations>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_savingsaccount);
                    }

                    @Override
                    public void onNext(
                            SavingsAccountWithAssociations savingsAccountWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingAccount(savingsAccountWithAssociations);
                    }
                }));
    }



}
