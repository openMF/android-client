package com.mifos.mifosxdroid.online.savingaccounttransaction;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class SavingsAccountTransactionPresenter
        extends BasePresenter<SavingsAccountTransactionMvpView> {

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SavingsAccountTransactionPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SavingsAccountTransactionMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadSavingAccountTemplate(String type, int accountId, String transactionType) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManager.getSavingsAccountTemplate(type, accountId, transactionType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load Saving Template");
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionTemplate
                                               savingsAccountTransactionTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingAccountTemplate(savingsAccountTransactionTemplate);
                    }
                }));
    }

    public void processTransaction(String type, int accountId, String transactionType,
                                   SavingsAccountTransactionRequest request) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManager.processTransaction(type, accountId, transactionType,
                request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Transaction Failed");
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionResponse
                                               savingsAccountTransactionResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().
                                showTransactionSuccessfullyDone(savingsAccountTransactionResponse);
                    }
                }));
    }

}
