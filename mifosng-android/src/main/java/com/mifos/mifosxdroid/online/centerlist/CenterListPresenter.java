package com.mifos.mifosxdroid.online.centerlist;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public class CenterListPresenter extends BasePresenter<CenterListMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;


    @Inject
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

    public void loadCenters(boolean paged, int offset, int limit) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getMoreCenters(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Center>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load Centers");
                    }

                    @Override
                    public void onNext(Page<Center> centerPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCenters(centerPage);
                    }
                });
    }


    public void loadCentersGroupAndMeeting(final int id) {
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getCentersGroupAndMeeting(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load Center Groups and Meeting");
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCentersGroupAndMeeting(
                                centerWithAssociations, id);
                    }
                });
    }

}
