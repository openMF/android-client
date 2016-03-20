/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlistfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import java.util.List;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public class CenterListPresenter implements Presenter<CenterListMvpView> {

    private final DataManager mDatamanager;
    private Subscription mSubscription;
    private CenterListMvpView mCenterListMvp;

    public CenterListPresenter(){
        mDatamanager = new DataManager();
    }

    @Override
    public void attachView(CenterListMvpView mvpView) {
        this.mCenterListMvp = mvpView;
    }

    @Override
    public void detachView() {
        mCenterListMvp = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadCenters(){
        mCenterListMvp.showCenterProgress(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDatamanager.getCenters()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Center>>() {
                    @Override
                    public void onCompleted() {
                        mCenterListMvp.showCenterProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCenterListMvp.showCenterProgress(false);
                    }

                    @Override
                    public void onNext(List<Center> centers) {
                        mCenterListMvp.showCenters(centers);
                    }
                });

    }

    public void loadCentersGroupAndMeeting(int centerId){
        mCenterListMvp.showCenterProgress(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDatamanager.getCentersGroupAndMeeting(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {
                        mCenterListMvp.showCenterProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCenterListMvp.showCenterProgress(false);
                        mCenterListMvp.showResponseError();
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        mCenterListMvp.showCentersGroupAndMeeting(centerWithAssociations);
                    }
                });

    }
}
