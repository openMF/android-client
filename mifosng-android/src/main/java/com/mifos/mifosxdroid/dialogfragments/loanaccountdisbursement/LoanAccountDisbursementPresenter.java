package com.mifos.mifosxdroid.dialogfragments.loanaccountdisbursement;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanDisbursement;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class LoanAccountDisbursementPresenter
        extends BasePresenter<LoanAccountDisbursementMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public LoanAccountDisbursementPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanAccountDisbursementMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadLoanTemplate(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoanTemplate(loanId)
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
                        getMvpView().showError("Failed to load LoanTemplate");
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanTemplate(response);
                    }
                });
    }

    public void dispurseLoan(int loanId, LoanDisbursement loanDisbursement) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.dispurseLoan(loanId, loanDisbursement)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("Try again");
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDispurseLoanSuccessfully(genericResponse);
                    }
                });
    }

}
