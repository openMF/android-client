package com.mifos.mifosxdroid.online.savingsaccountfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class SavingsAccountPresenter implements Presenter<SavingsAccountMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private SavingsAccountMvpView mSavingsAccountMvpView;

    public SavingsAccountPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SavingsAccountMvpView mvpView) {
        mSavingsAccountMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mSavingsAccountMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void CreateSavingAccount(SavingsPayload savingsPayload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createSavingsAccount(savingsPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Savings>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSavingsAccountMvpView.ResponseError("Try Again");
                    }

                    @Override
                    public void onNext(Savings savings) {
                        mSavingsAccountMvpView.showAccountCreationStatus(savings);
                    }
                });

    }

    public void getSavingsProductsTemplate(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getSavingsProductsTemplate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingProductsTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSavingsAccountMvpView.ResponseError("Failed to fetch SavingTemplate");
                    }

                    @Override
                    public void onNext(SavingProductsTemplate savingProductsTemplate) {
                        mSavingsAccountMvpView.showSavingProductsTemplate(savingProductsTemplate);
                    }
                });
    }

    public void getAllSavingsAccounts(){
        if(mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllSavingsAccounts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ProductSavings>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSavingsAccountMvpView.ResponseError("Failed to load AllSavingAccount");
                    }

                    @Override
                    public void onNext(List<ProductSavings> productSavingses) {
                        mSavingsAccountMvpView.showAllSavingAccount(productSavingses);
                    }
                });
    }
}
