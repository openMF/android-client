package com.mifos.mifosxdroid.online.centerlist;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public class CenterListPresenter implements Presenter<CenterListMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;
    private CenterListMvpView mCenterListMvpView;

    @Inject
    public CenterListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    @Override
    public void attachView(CenterListMvpView mvpView) {
        mCenterListMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mCenterListMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadCenters() {
        mCenterListMvpView.showProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription  = mDataManager.getCenters()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Center>>() {
                    @Override
                    public void onCompleted() {
                        mCenterListMvpView.showProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCenterListMvpView.showProgressBar(false);
                        mCenterListMvpView.showCenterGroupFetchinError();
                    }

                    @Override
                    public void onNext(List<Center> centers) {
                        mCenterListMvpView.showProgressBar(false);
                        mCenterListMvpView.showCenters(centers);
                    }
                });

    }

    public void loadCentersGroupAndMeeting(final int id) {
        mCenterListMvpView.showProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getCentersGroupAndMeeting(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {
                        mCenterListMvpView.showProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCenterListMvpView.showProgressBar(false);
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        mCenterListMvpView.showProgressBar(false);
                        mCenterListMvpView.showCentersGroupAndMeeting(
                                centerWithAssociations, id);
                    }
                });
    }

}
