package com.mifos.mifosxdroid.offline.offlinedashbarod;

import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.group.GroupPayload;
import com.mifos.services.data.CenterPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public class OfflineDashboardPresenter extends BasePresenter<OfflineDashboardMvpView> {


    private CompositeSubscription mSubscriptions;
    private final DataManagerClient mDataManagerClient;
    private final DataManagerGroups mDataManagerGroups;
    private final DataManagerCenter mDataManagerCenters;
    private final DataManagerLoan mDataManagerLoan;
    private final DataManagerSavings mDataManagerSavings;

    @Inject
    public OfflineDashboardPresenter(DataManagerClient dataManagerClient,
                                     DataManagerGroups dataManagerGroups,
                                     DataManagerCenter dataManagerCenter,
                                     DataManagerLoan dataManagerLoan,
                                     DataManagerSavings dataManagerSavings) {
        mSubscriptions = new CompositeSubscription();
        mDataManagerClient = dataManagerClient;
        mDataManagerGroups = dataManagerGroups;
        mDataManagerCenters = dataManagerCenter;
        mDataManagerLoan = dataManagerLoan;
        mDataManagerSavings = dataManagerSavings;
    }

    @Override
    public void attachView(OfflineDashboardMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadDatabaseClientPayload() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.getAllDatabaseClientPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ClientPayload>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_clientpayload);
                    }

                    @Override
                    public void onNext(List<ClientPayload> clientPayloads) {
                        getMvpView().showClients(clientPayloads);
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

    public void loadDatabaseGroupPayload() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getAllDatabaseGroupPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<GroupPayload>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_grouppayload);
                    }

                    @Override
                    public void onNext(List<GroupPayload> groupPayloads) {
                        getMvpView().showGroups(groupPayloads);
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

    public void loadDatabaseCenterPayload() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerCenters.getAllDatabaseCenterPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<CenterPayload>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_centerpayload);
                    }

                    @Override
                    public void onNext(List<CenterPayload> centerPayloads) {
                        getMvpView().showCenters(centerPayloads);
                        getMvpView().showProgressbar(false);
                    }
                }));
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
                    public void onNext(List<SavingsAccountTransactionRequest>
                                               transactionRequests) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingsAccountTransaction(transactionRequests);
                    }
                })
        );
    }
}
