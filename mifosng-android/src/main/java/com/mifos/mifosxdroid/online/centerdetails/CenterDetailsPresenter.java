package com.mifos.mifosxdroid.online.centerdetails;

import com.mifos.api.BaseApiManager;
import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ilya on 12/13/16.
 */

public class CenterDetailsPresenter extends BasePresenter<CenterDetailsMvpView> {

    private final DataManagerCenter mDataManagerCenter;

    @Inject
    public CenterDetailsPresenter(DataManagerCenter dataManagerCenter) {
        mDataManagerCenter = dataManagerCenter;
    }

    public void loadCentersGroupAndMeeting(final int centerId) {
        getMvpView().showProgressbar(true);

        mDataManagerCenter.getCentersGroupAndMeeting(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        getMvpView().showMeetingDetails(centerWithAssociations);
                        getMvpView().showCenterDetails(centerWithAssociations);
                    }
                });
    }

    public void loadSummaryInfo(int centerId) {
        BaseApiManager baseApiManager = new BaseApiManager();
        Observable<ArrayList<Map<String, Integer>>> response = baseApiManager
                .getRunreportsApi().getCenterSummaryInfo(centerId, false);

        response.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ArrayList<Map<String, Integer>>>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onNext(ArrayList<Map<String, Integer>> summaryInfo) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSummaryInfo(summaryInfo.get(0));
                    }
                });

    }
}
