package com.mifos.mifosxdroid.online.clientdetails;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.noncore.DataTable;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import retrofit.client.Response;
import retrofit.mime.TypedFile;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class ClientDetailsPresenter extends BasePresenter<ClientDetailsMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public ClientDetailsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientDetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
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
                        getMvpView().showFetchingError("Failed to load Client DataTable");
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientDataTable(dataTables);
                    }
                });
    }

    public void loadClientInformation(int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClient(id)
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
                        getMvpView().showFetchingError("Client not found.");
                    }

                    @Override
                    public void onNext(Client client) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientInformation(client);
                    }
                });
    }

    public void uploadImage(int id, File pngFile) {
        checkViewAttached();
        final String imagePath = pngFile.getAbsolutePath();
        getMvpView().showUploadImageProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.uploadClientImage(id,new TypedFile("image/png", pngFile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showUploadImageProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showUploadImageFailed("Failed to update image");
                    }

                    @Override
                    public void onNext(Response response) {
                        getMvpView().showUploadImageProgressbar(false);
                        getMvpView().showUploadImageSuccessfully(response,imagePath);
                    }
                });
    }

    public void deleteClientImage(int clientId) {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.deleteClientImage(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to delete image");
                    }

                    @Override
                    public void onNext(Response response) {
                        getMvpView().showClientImageDeletedSuccessfully();
                    }
                });
    }

    public void loadClientAccount(int clientId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription  = mDataManager.getClientAccounts(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAccounts>() {
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
                    public void onNext(ClientAccounts clientAccounts) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientAccount(clientAccounts);
                    }
                });
    }

}
