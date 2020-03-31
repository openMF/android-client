package com.mifos.mifosxdroid.online.groupdetails;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.zipmodels.GroupAndGroupAccounts;

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
    private CompositeSubscription mSubscriptions;

    @Inject
    public GroupDetailsPresenter(DataManagerGroups dataManagerGroups) {
        mDataManagerGroups = dataManagerGroups;
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

    public void loadGroupAssociateClients(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getGroupWithAssociations(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_load_client);
                    }

                    @Override
                    public void onNext(GroupWithAssociations groupWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupClients(groupWithAssociations.getClientMembers());
                    }
                })
        );
    }

}
