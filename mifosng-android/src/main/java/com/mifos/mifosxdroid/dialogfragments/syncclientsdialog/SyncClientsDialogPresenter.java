package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.sync.SyncClientInformationStatus;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;

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
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogPresenter extends BasePresenter<SyncClientsDialogMvpView> {

    private final DataManagerClient mDataManagerClient;
    private final DataManagerLoan mDataManagerLoan;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SyncClientsDialogPresenter(DataManagerClient dataManagerClient,
                                      DataManagerLoan dataManagerLoan) {
        mDataManagerClient = dataManagerClient;
        mDataManagerLoan = dataManagerLoan;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncClientsDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void syncClientAccounts(int clientId) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerClient.syncClientAccounts(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAccounts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_client_and_accounts);
                    }

                    @Override
                    public void onNext(ClientAccounts clientAccounts) {
                        getMvpView().showClientAccountsSyncedSuccessfully(clientAccounts);
                    }
                })

        );
    }

    public void syncLoanById(int loanId) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerLoan.syncLoanById(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanWithAssociations>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_loan);
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        getMvpView().showLoanSyncSuccessfully();
                    }
                }));

    }

    public void syncLoanRepaymentTemplate(int loanId) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerLoan.syncLoanRepaymentTemplate(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanRepaymentTemplate>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_load_loanrepayment);
                    }

                    @Override
                    public void onNext(LoanRepaymentTemplate loanRepaymentTemplate) {
                        getMvpView().showLoanRepaymentSyncSuccessfully();
                    }
                }));
    }


    public void syncClient(Client client) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerClient.syncClientInDatabase(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_client);
                    }

                    @Override
                    public void onNext(Client client) {
                        getMvpView().showClientSyncSuccessfully();
                    }
                })
        );
    }


    public List<LoanAccount> getActiveLoanAccounts(List<LoanAccount> loanAccountList) {
        final List<LoanAccount> loanAccounts = new ArrayList<>();
        Observable.from(loanAccountList)
                .filter(new Func1<LoanAccount, Boolean>() {
                    @Override
                    public Boolean call(LoanAccount loanAccount) {
                        return loanAccount.getStatus().getActive();
                    }
                })
                .subscribe(new Action1<LoanAccount>() {
                    @Override
                    public void call(LoanAccount loanAccount) {
                        loanAccounts.add(loanAccount);
                    }
                });
        return loanAccounts;
    }

    public void syncLoanAndLoanRepayment(List<LoanAccount> loanAccounts) {
        Observable.from(loanAccounts)
                .flatMap(new Func1<LoanAccount, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(LoanAccount loanAccount) {
                        return Observable.just(loanAccount.getId());
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer loanId) {
                        syncLoanById(loanId);
                        syncLoanRepaymentTemplate(loanId);
                    }
                });
    }


    public Boolean isClientInformationSync(SyncClientInformationStatus
                                                   syncClientInformationStatus) {
        return syncClientInformationStatus.getSyncClientInformationStatus();
    }

}
