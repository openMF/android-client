package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.zipmodels.ClientAndClientAccounts;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
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

    public void syncClientDetailsAndClientAccounts(Client client) {
        checkViewAttached();
        mSubscriptions.add(Observable.zip(
                mDataManagerClient.syncClientAccounts(client.getId()),
                mDataManagerClient.syncClientInDatabase(client),
                new Func2<ClientAccounts, Client, ClientAndClientAccounts>() {
                    @Override
                    public ClientAndClientAccounts call(ClientAccounts clientAccounts, Client
                            client) {
                        ClientAndClientAccounts clientAndClientAccounts
                                = new ClientAndClientAccounts();
                        clientAndClientAccounts.setClient(client);
                        clientAndClientAccounts.setClientAccounts(clientAccounts);
                        return clientAndClientAccounts;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAndClientAccounts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_client_and_accounts);
                    }

                    @Override
                    public void onNext(ClientAndClientAccounts clientAndClientAccounts) {
                        getMvpView().showClientAndAccountsSyncedSuccessfully
                                (clientAndClientAccounts.getClient(),
                                        clientAndClientAccounts.getClientAccounts());
                    }
                }));
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
                        getMvpView().showLoanSyncSuccessfully(loanWithAssociations);
                    }
                }));

    }

    public void syncLoanRepaymentTemplate(int loanId) {
        checkViewAttached();
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
                        getMvpView().showError(R.string.failed_to_load_loanrepayment);
                    }

                    @Override
                    public void onNext(LoanRepaymentTemplate loanRepaymentTemplate) {
                        getMvpView().showLoanRepayentSyncSuccessfully(loanRepaymentTemplate);
                    }
                }));
    }

}
