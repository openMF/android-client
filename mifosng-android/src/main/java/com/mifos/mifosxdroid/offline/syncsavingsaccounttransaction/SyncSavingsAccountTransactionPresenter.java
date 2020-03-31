package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction;

import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.utils.MFErrorParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 19/08/16.
 */
public class SyncSavingsAccountTransactionPresenter extends
        BasePresenter<SyncSavingsAccountTransactionMvpView> {

    public final String LOG_TAG = getClass().getSimpleName();

    public final DataManagerSavings mDataManagerSavings;
    public final DataManagerLoan mDataManagerLoan;
    private CompositeSubscription mSubscriptions;

    private List<SavingsAccountTransactionRequest> mSavingsAccountTransactionRequests;

    private int mTransactionIndex = 0;
    private int mTransactionsFailed = 0;

    @Inject
    public SyncSavingsAccountTransactionPresenter(DataManagerSavings dataManagerSavings,
                                                  DataManagerLoan dataManagerLoan) {
        mDataManagerSavings = dataManagerSavings;
        mDataManagerLoan = dataManagerLoan;
        mSubscriptions = new CompositeSubscription();
        mSavingsAccountTransactionRequests = new ArrayList<>();
    }

    @Override
    public void attachView(SyncSavingsAccountTransactionMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }


    public void syncSavingsAccountTransactions() {
        if (mSavingsAccountTransactionRequests.size() != 0) {
            mTransactionIndex = 0;
            checkErrorAndSync();
        } else {
            getMvpView().showError(R.string.nothing_to_sync);
        }
    }


    /**
     * This Method Check, SavingsAccountTransactionRequest Error Message is null or not, If
     * error message will not null. It means that SavingsAccountTransactionRequest already tried to
     * synced but there is some error to sync that Transaction.
     * and If error message  is null. It means SavingsAccountTransactionRequest never synced before,
     * start syncing that SavingsAccountTransactionRequest.
     */
    public void checkErrorAndSync() {
        for (int i = 0; i < mSavingsAccountTransactionRequests.size(); ++i) {
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
            } else if (checkTransactionsSyncBeforeOrNot()) {
                getMvpView().showError(R.string.error_fix_before_sync);
            }
        }
    }


    /**
     * This Method delete the SavingsAccountTransactionRequest from Database and load again
     * List<SavingsAccountTransactionRequest> and Update the UI.
     */
    public void showTransactionSyncSuccessfully() {
        deleteAndUpdateSavingsAccountTransaction(
                mSavingsAccountTransactionRequests.get(mTransactionIndex).getSavingAccountId());
    }

    /**
     * This Method will be called whenever Transaction sync failed. This Method set the Error
     * message to the SavingsAccountTransactionRequest and update
     * SavingsAccountTransactionRequest into the Database
     *
     * @param errorMessage Server Error Message
     */
    public void showTransactionSyncFailed(String errorMessage) {
        SavingsAccountTransactionRequest transaction = mSavingsAccountTransactionRequests
                .get(mTransactionIndex);
        transaction.setErrorMessage(errorMessage);
        updateSavingsAccountTransaction(transaction);
    }

    /**
     * This Method will be called when Transaction will be sync successfully and deleted from
     * Database then This Method Start Syncing next Transaction.
     *
     * @param transaction SavingsAccountTransactionRequest
     */
    public void showTransactionUpdatedSuccessfully(SavingsAccountTransactionRequest transaction) {
        mSavingsAccountTransactionRequests.set(mTransactionIndex, transaction);
        getMvpView().showSavingsAccountTransactions(mSavingsAccountTransactionRequests);

        mTransactionIndex = mTransactionIndex + 1;
        if (mSavingsAccountTransactionRequests.size() != mTransactionIndex) {
            syncSavingsAccountTransactions();
        }
    }

    /**
     * This Method Update the UI. This Method will be called when sync transaction will be
     * deleted from Database and  load again Transaction from Database
     * List<SavingsAccountTransactionRequest>.
     *
     * @param transactions List<SavingsAccountTransactionRequest>
     */
    public void showTransactionDeletedAndUpdated(List<SavingsAccountTransactionRequest>
                                                         transactions) {
        mTransactionIndex = 0;
        mSavingsAccountTransactionRequests = transactions;
        getMvpView().showSavingsAccountTransactions(transactions);
        if (mSavingsAccountTransactionRequests.size() != 0) {
            syncSavingsAccountTransactions();
        } else {
            getMvpView().showEmptySavingsAccountTransactions(R.string.nothing_to_sync);
        }

    }


    public Boolean checkTransactionsSyncBeforeOrNot() {
        Observable.from(mSavingsAccountTransactionRequests)
                .filter(new Func1<SavingsAccountTransactionRequest, Boolean>() {
                    @Override
                    public Boolean call(SavingsAccountTransactionRequest
                                                savingsAccountTransactionRequest) {
                        return savingsAccountTransactionRequest.getErrorMessage() != null;
                    }
                })
                .subscribe(new Action1<SavingsAccountTransactionRequest>() {
                    @Override
                    public void call(SavingsAccountTransactionRequest
                                             savingsAccountTransactionRequest) {
                        mTransactionsFailed = mTransactionsFailed + 1;
                    }
                });
        return mTransactionsFailed == mSavingsAccountTransactionRequests.size();
    }


    /**
     * This Method Load the List<SavingsAccountTransactionRequest> from
     * SavingsAccountTransactionRequest_Table and Update the UI
     */
    public void loadDatabaseSavingsAccountTransactions() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSavings.getAllSavingsAccountTransactions()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SavingsAccountTransactionRequest>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_savingaccounttransaction);
                    }

                    @Override
                    public void onNext(List<SavingsAccountTransactionRequest> transactionRequests) {
                        getMvpView().showProgressbar(false);
                        if (!transactionRequests.isEmpty()) {
                            getMvpView().showSavingsAccountTransactions(transactionRequests);
                            mSavingsAccountTransactionRequests = transactionRequests;
                        } else {
                            getMvpView().showEmptySavingsAccountTransactions(
                                    R.string.no_transaction_to_sync);
                        }
                    }
                })
        );
    }


    /**
     * THis Method Load the Payment Type Options from Database PaymentTypeOption_Table
     * and update the UI.
     */
    public void loadPaymentTypeOption() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.getPaymentTypeOption()
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
                        getMvpView().showPaymentTypeOptions(paymentTypeOptions);
                    }
                }));
    }


    /**
     * This Method is for Sync Offline Saved SavingsAccountTransaction to the Server.
     * This method will be called when user will be in online mode and user's Internet connection
     * will be working well. If the Transaction will failed to sync then
     * updateSavingsAccountTransaction(SavingsAccountTransactionRequest request) save the developer
     * error message to Database with the failed Transaction. otherwise
     * deleteAndUpdateSavingsAccountTransaction(int savingsAccountId) delete the sync
     * Transaction from Database and load again Transaction List from
     * SavingsAccountTransactionRequest_Table and then sync next.
     *
     * @param type            SavingsAccount type
     * @param accountId       SavingsAccount Id
     * @param transactionType Transaction type
     * @param request         SavingsAccountTransactionRequest
     */
    public void processTransaction(String type, int accountId, String transactionType,
                                   SavingsAccountTransactionRequest request) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
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
                        getMvpView().showProgressbar(false);
                        showTransactionSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionResponse
                                               savingsAccountTransactionResponse) {
                        getMvpView().showProgressbar(false);
                        showTransactionSyncSuccessfully();
                    }
                }));
    }


    /**
     * This Method delete the SavingsAccountTransactionRequest from the Database and load again
     * List<SavingsAccountTransactionRequest> from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>.
     *
     * @param savingsAccountId SavingsAccountTransactionRequest's SavingsAccount Id
     */
    public void deleteAndUpdateSavingsAccountTransaction(int savingsAccountId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SavingsAccountTransactionRequest>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_list);
                    }

                    @Override
                    public void onNext(List<SavingsAccountTransactionRequest>
                                               savingsAccountTransactionRequests) {
                        getMvpView().showProgressbar(false);
                        showTransactionDeletedAndUpdated(savingsAccountTransactionRequests);
                    }
                })
        );
    }


    /**
     * This Method Update the SavingsAccountTransactionRequest in the Database. This will be called
     * whenever any transaction will be failed to sync then the sync developer error message will
     * be added to SavingsAccountTransactionRequest to update in Database.
     *
     * @param request SavingsAccountTransactionRequest
     */
    public void updateSavingsAccountTransaction(SavingsAccountTransactionRequest request) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSavings.updateLoanRepaymentTransaction(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountTransactionRequest>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_savingsaccount);
                    }

                    @Override
                    public void onNext(SavingsAccountTransactionRequest
                                               savingsAccountTransactionRequest) {
                        getMvpView().showProgressbar(false);
                        showTransactionUpdatedSuccessfully(savingsAccountTransactionRequest);
                    }
                })
        );
    }
}
