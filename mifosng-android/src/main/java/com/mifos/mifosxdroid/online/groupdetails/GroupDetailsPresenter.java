package com.mifos.mifosxdroid.online.groupdetails;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.zipmodels.GroupAndGroupAccounts;


import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class GroupDetailsPresenter extends BasePresenter<GroupDetailsMvpView> {

    private final DataManagerGroups mDataManagerGroups;
    private CompositeDisposable compositeDisposable;

    @Inject
    public GroupDetailsPresenter(DataManagerGroups dataManagerGroups) {
        mDataManagerGroups = dataManagerGroups;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(GroupDetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadGroupDetailsAndAccounts(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(Observable.combineLatest(
                mDataManagerGroups.getGroup(groupId),
                mDataManagerGroups.getGroupAccounts(groupId),
                new BiFunction<Group, GroupAccounts, Object>() {
                    @Override
                    public GroupAndGroupAccounts apply(Group group, GroupAccounts groupAccounts) {
                        return new GroupAndGroupAccounts(group, groupAccounts);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<GroupAndGroupAccounts>() {
                    @Override
                    public void onComplete() {
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
        compositeDisposable.add(mDataManagerGroups.getGroupWithAssociations(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GroupWithAssociations>() {
                    @Override
                    public void onComplete() {

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
