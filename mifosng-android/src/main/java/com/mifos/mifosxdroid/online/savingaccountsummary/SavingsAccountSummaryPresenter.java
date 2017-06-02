package com.mifos.mifosxdroid.online.savingaccountsummary;

import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.utils.Constants;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class SavingsAccountSummaryPresenter extends BasePresenter<SavingsAccountSummaryMvpView> {

    private final DataManagerSavings mDataManagerSavings;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SavingsAccountSummaryPresenter(DataManagerSavings dataManagerSavings) {
        mDataManagerSavings = dataManagerSavings;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SavingsAccountSummaryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    //This Method will hit end point ?associations=transactions
    public void loadSavingAccount(String type, int accountId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSavings
                .getSavingsAccount(type, accountId, Constants.TRANSACTIONS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountWithAssociations>() {
                    @Override
                    public void onCompleted() {
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
