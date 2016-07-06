package com.mifos.mifosxdroid.online.createnewclient;

import com.mifos.api.DataManager;
import com.mifos.api.model.ClientPayload;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 6/6/16.
 */
public class CreateNewClientPresenter extends BasePresenter<CreateNewClientMvpView> {


    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public CreateNewClientPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CreateNewClientMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadClientTemplate() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription = mDataManager.getClientTemplate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientsTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch clientTemplate");
                    }

                    @Override
                    public void onNext(ClientsTemplate clientsTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientTemplate(clientsTemplate);
                    }
                });
    }

    public void loadOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription = mDataManager.getOffices()
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
                        getMvpView().showFetchingError("Failed to load offices list");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showOffices(offices);
                    }
                });
    }


    public void loadStaffInOffices(int officeId) {
        checkViewAttached();
        mSubscription = mDataManager.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Staff>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to load Staffs");
                    }

                    @Override
                    public void onNext(List<Staff> staffs) {
                        getMvpView().showStaffInOffices(staffs);
                    }
                });
    }

    public void createClient(ClientPayload clientPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createClient(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Try Again");
                    }

                    @Override
                    public void onNext(Client client) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientCreatedSuccessfully(client);
                    }
                });
    }


}
