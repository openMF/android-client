package com.mifos.mifosxdroid.online.savingsaccountsummaryfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.noncore.DataTable;
import java.util.List;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class SavingsAccountSummaryPresenter implements Presenter<SavingsAccountSummaryMvpView> {


    private final DataManager mDataManager;
    public Subscription mSubscription;
    private SavingsAccountSummaryMvpView mSavingsAccountSummaryMvpView;

    public SavingsAccountSummaryPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SavingsAccountSummaryMvpView mvpView) {
        mSavingsAccountSummaryMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mSavingsAccountSummaryMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadSavingAccount(String savingaccounttype, int savingaccountid,String association){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getSavingAccount(savingaccounttype,savingaccountid,association)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSavingsAccountSummaryMvpView.ResponseFailedSavingAccount("Internal Server Error");
                    }

                    @Override
                    public void onNext(SavingsAccountWithAssociations savingsAccountWithAssociations) {
                        mSavingsAccountSummaryMvpView.showSavingAccount(savingsAccountWithAssociations);
                    }
                });
    }

    public void loadSavingsDataTable(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getSavingsDataTable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DataTable>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSavingsAccountSummaryMvpView.ResponseErrorLoading("Failed to Load DataTable");
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        mSavingsAccountSummaryMvpView.showSavingDataTable(dataTables);
                    }
                });
    }
}
