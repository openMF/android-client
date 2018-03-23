package com.mifos.mifosxdroid.online.clientdetails;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.zipmodels.ClientAndClientAccounts;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class ClientDetailsPresenter extends BasePresenter<ClientDetailsMvpView> {

    private final DataManagerDataTable  mDataManagerDataTable;
    private final DataManagerClient mDataManagerClient;
    private CompositeDisposable compositeDisposable;
    @Inject
    public ClientDetailsPresenter(DataManagerDataTable dataManagerDataTable,
                                  DataManagerClient dataManagerClient) {
        mDataManagerDataTable = dataManagerDataTable;
        mDataManagerClient = dataManagerClient;
    }

    @Override
    public void attachView(ClientDetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeDisposable != null) compositeDisposable.clear();
    }

    public void uploadImage(int id, File pngFile) {
        checkViewAttached();
        final String imagePath = pngFile.getAbsolutePath();

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/png"), pngFile);

        // MultipartBody.Part is used to send also the actual file name
        Part body = Part.createFormData("file", pngFile.getName(), requestFile);

        getMvpView().showUploadImageProgressbar(true);
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = mDataManagerClient.uploadClientImage(id, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showUploadImageProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showUploadImageFailed("Failed to update image");
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        getMvpView().showUploadImageProgressbar(false);
                        getMvpView().showUploadImageSuccessfully(response, imagePath);
                    }
                });
    }

    public void deleteClientImage(int clientId) {
        checkViewAttached();
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = mDataManagerClient.deleteClientImage(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to delete image");
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        getMvpView().showClientImageDeletedSuccessfully();
                    }
                });
    }

    public void loadClientDetailsAndClientAccounts(int clientId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = Observable.zip(
                mDataManagerClient.getClientAccounts(clientId),
                mDataManagerClient.getClient(clientId),
                new BiFunction<ClientAccounts, Client, Object>() {
                    @Override
                    public ClientAndClientAccounts apply(ClientAccounts clientAccounts, Client
                            client) {
                        ClientAndClientAccounts clientAndClientAccounts
                                = new ClientAndClientAccounts();
                        clientAndClientAccounts.setClient(client);
                        clientAndClientAccounts.setClientAccounts(clientAccounts);
                        return clientAndClientAccounts;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Object>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Client not found.");
                    }

                    @Override
                    public void onNext(ClientAndClientAccounts clientAndClientAccounts) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientAccount(clientAndClientAccounts.getClientAccounts());
                        getMvpView().showClientInformation(clientAndClientAccounts.getClient());
                    }
                });
    }

}
