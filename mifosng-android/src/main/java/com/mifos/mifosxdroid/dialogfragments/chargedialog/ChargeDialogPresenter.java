package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerCharge;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.ChargeCreationResponse;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.services.data.ChargesPayload;
import com.mifos.utils.MFErrorParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class ChargeDialogPresenter extends BasePresenter<ChargeDialogMvpView> {

    private final DataManager mDataManager;
    private final DataManagerCharge mDataManagerCharge;
    private Subscription mSubscription;
    private int limit = 100;

    @Inject
    public ChargeDialogPresenter(DataManager dataManager, DataManagerCharge dataManagerCharge) {
        mDataManager = dataManager;
        mDataManagerCharge = dataManagerCharge;
    }

    @Override
    public void attachView(ChargeDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadAllChargesV2(int clientId, int offset) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerCharge.getClientCharges(clientId, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Charges>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        try {
                            if (e instanceof HttpException) {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                getMvpView().showFetchingError(MFErrorParser
                                        .parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(Page<Charges> chargesPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllChargesV2(chargesPage);
                    }
                });
    }

    public void createCharges(int clientId, ChargesPayload payload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createCharges(clientId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ChargeCreationResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        try {
                            if (e instanceof HttpException) {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                getMvpView().showChargeCreatedFailure(MFErrorParser
                                        .parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(ChargeCreationResponse chargeCreationResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showChargesCreatedSuccessfully(chargeCreationResponse);
                    }
                });
    }

    public List<String> filterChargeName(final List<Charges>
                                                 chargesList) {
        final ArrayList<String> chargeNameList = new ArrayList<>();
        Observable.from(chargesList)
                .subscribe(new Action1<Charges>() {
                    @Override
                    public void call(Charges charges) {
                        chargeNameList.add(charges.getName());
                    }
                });
        return chargeNameList;
    }
}
