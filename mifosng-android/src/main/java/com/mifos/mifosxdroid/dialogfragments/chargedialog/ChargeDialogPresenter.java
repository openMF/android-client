package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Charges;
import com.mifos.objects.templates.clients.ChargeOptions;
import com.mifos.objects.templates.clients.ChargeTemplate;
import com.mifos.services.data.ChargesPayload;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
                        getMvpView().showFetchingError("Failed to fetch Charges");
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
                .subscribe(new Subscriber<Charges>() {
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
                    public void onNext(Charges charges) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showChargesCreatedSuccessfully(charges);
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
