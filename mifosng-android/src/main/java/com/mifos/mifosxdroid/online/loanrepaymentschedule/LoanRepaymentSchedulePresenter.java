package com.mifos.mifosxdroid.online.loanrepaymentschedule;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class LoanRepaymentSchedulePresenter extends BasePresenter<LoanRepaymentScheduleMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public LoanRepaymentSchedulePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanRepaymentScheduleMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadLoanRepaySchedule(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoanRepaySchedule(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanWithAssociations>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load LoanRepayment");
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanRepaySchedule(loanWithAssociations);
                    }
                });
    }


}
