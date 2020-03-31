package com.mifos.mifosxdroid.online.groupslist;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class GroupsListPresenter extends BasePresenter<GroupsListMvpView> {


    private final DataManagerGroups mDataManagerGroups;
    private CompositeSubscription mSubscriptions;

    private List<Group> mDbGroupList;
    private List<Group> mSyncGroupList;

    private int limit = 100;
    private Boolean loadmore = false;
    private Boolean mRestApiGroupSyncStatus = false;
    private Boolean mDatabaseGroupSyncStatus = false;

    @Inject
    public GroupsListPresenter(DataManagerGroups dataManagerGroups) {
        mDataManagerGroups = dataManagerGroups;
        mSubscriptions = new CompositeSubscription();
        mDbGroupList = new ArrayList<>();
        mSyncGroupList = new ArrayList<>();
    }

    @Override
    public void attachView(GroupsListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadGroups(Boolean loadmore, int offset) {
        this.loadmore = loadmore;
        loadGroups(true, offset, limit);
    }

    /**
     * Showing Groups List in View, If loadmore is true call showLoadMoreGroups(...) and else
     * call showGroupsList(...).
     */
    public void showClientList(List<Group> clients) {
        if (loadmore) {
            getMvpView().showLoadMoreGroups(clients);
        } else {
            getMvpView().showGroups(clients);
        }
    }

    /**
     * This Method will called, when Parent (Fragment or Activity) will be true.
     * If Parent Fragment is true then there is no need to fetch ClientList, Just show the Parent
     * (Fragment or Activity)'s Groups in View
     *
     * @param groups List<Group></>
     */
    public void showParentClients(List<Group> groups) {
        getMvpView().unregisterSwipeAndScrollListener();
        if (groups.size() == 0) {
            getMvpView().showEmptyGroups(R.string.group);
        } else {
            mRestApiGroupSyncStatus = true;
            mSyncGroupList = groups;
            setAlreadyClientSyncStatus();
        }
    }

    /**
     * Setting GroupSync Status True when mRestApiGroupSyncStatus && mDatabaseGroupSyncStatus
     * are true.
     */
    public void setAlreadyClientSyncStatus() {
        if (mRestApiGroupSyncStatus && mDatabaseGroupSyncStatus) {
            showClientList(checkGroupAlreadySyncedOrNot(mSyncGroupList));
        }
    }

    public void loadGroups(boolean paged, int offset, int limit) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getGroups(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Group>>() {
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
                    public void onNext(Page<Group> groupPage) {

                        mSyncGroupList = groupPage.getPageItems();

                        if (mSyncGroupList.size() == 0 && !loadmore) {
                            getMvpView().showEmptyGroups(R.string.group);
                            getMvpView().unregisterSwipeAndScrollListener();
                        } else if (mSyncGroupList.size() == 0 && loadmore) {
                            getMvpView().showMessage(R.string.no_more_groups_available);
                        } else {
                            mRestApiGroupSyncStatus = true;
                            setAlreadyClientSyncStatus();
                        }
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

    public void loadDatabaseGroups() {
        checkViewAttached();
        mSubscriptions.add(mDataManagerGroups.getDatabaseGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Group>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showMessage(R.string.failed_to_load_db_groups);
                    }

                    @Override
                    public void onNext(Page<Group> groupPage) {
                        mDatabaseGroupSyncStatus = true;
                        mDbGroupList = groupPage.getPageItems();
                        setAlreadyClientSyncStatus();
                    }
                })
        );
    }

    /**
     * This Method Filtering the Groups Loaded from Server, is already sync or not. If yes the
     * put the client.setSync(true) and view will show to user that group already synced
     *
     * @param groups
     * @return List<Client>
     */
    public List<Group> checkGroupAlreadySyncedOrNot(List<Group> groups) {
        if (mDbGroupList.size() != 0) {

            for (Group dbGroup : mDbGroupList) {
                for (Group syncGroup : groups) {
                    if (dbGroup.getId().intValue() == syncGroup.getId().intValue()) {
                        syncGroup.setSync(true);
                        break;
                    }
                }
            }

        }
        return groups;
    }
}
