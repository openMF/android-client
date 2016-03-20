package com.mifos.mifosxdroid.online.loanrepaymentschedulefragment;

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
public class LoanRepaymentSchedulePresenter implements Presenter<LoanRepaymentScheduleMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private LoanRepaymentScheduleMvpView mLoanRepaymentScheduleMvpView;

    public LoanRepaymentSchedulePresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanRepaymentScheduleMvpView mvpView) {
        mLoanRepaymentScheduleMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mLoanRepaymentScheduleMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadLoanRepaytSchedule(int loanid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoanRepaymentSchedule(loanid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoanRepaymentScheduleMvpView.ResponseError("Failed to load LoanRepaySchedule");
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        mLoanRepaymentScheduleMvpView.showLoanRepaySchedule(loanWithAssociations);
                    }
                });

    }
}
