package com.mifos.mifosxdroid.online.savingsaccountapproval;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.SavingsApproval;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 09/06/16.
 */
public class SavingsAccountApprovalPresenter extends BasePresenter<SavingsAccountApprovalMvpView> {

    private final DataManagerSavings dataManagerSavings;
    private Subscription mSubscription;

    @Inject
    public SavingsAccountApprovalPresenter(DataManagerSavings dataManager) {
        dataManagerSavings = dataManager;
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
        mSubscription = dataManagerSavings
                .approveSavingsApplication(savingsAccountId, savingsApproval)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingAccountApprovedSuccessfully(genericResponse);
                    }
                });
    }
}
