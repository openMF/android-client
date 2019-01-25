package com.mifos.mifosxdroid.online.clientcharge;

import com.mifos.api.datamanager.DataManagerCharge;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public class ClientChargePresenter extends BasePresenter<ClientChargeMvpView> {


    private final DataManagerCharge mDataManagerCharge;
    private Subscription mSubscription;

    @Inject
    public ClientChargePresenter(DataManagerCharge dataManagerCharge) {
        mDataManagerCharge = dataManagerCharge;
    }


    @Override
    public void attachView(ClientChargeMvpView mvpView) {
        super.attachView(mvpView);
    }


    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadCharges(int clientId, int offset, int limit) {
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
                                getMvpView().showFetchingErrorCharges(MFErrorParser
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
                        if (chargesPage.getPageItems().size() > 0) {
                            getMvpView().showChargesList(chargesPage);
                        } else {
                            getMvpView().showEmptyCharges();
                        }
                    }
                });
    }


}
