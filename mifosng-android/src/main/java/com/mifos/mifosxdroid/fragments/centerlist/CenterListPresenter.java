package com.mifos.mifosxdroid.fragments.centerlist;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.db.OfflineCenter;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 09/06/16.
 */
public class CenterListPresenter extends BasePresenter<CenterListMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    public CenterListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CenterListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadCenterList(
            String dateFormat, String locale, String meetingDate, int officeId, int staffI) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getCenterList(
                dateFormat, locale, meetingDate, officeId, staffI)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<OfflineCenter>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError("Please login to continue");
                    }

                    @Override
                    public void onNext(List<OfflineCenter> offlineCenters) {
                        getMvpView().showCenterList(offlineCenters);
                    }
                });

    }
}
