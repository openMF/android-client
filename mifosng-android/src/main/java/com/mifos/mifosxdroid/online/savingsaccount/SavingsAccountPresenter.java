package com.mifos.mifosxdroid.online.savingsaccount;

import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Savings;
import com.mifos.objects.common.InterestType;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.objects.zipmodels.SavingProductsAndTemplate;
import com.mifos.services.data.SavingsPayload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    public void loadSavingsAccountsAndTemplate() {
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
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingProductsAndTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(
                                R.string.failed_to_load_savings_products_and_template);
                    }

                    @Override
                    public void onNext(SavingProductsAndTemplate productsAndTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccounts(productsAndTemplate
                                .getmProductSavings());
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_add_savings_account);
                    }

                    @Override
                    public void onNext(Savings savings) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccountCreatedSuccessfully(savings);
                    }
                }));
    }

    public List<String> filterSpinnerOptions(List<InterestType> interestTypes) {
        final ArrayList<String> interestNameList = new ArrayList<>();
        Observable.from(interestTypes)
                .subscribe(new Action1<InterestType>() {
                    @Override
                    public void call(InterestType interestType) {
                        interestNameList.add(interestType.getValue());
                    }
                });
        return interestNameList;
    }

    public List<String> filterSavingProductsNames(List<ProductSavings> productSavings) {
        final ArrayList<String> productsNames = new ArrayList<>();
        Observable.from(productSavings)
                .subscribe(new Action1<ProductSavings>() {
                    @Override
                    public void call(ProductSavings product) {
                        productsNames.add(product.getName());
                    }
                });
        return productsNames;
    }
}
