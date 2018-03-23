package com.mifos.mifosxdroid.online.grouplist;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.GroupWithAssociations;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class GroupListPresenter extends BasePresenter<GroupListMvpView> {

    private final DataManager mDataManager;
    private CompositeDisposable compositeDisposable;

    @Inject
    public GroupListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    @Override
    public void attachView(GroupListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeDisposable != null) compositeDisposable.clear();
    }

    public void loadGroupByCenter(int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = mDataManager.getGroupsByCenter(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<CenterWithAssociations>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load GroupList");
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupList(centerWithAssociations);
                    }
                });
    }

    public void loadGroups(int groupid) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = mDataManager.getGroups(groupid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GroupWithAssociations>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load Groups");
                    }

                    @Override
                    public void onNext(GroupWithAssociations groupWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroups(groupWithAssociations);
                    }
                });
    }


}
