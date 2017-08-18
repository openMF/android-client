package com.mifos.mifosxdroid.online.sharingaccount;

import android.support.annotation.NonNull;

import com.mifos.api.datamanager.DataManagerSharing;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.common.InterestType;
import com.mifos.objects.organisation.ProductSharing;
import com.mifos.objects.response.ShareResponse;
import com.mifos.objects.templates.clients.ChargeOptions;
import com.mifos.objects.templates.sharing.SharingProductTemplate;
import com.mifos.objects.templates.sharing.SharingAccountsTemplate;
import com.mifos.services.data.SharingPayload;
import com.mifos.utils.MFErrorParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mayankjindal on 18/08/17.
 */

public class SharingAccountPresenter extends BasePresenter<SharingAccountMvpView> {

    private final DataManagerSharing mDataManagerSharing;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SharingAccountPresenter(@NonNull DataManagerSharing dataManagerSharing) {
        mDataManagerSharing = dataManagerSharing;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(@NonNull SharingAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }

    public void loadSharingAccountsAndTemplate(int clientId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSharing.
                getSharingAccountTemplate(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SharingProductTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError
                                (R.string.failed_to_load_sharing_products_and_template);
                    }

                    @Override
                    public void onNext(SharingProductTemplate productTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSharingAccountTemplate(productTemplate.
                                getProductOptions(), productTemplate.getChargeOptions());
                    }
                }));
    }

    public void loadSharingAccountTemplateByProduct(int clientId, int productId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSharing.
                getClientSharingAccountTemplateByProduct(clientId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SharingAccountsTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError
                                (R.string.failed_to_load_sharing_products_and_template);
                    }

                    @Override
                    public void onNext(SharingAccountsTemplate sharingAccountsTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccountTemplateByProduct(sharingAccountsTemplate);
                    }
                }));
    }

    public void createSharingAccount(@NonNull SharingPayload sharingPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSharing.createSharingAccount(sharingPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ShareResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(ShareResponse shareResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSharingAccountCreatedSuccessfully(shareResponse);
                    }
                }));
    }

    @NonNull
    public List<String> filterSharingProductsNames(@NonNull List<ProductSharing> productSharings) {
        final ArrayList<String> productsNames = new ArrayList<>();
        Observable.from(productSharings)
                .subscribe(new Action1<ProductSharing>() {
                    @Override
                    public void call(ProductSharing product) {
                        productsNames.add(product.getName());
                    }
                });
        return productsNames;
    }

    @NonNull
    public List<String> filterChargeOptionNames(@NonNull List<ChargeOptions> chargeOptions) {
        final ArrayList<String> productsNames = new ArrayList<>();
        Observable.from(chargeOptions)
                .subscribe(new Action1<ChargeOptions>() {
                    @Override
                    public void call(ChargeOptions charge) {
                        productsNames.add(charge.getName());
                    }
                });
        return productsNames;
    }

    @NonNull
    public List<String> filterLockinPeriodFrequencyTypesName(
            @NonNull List<InterestType> lockinPeriodFrequencyTypeOptionsList) {
        final ArrayList<String> lockinPeriodFrequencyTypeOptions = new ArrayList<>();
        Observable.from(lockinPeriodFrequencyTypeOptionsList)
                .subscribe(new Action1<InterestType>() {
                    @Override
                    public void call(InterestType lockinPeriodFrequencyTypeOption) {
                        lockinPeriodFrequencyTypeOptions.add(
                                lockinPeriodFrequencyTypeOption.getValue());
                    }
                });
        return lockinPeriodFrequencyTypeOptions;
    }

    @NonNull
    public List<String> filterMinimumActivePeriodFrequencyTypesNames(
            @NonNull List<InterestType> minimumActivePeriodFrequencyTypeOptionsList) {
        final ArrayList<String> minimumActivePeriodFrequencyTypeOptions = new ArrayList<>();
        Observable.from(minimumActivePeriodFrequencyTypeOptionsList)
                .subscribe(new Action1<InterestType>() {
                    @Override
                    public void call(InterestType minimumActivePeriodFrequencyTypeOption) {
                        minimumActivePeriodFrequencyTypeOptions.add(
                                minimumActivePeriodFrequencyTypeOption.getValue());
                    }
                });
        return minimumActivePeriodFrequencyTypeOptions;
    }

    @NonNull
    public List<String> filterSavingProductsNames(@NonNull List<SavingsAccount>
                                                              savingAccountOptionsList) {
        final ArrayList<String> lockinPeriodFrequencyTypeOptions = new ArrayList<>();
        Observable.from(savingAccountOptionsList)
                .subscribe(new Action1<SavingsAccount>() {
                    @Override
                    public void call(SavingsAccount savingAccountOption) {
                        lockinPeriodFrequencyTypeOptions.add(savingAccountOption.getAccountNo());
                    }
                });
        return lockinPeriodFrequencyTypeOptions;
    }
}
