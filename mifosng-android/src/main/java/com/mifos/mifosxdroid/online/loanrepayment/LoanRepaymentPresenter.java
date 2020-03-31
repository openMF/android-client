package com.mifos.mifosxdroid.online.loanrepayment;

import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class LoanRepaymentPresenter extends BasePresenter<LoanRepaymentMvpView> {

    public final String LOG_TAG = getClass().getSimpleName();

    private final DataManagerLoan mDataManagerLoan;
    private CompositeSubscription mSubscriptions;

    @Inject
    public LoanRepaymentPresenter(DataManagerLoan dataManagerLoan) {
        mDataManagerLoan = dataManagerLoan;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(LoanRepaymentMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loanLoanRepaymentTemplate(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.getLoanRepayTemplate(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_loanrepayment);
                    }

                    @Override
                    public void onNext(LoanRepaymentTemplate loanRepaymentTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanRepayTemplate(loanRepaymentTemplate);
                    }
                }));
    }

    public void submitPayment(int loanId, LoanRepaymentRequest request) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.submitPayment(loanId, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.payment_failed);
                    }

                    @Override
                    public void onNext(LoanRepaymentResponse loanRepaymentResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showPaymentSubmittedSuccessfully(loanRepaymentResponse);
                    }
                }));
    }

    public void checkDatabaseLoanRepaymentByLoanId(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.getDatabaseLoanRepaymentByLoanId(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentRequest>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_loanrepayment);
                    }

                    @Override
                    public void onNext(LoanRepaymentRequest loanRepaymentRequest) {
                        getMvpView().showProgressbar(false);
                        if (loanRepaymentRequest != null) {
                            getMvpView().showLoanRepaymentExistInDatabase();
                        } else {
                            getMvpView().showLoanRepaymentDoesNotExistInDatabase();
                        }
                    }
                })
        );
    }
}
