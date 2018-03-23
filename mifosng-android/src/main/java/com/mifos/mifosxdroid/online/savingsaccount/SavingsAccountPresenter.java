package com.mifos.mifosxdroid.online.savingsaccount;

import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.savings.FieldOfficerOptions;
import com.mifos.objects.client.Savings;
import com.mifos.objects.common.InterestType;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.objects.zipmodels.SavingProductsAndTemplate;
import com.mifos.services.data.SavingsPayload;
import com.mifos.utils.MFErrorParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class SavingsAccountPresenter extends BasePresenter<SavingsAccountMvpView> {

    private final DataManagerSavings mDataManagerSavings;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SavingsAccountPresenter(DataManagerSavings dataManagerSavings) {
        mDataManagerSavings = dataManagerSavings;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(SavingsAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadSavingsAccountsAndTemplate() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(Observable.combineLatest(
                mDataManagerSavings.getSavingsAccounts(),
                mDataManagerSavings.getSavingsAccountTemplate(),
                new BiFunction<List<ProductSavings>, SavingProductsTemplate,
                                        SavingProductsAndTemplate>() {
                    @Override
                    public SavingProductsAndTemplate apply(List<ProductSavings> productSavings,
                                                          SavingProductsTemplate template) {
                        return new SavingProductsAndTemplate(productSavings, template);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<SavingProductsAndTemplate>() {
                    @Override
                    public void onComplete() {

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
                    }
                })
        );
    }

    public void loadClientSavingAccountTemplateByProduct(int clientId, int productId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerSavings.
                getClientSavingsAccountTemplateByProduct(clientId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<SavingProductsTemplate>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError
                                (R.string.failed_to_load_savings_products_and_template);
                    }

                    @Override
                    public void onNext(SavingProductsTemplate savingProductsTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccountTemplateByProduct(savingProductsTemplate);
                    }
                }));
    }

    public void loadGroupSavingAccountTemplateByProduct(int groupId, int productId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerSavings.
                getGroupSavingsAccountTemplateByProduct(groupId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<SavingProductsTemplate>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError
                                (R.string.failed_to_load_savings_products_and_template);
                    }

                    @Override
                    public void onNext(SavingProductsTemplate savingProductsTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccountTemplateByProduct(savingProductsTemplate);
                    }
                }));
    }

    public void createSavingsAccount(SavingsPayload savingsPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerSavings.createSavingsAccount(savingsPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Savings>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(MFErrorParser.errorMessage(e));
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
        Observable.fromIterable(interestTypes)
                .subscribe(new Consumer<InterestType>() {
                    @Override
                    public void accept(InterestType interestType) {
                        interestNameList.add(interestType.getValue());
                    }
                });
        return interestNameList;
    }

    public List<String> filterSavingProductsNames(List<ProductSavings> productSavings) {
        final ArrayList<String> productsNames = new ArrayList<>();
        Observable.fromIterable(productSavings)
                .subscribe(new Consumer<ProductSavings>() {
                    @Override
                    public void accept(ProductSavings product) {
                        productsNames.add(product.getName());
                    }
                });
        return productsNames;
    }

    public List<String> filterFieldOfficerNames(List<FieldOfficerOptions> fieldOfficerOptions) {
        final ArrayList<String> fieldOfficerNames = new ArrayList<>();
        Observable.fromIterable(fieldOfficerOptions)
                .subscribe(new Consumer<FieldOfficerOptions>() {
                    @Override
                    public void accept(FieldOfficerOptions fieldOfficerOptions) {
                        fieldOfficerNames.add(fieldOfficerOptions.getDisplayName());
                    }
                });
        return fieldOfficerNames;
    }
}
