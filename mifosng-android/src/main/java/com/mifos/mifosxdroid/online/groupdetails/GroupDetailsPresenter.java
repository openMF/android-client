package com.mifos.mifosxdroid.online.groupdetails;

import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.group.Group;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class GroupDetailsPresenter extends BasePresenter<GroupDetailsMvpView> {

    private final DataManagerGroups mDataManagerGroups;
    private final DataManagerDataTable mDataManagerDataTable;
    private CompositeSubscription mSubscriptions;

    @Inject
    public GroupDetailsPresenter(DataManagerGroups dataManagerGroups,
                                 DataManagerDataTable dataManagerDataTable) {
        mDataManagerGroups = dataManagerGroups;
        mDataManagerDataTable = dataManagerDataTable;
        mSubscriptions = new CompositeSubscription();
    }


    @Override
    public void attachView(GroupDetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }


    public void loadGroup(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getGroup(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Group>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load Client");
                    }

                    @Override
                    public void onNext(Group group) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroup(group);
                    }
                }));
    }

    public void loadGroupsOfClients(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getGroupAccounts(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupAccounts>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Accounts not found.");
                    }

                    @Override
                    public void onNext(GroupAccounts groupAccounts) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupsOfClient(groupAccounts);
                    }
                }));
    }

    public void loadClientDataTable() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerDataTable.getDataTable(Constants.DATA_TABLE_NAME_GROUP)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DataTable>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load DataTable");
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientDataTable(dataTables);
                    }
                }));
    }

}
