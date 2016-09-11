package com.mifos.mifosxdroid.online.groupdetails;

import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.group.Group;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.zipmodels.GroupAndGroupAccounts;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
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
        mSubscriptions.clear();
    }

    public void loadGroupDetailsAndAccounts(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(Observable.combineLatest(
                mDataManagerGroups.getGroup(groupId),
                mDataManagerGroups.getGroupAccounts(groupId),
                new Func2<Group, GroupAccounts, GroupAndGroupAccounts>() {
                    @Override
                    public GroupAndGroupAccounts call(Group group, GroupAccounts groupAccounts) {
                        return new GroupAndGroupAccounts(group, groupAccounts);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupAndGroupAccounts>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_group_and_account);
                    }

                    @Override
                    public void onNext(GroupAndGroupAccounts groupAndGroupAccounts) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroup(groupAndGroupAccounts.getGroup());
                        getMvpView().showGroupAccounts(groupAndGroupAccounts.getGroupAccounts());
                    }
                })
        );
    }

    public void loadClientDataTable() {
        checkViewAttached();
        mSubscriptions.add(mDataManagerDataTable.getDataTable(Constants.DATA_TABLE_NAME_GROUP)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DataTable>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError(R.string.failed_to_fetch_datatable);
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        getMvpView().showGroupDataTable(dataTables);
                    }
                }));
    }

}
