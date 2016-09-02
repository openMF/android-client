package com.mifos.mifosxdroid.online.savingsaccount;

import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.objects.zipmodels.SavingProductsAndTemplate;
import com.mifos.services.data.SavingsPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class SavingsAccountPresenter extends BasePresenter<SavingsAccountMvpView> {

    private final DataManagerSavings mDataManagerSavings;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SavingsAccountPresenter(DataManagerSavings dataManagerSavings) {
        mDataManagerSavings = dataManagerSavings;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SavingsAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadSavingsAccounts() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(Observable.combineLatest(
                mDataManagerSavings.getSavingsAccounts(),
                mDataManagerSavings.getSavingsAccountTemplate(),
                new Func2<List<ProductSavings>, SavingProductsTemplate,
                        SavingProductsAndTemplate>() {
                    @Override
                    public SavingProductsAndTemplate call(List<ProductSavings> productSavings,
                                                          SavingProductsTemplate template) {
                        return new SavingProductsAndTemplate(productSavings, template);
                    }
                }
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingProductsAndTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load SavingProducts and " +
                                "template");
                    }

                    @Override
                    public void onNext(SavingProductsAndTemplate productsAndTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccounts(productsAndTemplate.getmProductSavings());
                        getMvpView().showSavingsAccountTemplate(
                                productsAndTemplate.getmSavingProductsTemplate());
                    }
                })
        );
    }


    public void createSavingsAccount(SavingsPayload savingsPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSavings.createSavingsAccount(savingsPayload)
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
                }));
    }
}
