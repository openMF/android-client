package com.mifos.mifosxdroid.online.createnewgroup;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.datamanager.DataManagerOffices;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.response.SaveResponse;
import com.mifos.utils.MFErrorParser;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class CreateNewGroupPresenter extends BasePresenter<CreateNewGroupMvpView> {

    private final DataManagerOffices mDataManagerOffices;
    private final DataManagerGroups mDataManagerGroups;
    private CompositeSubscription mSubscriptions;

    @Inject
    public CreateNewGroupPresenter(DataManagerOffices dataManagerOffices,
                                   DataManagerGroups dataManagerGroups) {
        mDataManagerOffices = dataManagerOffices;
        mDataManagerGroups = dataManagerGroups;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(CreateNewGroupMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerOffices.getOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Office>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch office list");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showOffices(offices);
                    }
                }));
    }

    public void createGroup(GroupPayload groupPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.createGroup(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SaveResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(SaveResponse saveResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupCreatedSuccessfully(saveResponse);
                    }
                }));
    }

}
