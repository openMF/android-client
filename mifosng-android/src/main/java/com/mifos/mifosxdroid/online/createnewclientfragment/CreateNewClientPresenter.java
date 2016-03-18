package com.mifos.mifosxdroid.online.createnewclientfragment;

import com.mifos.api.DataManager;
import com.mifos.api.model.ClientPayload;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.templates.clients.ClientsTemplate;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class CreateNewClientPresenter implements Presenter<CreateNewClientMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private CreateNewClientMvpView mCreateNewClientMvpView;

    public CreateNewClientPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }
    @Override
    public void attachView(CreateNewClientMvpView mvpView) {
        mCreateNewClientMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mCreateNewClientMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadofficelist(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Office>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewClientMvpView.ResponseFailed("Failed to fetch OfficeList");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        mCreateNewClientMvpView.showofficelist(offices);
                    }
                });
    }

    public void loadclientTemplate(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientTemplate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientsTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewClientMvpView.ResponseFailed("Failed to fetch ClientTemplate");
                    }

                    @Override
                    public void onNext(ClientsTemplate clientsTemplate) {
                        mCreateNewClientMvpView.showClientTemplate(clientsTemplate);
                    }
                });
    }

    public void createclient(ClientPayload clientPayload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createclient(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewClientMvpView.ResponseFailed("Error creating client");
                    }

                    @Override
                    public void onNext(Client client) {
                        mCreateNewClientMvpView.showCreatedClient(client);
                    }
                });
    }
}
