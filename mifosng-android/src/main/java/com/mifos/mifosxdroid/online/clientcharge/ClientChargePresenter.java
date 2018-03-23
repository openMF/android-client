package com.mifos.mifosxdroid.online.clientcharge;

import com.mifos.api.datamanager.DataManagerCharge;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


/**
 * Created by Rajan Maurya on 5/6/16.
 */
public class ClientChargePresenter extends BasePresenter<ClientChargeMvpView> {


    private final DataManagerCharge mDataManagerCharge;
    private CompositeDisposable mCompositeDisposable;

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
        if (mCompositeDisposable != null) mCompositeDisposable.clear();
    }

    public void loadCharges(int clientId, int offset, int limit) {
        getMvpView().showProgressbar(true);
        if (mCompositeDisposable != null) mCompositeDisposable.clear();
        mDataManagerCharge.getClientCharges(clientId, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<Page<Charges>>() {
                    @Override
                    public void onComplete() {
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
                            RxJavaPlugins.getErrorHandler();
                        }
                    }

                    @Override
                    public void onNext(Page<Charges> chargesPage) {
                        getMvpView().showProgressbar(false);
                        if (chargesPage.getTotalFilteredRecords() > 0) {
                            getMvpView().showChargesList(chargesPage);
                        } else {
                            getMvpView().showEmptyCharges();
                        }
                    }
                });
    }


}
