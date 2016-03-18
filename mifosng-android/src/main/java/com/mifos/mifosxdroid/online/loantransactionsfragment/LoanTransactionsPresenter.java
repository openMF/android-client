package com.mifos.mifosxdroid.online.loantransactionsfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class LoanTransactionsPresenter  implements Presenter<LoanTransactionsMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private LoanTransactionsMvpView mLoanTransactionsMvpView;

    public LoanTransactionsPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanTransactionsMvpView mvpView) {
        mLoanTransactionsMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mLoanTransactionsMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loanLoanTransactions(int loanid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoanTransactions(loanid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoanTransactionsMvpView.ResponseError("Failed to Load LoanTransactions");
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        mLoanTransactionsMvpView.showLoanTransactions(loanWithAssociations);
                    }
                });
    }
}
