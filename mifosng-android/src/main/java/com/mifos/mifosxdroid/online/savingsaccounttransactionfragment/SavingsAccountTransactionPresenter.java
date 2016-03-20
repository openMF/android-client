package com.mifos.mifosxdroid.online.savingsaccounttransactionfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class SavingsAccountTransactionPresenter implements Presenter<SavingsAccountTransactionMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private SavingsAccountTransactionMvpView mSavingsAccountTransactionMvpView;

    public SavingsAccountTransactionPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SavingsAccountTransactionMvpView mvpView) {
        mSavingsAccountTransactionMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mSavingsAccountTransactionMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadSavingAccountTemplate(String savingaccounttype, int accountId, String transactionType){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getSavingsAccountTransactionTemplate(savingaccounttype,accountId,transactionType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSavingsAccountTransactionMvpView.ResponseError("Failed To fetch SavingAccount");
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionTemplate savingsAccountTransactionTemplate) {
                        mSavingsAccountTransactionMvpView.showSavingAccountTemlate(savingsAccountTransactionTemplate);
                    }
                });
    }

    public void ProcessTransaction(String type, int accountId, String transactionType, SavingsAccountTransactionRequest request){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.processTransaction(type,accountId,transactionType,request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSavingsAccountTransactionMvpView.ResponseError("Transaction Failed");
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionResponse savingsAccountTransactionResponse) {
                        mSavingsAccountTransactionMvpView.showProcessTransactionResult(savingsAccountTransactionResponse);
                    }
                });
    }
}
