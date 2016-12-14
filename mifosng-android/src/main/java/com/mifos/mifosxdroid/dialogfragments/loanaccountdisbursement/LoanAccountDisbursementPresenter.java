package com.mifos.mifosxdroid.dialogfragments.loanaccountdisbursement;


import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.templates.loans.LoanDisburseTemplate;;
import com.mifos.objects.templates.loans.PaymentTypeOptions;

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
                .subscribe(new Subscriber<LoanDisburseTemplate>() {
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
                    public void onNext(LoanDisburseTemplate loanDisburseTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanTemplate(loanDisburseTemplate);
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
                .subscribe(new Subscriber<Loans>() {
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
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDispurseLoanSuccessfully(loans);
                    }
                });
    }

    public List<String> filterPaymentType(final List<PaymentTypeOptions>
                                              paymentTypeOptions) {
        final ArrayList<String> paymentTypeNameList = new ArrayList<>();
        Observable.from(paymentTypeOptions)
                .subscribe(new Action1<PaymentTypeOptions>() {
                    @Override
                    public void call(PaymentTypeOptions paymentTypeOptions) {
                        paymentTypeNameList.add(paymentTypeOptions.getName());
                    }
                });
        return paymentTypeNameList;
    }
}
