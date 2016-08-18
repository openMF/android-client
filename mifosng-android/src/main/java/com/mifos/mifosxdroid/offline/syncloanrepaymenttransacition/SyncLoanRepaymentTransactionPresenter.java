package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition;

import com.google.gson.Gson;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.ErrorSyncServerMessage;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 28/07/16.
 */
public class SyncLoanRepaymentTransactionPresenter extends
        BasePresenter<SyncLoanRepaymentTransactionMvpView> {

    public final DataManagerLoan mDataManagerLoan;
    private CompositeSubscription mSubscriptions;


    @Inject
    public SyncLoanRepaymentTransactionPresenter(DataManagerLoan dataManagerLoan) {
        mDataManagerLoan = dataManagerLoan;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncLoanRepaymentTransactionMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadDatabaseLoanRepaymentTransactions() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.getDatabaseLoanRepayments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<LoanRepaymentRequest>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_loanrepayment);
                    }

                    @Override
                    public void onNext(List<LoanRepaymentRequest> loanRepaymentRequests) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanRepaymentTransactions(loanRepaymentRequests);
                    }
                }));
    }

    public void loanPaymentTypeOption() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan
                .getPaymentTypeOption()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<PaymentTypeOption>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_paymentoptions);
                    }

                    @Override
                    public void onNext(List<PaymentTypeOption> paymentTypeOptions) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showPaymentTypeOption(paymentTypeOptions);
                    }
                }));
    }

    public void syncLoanRepayment(int loanId, LoanRepaymentRequest loanRepaymentRequest) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.submitPayment(loanId, loanRepaymentRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            if (e instanceof HttpException) {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                Gson gson = new Gson();
                                ErrorSyncServerMessage syncErrorMessage = gson.
                                        fromJson(errorMessage, ErrorSyncServerMessage.class);
                                getMvpView().showProgressbar(false);
                                getMvpView().showPaymentFailed(syncErrorMessage);
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(throwable);
                        }
                    }

                    @Override
                    public void onNext(LoanRepaymentResponse loanRepaymentResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showPaymentSubmittedSuccessfully();
                    }
                }));
    }

    public void deleteAndUpdateLoanRepayments(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.deleteAndUpdateLoanRepayments(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<LoanRepaymentRequest>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_list);
                    }

                    @Override
                    public void onNext(List<LoanRepaymentRequest> loanRepaymentRequests) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanRepaymentDeletedAndUpdateLoanRepayment(
                                loanRepaymentRequests);
                    }
                })
        );
    }

    public void updateLoanRepayment(LoanRepaymentRequest loanRepaymentRequest) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.updateLoanRepaymentTransaction(loanRepaymentRequest)
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
                        getMvpView().showLoanRepaymentUpdated(loanRepaymentRequest);
                    }
                })

        );
    }
}
