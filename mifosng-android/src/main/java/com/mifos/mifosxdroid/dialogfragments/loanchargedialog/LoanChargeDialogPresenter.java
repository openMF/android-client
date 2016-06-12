package com.mifos.mifosxdroid.dialogfragments.loanchargedialog;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Charges;
import com.mifos.services.data.ChargesPayload;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 9/6/16.
 */
public class LoanChargeDialogPresenter extends BasePresenter<LoanChargeDialogMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public LoanChargeDialogPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanChargeDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loanAllChargesV3(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllChargesV3(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("Failed to load Charges");
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllChargesV3(response);
                    }
                });
    }

    public void createLoanCharges(int loanId, ChargesPayload chargesPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createLoanCharges(loanId, chargesPayload)
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
                        getMvpView().showError("Failed to CreateLoanCharges");
                    }

                    @Override
                    public void onNext(Charges charges) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanChargesCreatedSuccessfully(charges);
                    }
                });
    }
}
