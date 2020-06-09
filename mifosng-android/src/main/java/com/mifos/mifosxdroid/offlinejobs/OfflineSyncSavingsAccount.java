package com.mifos.mifosxdroid.offlinejobs;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.mifos.App;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
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

public class OfflineSyncSavingsAccount extends Job {
    @Inject
    DataManagerSavings mDataManagerSavings;

    @Inject
    DataManagerLoan mDataManagerLoan;
    private CompositeSubscription mSubscriptions;

    private List<SavingsAccountTransactionRequest> mSavingsAccountTransactionRequests;

    private int mTransactionIndex = 0;

    public static void schedulePeriodic() {
        new JobRequest.Builder(Tags.OfflineSyncSavingsAccount)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15),
                        TimeUnit.MINUTES.toMillis(5))
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        mSubscriptions = new CompositeSubscription();
        mSavingsAccountTransactionRequests = new ArrayList<>();
        App.get(getContext()).getComponent().inject(this);
        if (PrefManager.getUserStatus() == 0) {
            loadDatabaseSavingsAccountTransactions();
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }

    public void loadDatabaseSavingsAccountTransactions() {
        mSubscriptions.add(mDataManagerSavings.getAllSavingsAccountTransactions()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SavingsAccountTransactionRequest>>() {
                    @Override
                    public void onCompleted() {
                        syncNow();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SavingsAccountTransactionRequest> transactionRequests) {
                        if (!transactionRequests.isEmpty()) {
                            mSavingsAccountTransactionRequests = transactionRequests;
                        }
                    }
                })
        );
    }

    public void syncNow() {
        if (mSavingsAccountTransactionRequests.size() != 0) {
            mTransactionIndex = 0;
            checkErrorAndSync();
        }
    }

    public void checkErrorAndSync() {
        for (int i = mTransactionIndex; i < mSavingsAccountTransactionRequests.size(); ++i) {
            if (mSavingsAccountTransactionRequests.get(i).getErrorMessage() == null) {

                mTransactionIndex = i;

                String savingAccountType =
                        mSavingsAccountTransactionRequests.get(i).getSavingsAccountType();
                int savingAccountId =
                        mSavingsAccountTransactionRequests.get(i).getSavingAccountId();
                String transactionType = mSavingsAccountTransactionRequests
                        .get(i).getTransactionType();
                processTransaction(savingAccountType, savingAccountId, transactionType,
                        mSavingsAccountTransactionRequests.get(i));

                break;
            }
        }
    }

    public void processTransaction(String type, int accountId, String transactionType,
                                   SavingsAccountTransactionRequest request) {
        mSubscriptions.add(mDataManagerSavings
                .processTransaction(type, accountId, transactionType, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showTransactionSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionResponse
                                               savingsAccountTransactionResponse) {
                        showTransactionSyncSuccessfully();
                    }
                }));
    }

    public void showTransactionSyncFailed(String errorMessage) {
        SavingsAccountTransactionRequest transaction = mSavingsAccountTransactionRequests
                .get(mTransactionIndex);
        transaction.setErrorMessage(errorMessage);
        updateSavingsAccountTransaction(transaction);
    }

    public void updateSavingsAccountTransaction(SavingsAccountTransactionRequest request) {
        mSubscriptions.add(mDataManagerSavings.updateLoanRepaymentTransaction(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionRequest>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SavingsAccountTransactionRequest
                                               savingsAccountTransactionRequest) {
                        showTransactionUpdatedSuccessfully(savingsAccountTransactionRequest);
                    }
                })
        );
    }

    public void showTransactionUpdatedSuccessfully(SavingsAccountTransactionRequest transaction) {
        mSavingsAccountTransactionRequests.set(mTransactionIndex, transaction);
        mTransactionIndex = mTransactionIndex + 1;
        if (mSavingsAccountTransactionRequests.size() != mTransactionIndex) {
            syncSavingsAccountTransactions();
        }
    }

    public void showTransactionSyncSuccessfully() {
        deleteAndUpdateSavingsAccountTransaction(
                mSavingsAccountTransactionRequests.get(mTransactionIndex).getSavingAccountId());
    }

    public void deleteAndUpdateSavingsAccountTransaction(int savingsAccountId) {
        mSubscriptions.add(mDataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SavingsAccountTransactionRequest>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SavingsAccountTransactionRequest>
                                               savingsAccountTransactionRequests) {
                        showTransactionDeletedAndUpdated(savingsAccountTransactionRequests);
                    }
                })
        );
    }

    public void showTransactionDeletedAndUpdated(List<SavingsAccountTransactionRequest>
                                                         transactions) {
        mTransactionIndex = 0;
        mSavingsAccountTransactionRequests = transactions;
        if (mSavingsAccountTransactionRequests.size() != 0) {
            syncSavingsAccountTransactions();
        }
    }

    public void syncSavingsAccountTransactions() {
        if (mSavingsAccountTransactionRequests.size() != 0) {
            mTransactionIndex = 0;
            checkErrorAndSync();
        }
    }
}
