package com.mifos.mifosxdroid.online.savingaccounttransaction;

import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
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

    private final DataManagerSavings mDataManagerSavings;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SavingsAccountTransactionPresenter(DataManagerSavings dataManagerSavings) {
        mDataManagerSavings = dataManagerSavings;
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
        mSubscriptions.add(mDataManagerSavings
                .getSavingsAccountTransactionTemplate(type, accountId, transactionType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionTemplate>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_fetch_savings_template);
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
        mSubscriptions.add(mDataManagerSavings
                .processTransaction(type, accountId, transactionType, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.transaction_failed);
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

    public void checkInDatabaseSavingAccountTransaction(int savingAccountId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSavings.getDatabaseSavingsAccountTransaction(savingAccountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionRequest>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_savingaccounttransaction);
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionRequest
                                               savingsAccountTransactionRequest) {
                        getMvpView().showProgressbar(false);
                        if (savingsAccountTransactionRequest != null) {
                            getMvpView().showSavingAccountTransactionExistInDatabase();
                        } else {
                            getMvpView().showSavingAccountTransactionDoesNotExistInDatabase();
                        }
                    }
                })
        );
    }

}
