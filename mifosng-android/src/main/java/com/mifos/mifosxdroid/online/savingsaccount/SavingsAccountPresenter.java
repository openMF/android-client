package com.mifos.mifosxdroid.online.savingsaccount;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class SavingsAccountPresenter extends BasePresenter<SavingsAccountMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public SavingsAccountPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SavingsAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadSavingsAccounts() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getSavingsAccounts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ProductSavings>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load SavingsAccounts");
                    }

                    @Override
                    public void onNext(List<ProductSavings> productSavingses) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccounts(productSavingses);
                    }
                });

    }

    public void createSavingsAccount(SavingsPayload savingsPayload){
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createSavingsAccount(savingsPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Savings>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Try Again");
                    }

                    @Override
                    public void onNext(Savings savings) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccountCreatedSuccessfully(savings);
                    }
                });
    }

    public void loadSavingsAccountTemplate() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getSavingsAccountTemplate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingProductsTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch SavingsAccountTemplate");
                    }

                    @Override
                    public void onNext(SavingProductsTemplate savingProductsTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccountTemplate(savingProductsTemplate);
                    }
                });
    }

}
