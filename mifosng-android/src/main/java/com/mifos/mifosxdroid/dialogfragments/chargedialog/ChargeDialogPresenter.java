package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.ChargeCreationResponse;
import com.mifos.objects.templates.clients.ChargeOptions;
import com.mifos.objects.templates.clients.ChargeTemplate;
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
    private Subscription mSubscription;

    @Inject
    public ChargeDialogPresenter(DataManager dataManager) {
        mDataManager = dataManager;
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

    public void loadAllChargesV2(int clientId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllChargesV2(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ChargeTemplate>() {
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
                    public void onNext(ChargeTemplate chargeTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllChargesV2(chargeTemplate);
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

    public List<String> filterChargeName(final List<ChargeOptions>
                                                 chargeOptions) {
        final ArrayList<String> chargeNameList = new ArrayList<>();
        Observable.from(chargeOptions)
                .subscribe(new Action1<ChargeOptions>() {
                    @Override
                    public void call(ChargeOptions chargeOptions) {
                        chargeNameList.add(chargeOptions.getName());
                    }
                });
        return chargeNameList;
    }
}
