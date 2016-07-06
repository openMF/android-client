package com.mifos.mifosxdroid.online.createnewclient;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerOffices;
import com.mifos.api.datamanager.DataManagerStaff;
import com.mifos.objects.client.ClientPayload;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.utils.MifosResponseHandler;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 6/6/16.
 */
//TODO Use CompositeSubscription in place Subscription for better handling Observable Subscription
public class CreateNewClientPresenter extends BasePresenter<CreateNewClientMvpView> {


    private final DataManagerClient mDataManagerClient;
    private final DataManagerOffices mDataManagerOffices;
    private final DataManagerStaff mDataManagerStaff;
    private Subscription mSubscription;

    @Inject
    public CreateNewClientPresenter(DataManagerClient dataManagerClient,
                                    DataManagerOffices dataManagerOffices,
                                    DataManagerStaff dataManagerStaff) {
        mDataManagerClient = dataManagerClient;
        mDataManagerOffices = dataManagerOffices;
        mDataManagerStaff = dataManagerStaff;
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
        mSubscription = mDataManagerClient.getClientTemplate()
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
        mSubscription = mDataManagerOffices.getOffices()
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
        mSubscription = mDataManagerStaff.getStaffInOffice(officeId)
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
        mSubscription = mDataManagerClient.createClient(clientPayload)
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
                        getMvpView().showClientCreatedSuccessfully(client, "Client" +
                                MifosResponseHandler.getResponse());
                    }
                });
    }


}
