package com.mifos.mifosxdroid.online.centerlist;

import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public class CenterListPresenter extends BasePresenter<CenterListMvpView> {

    private final DataManagerCenter mDataManagerCenter;
    private CompositeSubscription mSubscriptions;

    private List<Center> mDbCenterList;
    private List<Center> mSyncCenterList;

    private int limit = 100;
    private Boolean loadmore = false;
    private Boolean mRestApiCenterSyncStatus = false;
    private Boolean mDatabaseCenterSyncStatus = false;

    @Inject
    public CenterListPresenter(DataManagerCenter dataManagerCenter) {
        mDataManagerCenter = dataManagerCenter;
        mSubscriptions = new CompositeSubscription();
    }


    @Override
    public void attachView(CenterListMvpView mvpView) {
        super.attachView(mvpView);
    }


    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }


    /**
     * This Method for loading the first 100 centers.
     *
     * @param loadmore
     * @param offset
     */
    public void loadCenters(boolean loadmore, int offset) {
        this.loadmore = loadmore;
        loadCenters(true, offset, limit);
    }

    public void showCenters(List<Center> centers) {
        if (loadmore) {
            getMvpView().showMoreCenters(centers);
        } else {
            getMvpView().showCenters(centers);
        }
    }


    /**
     * Setting CenterSync Status True when mRestApiCenterSyncStatus && mDatabaseCenterSyncStatus
     * are true.
     */
    public void setAlreadyCenterSyncStatus() {
        if (mRestApiCenterSyncStatus && mDatabaseCenterSyncStatus) {
            showCenters(checkCenterAlreadySyncedOrNot(mSyncCenterList));
        }
    }


    /**
     * @param paged  True Enabling the Pagination of the API
     * @param offset Value given from which position Center List will be fetched.
     * @param limit  Number of Centers to fetch.
     */
    public void loadCenters(boolean paged, int offset, int limit) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerCenter.getCenters(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Center>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        if (loadmore) {
                            getMvpView().showMessage(R.string.failed_to_fetch_groups);
                        } else {
                            getMvpView().showFetchingError();
                        }
                    }

                    @Override
                    public void onNext(Page<Center> centerPage) {
                        mSyncCenterList = centerPage.getPageItems();

                        if (mSyncCenterList.size() == 0 && !loadmore) {
                            getMvpView().showEmptyCenters(R.string.empty_groups_list);
                        } else if (mSyncCenterList.size() == 0 && loadmore) {
                            getMvpView().showMessage(R.string.no_more_groups_available);
                        } else {
                            mRestApiCenterSyncStatus = true;
                            setAlreadyCenterSyncStatus();
                        }
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

    public void loadDatabaseCenters() {

    }


    public void loadCentersGroupAndMeeting(final int id) {
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerCenter.getCentersGroupAndMeeting(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError();
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCentersGroupAndMeeting(centerWithAssociations, id);
                    }
                }));
    }

    /**
     * This Method Filtering the Centers Loaded from Server, is already sync or not. If yes the
     * put the centers.setSync(true) and view will show to user that group already synced
     *
     * @param centers
     * @return List<Center>
     */
    public List<Center> checkCenterAlreadySyncedOrNot(List<Center> centers) {
        if (mDbCenterList.size() != 0) {

            for (Center dbCenter : mDbCenterList) {
                for (Center syncCenter : centers) {
                    if (dbCenter.getId().intValue() == syncCenter.getId().intValue()) {
                        syncCenter.setSync(true);
                        break;
                    }
                }
            }

        }
        return centers;
    }

}
