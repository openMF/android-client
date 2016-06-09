package com.mifos.mifosxdroid.dialogfragments.savingsaccountapproval;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.SavingsApproval;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 09/06/16.
 */
public class SavingsAccountApprovalPresenter extends BasePresenter<SavingsAccountApprovalMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public SavingsAccountApprovalPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SavingsAccountApprovalMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void approveSavingsApplication(int savingsAccountId, SavingsApproval savingsApproval) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.approveSavingsApplication(savingsAccountId, savingsApproval)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("Try Again");
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingAccountApprovedSuccessfully(genericResponse);
                    }
                });
    }
}
