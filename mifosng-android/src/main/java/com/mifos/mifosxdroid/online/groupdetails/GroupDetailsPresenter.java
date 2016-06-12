package com.mifos.mifosxdroid.online.groupdetails;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.group.Group;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class GroupDetailsPresenter extends BasePresenter<GroupDetailsMvpView> {


    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public GroupDetailsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    @Override
    public void attachView(GroupDetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }


    public void loadGroup(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getGroup(groupId)
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
                });
    }

    public void loadGroupsOfClients(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllGroupsOfClient(groupId)
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
                });
    }

    public void loadClientDataTable() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientDataTable()
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
                });
    }

}
