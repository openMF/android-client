package com.mifos.mifosxdroid.online.loanrepaymentfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class LoanRepaymentPresenter implements Presenter<LoanRepaymentMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private LoanRepaymentMvpView mLoanRepaymentMvpView;

    public LoanRepaymentPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }


    @Override
    public void attachView(LoanRepaymentMvpView mvpView) {
        mLoanRepaymentMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mLoanRepaymentMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadLoanRepaymenyTemplate(int loanid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getloanRepaymentTemplate(loanid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoanRepaymentMvpView.ResponseError("Failed to Fetch LoanRepaymentTemplate");
                    }

                    @Override
                    public void onNext(LoanRepaymentTemplate loanRepaymentTemplate) {
                        mLoanRepaymentMvpView.showloanrepaymenttemplate(loanRepaymentTemplate);
                    }
                });
    }

    public void submitloanPayment(int loanid,LoanRepaymentRequest loanRepaymentRequest){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.submitPayment(loanid,loanRepaymentRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoanRepaymentMvpView.ResponseError("Failed to submit Payment");
                    }

                    @Override
                    public void onNext(LoanRepaymentResponse loanRepaymentResponse) {
                        mLoanRepaymentMvpView.showsubmitPaymentResponse(loanRepaymentResponse);
                    }
                });
    }

}
