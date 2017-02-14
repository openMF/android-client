package com.mifos.mifosxdroid.online.createnewcenter;

import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.response.SaveResponse;
import com.mifos.services.data.CenterPayload;
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
public class CreateNewCenterPresenter extends BasePresenter<CreateNewCenterMvpView> {

    private final DataManagerCenter dataManagerCenter;
    private CompositeSubscription subscriptions;

    @Inject
    public CreateNewCenterPresenter(DataManagerCenter dataManager) {
        dataManagerCenter = dataManager;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(CreateNewCenterMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscriptions.unsubscribe();
    }


    public void loadOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerCenter.getOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Office>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_offices);
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showOffices(offices);
                    }
                }));
    }

    public void createCenter(CenterPayload centerPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerCenter.createCenter(centerPayload)
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
                        getMvpView().centerCreatedSuccessfully(saveResponse);
                    }
                }));
    }


}
