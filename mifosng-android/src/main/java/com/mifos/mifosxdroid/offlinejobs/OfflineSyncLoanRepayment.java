package com.mifos.mifosxdroid.offlinejobs;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.mifos.App;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.utils.MFErrorParser;
import com.mifos.utils.PrefManager;
import com.mifos.utils.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by aksh on 27/7/18.
 */

public class OfflineSyncLoanRepayment extends Job {

    @Inject
    DataManagerLoan mDataManagerLoan;

    private CompositeSubscription mSubscriptions;

    private List<LoanRepaymentRequest> mLoanRepaymentRequests;

    private int mClientSyncIndex = 0;

    public static void schedulePeriodic() {
        new JobRequest.Builder(Tags.OfflineSyncLoanRepayment)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15),
                        TimeUnit.MINUTES.toMillis(5))
                .build()
                .schedule();

    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        mSubscriptions = new CompositeSubscription();
        mLoanRepaymentRequests = new ArrayList<>();
        App.get(getContext()).getComponent().inject(this);
        if (PrefManager.getUserStatus() == 0) {
            loadDatabaseLoanRepaymentTransactions();
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }

    public void loadDatabaseLoanRepaymentTransactions() {
        mSubscriptions.add(mDataManagerLoan.getDatabaseLoanRepayments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<LoanRepaymentRequest>>() {
                    @Override
                    public void onCompleted() {
                        mClientSyncIndex = 0;
                        syncGroupPayload();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<LoanRepaymentRequest> loanRepaymentRequests) {
                        showLoanRepaymentTransactions(loanRepaymentRequests);
                    }
                }));
    }

    public void showLoanRepaymentTransactions(List<LoanRepaymentRequest> loanRepaymentRequests) {
        mLoanRepaymentRequests = loanRepaymentRequests;
    }

    public void syncGroupPayload() {
        for (int i = mClientSyncIndex; i < mLoanRepaymentRequests.size(); ++i) {
            if (mLoanRepaymentRequests.get(i).getErrorMessage() == null) {
                syncLoanRepayment(mLoanRepaymentRequests
                        .get(i)
                        .getLoanId(), mLoanRepaymentRequests.get(i));
                mClientSyncIndex = i;
                break;
            }
        }
    }

    public void syncLoanRepayment(int loanId, LoanRepaymentRequest loanRepaymentRequest) {
        mSubscriptions.add(mDataManagerLoan.submitPayment(loanId, loanRepaymentRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showPaymentFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(LoanRepaymentResponse loanRepaymentResponse) {
                        showPaymentSubmittedSuccessfully();
                    }
                }));
    }

    public void showPaymentFailed(String errorMessage) {
        LoanRepaymentRequest loanRepaymentRequest = mLoanRepaymentRequests.get(mClientSyncIndex);
        loanRepaymentRequest.setErrorMessage(errorMessage);
        updateLoanRepayment(loanRepaymentRequest);
    }

    public void updateLoanRepayment(LoanRepaymentRequest loanRepaymentRequest) {
        mSubscriptions.add(mDataManagerLoan.updateLoanRepaymentTransaction(loanRepaymentRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentRequest>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(LoanRepaymentRequest loanRepaymentRequest) {
                        showLoanRepaymentUpdated(loanRepaymentRequest);
                    }
                })

        );
    }

    public void showLoanRepaymentUpdated(LoanRepaymentRequest loanRepaymentRequest) {
        mLoanRepaymentRequests.set(mClientSyncIndex, loanRepaymentRequest);
        mClientSyncIndex = mClientSyncIndex + 1;
        if (mLoanRepaymentRequests.size() != mClientSyncIndex) {
            syncGroupPayload();
        }
    }

    public void showPaymentSubmittedSuccessfully() {
        deleteAndUpdateLoanRepayments(mLoanRepaymentRequests
                .get(mClientSyncIndex).getLoanId());
    }

    public void deleteAndUpdateLoanRepayments(int loanId) {
        mSubscriptions.add(mDataManagerLoan.deleteAndUpdateLoanRepayments(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<LoanRepaymentRequest>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<LoanRepaymentRequest> loanRepaymentRequests) {
                        showLoanRepaymentDeletedAndUpdateLoanRepayment(
                                loanRepaymentRequests);
                    }
                })
        );
    }

    public void showLoanRepaymentDeletedAndUpdateLoanRepayment(List<LoanRepaymentRequest>
                                                                       loanRepaymentRequests) {
        mClientSyncIndex = 0;
        this.mLoanRepaymentRequests = loanRepaymentRequests;
        if (mLoanRepaymentRequests.size() != 0) {
            syncGroupPayload();
        }
    }
}
