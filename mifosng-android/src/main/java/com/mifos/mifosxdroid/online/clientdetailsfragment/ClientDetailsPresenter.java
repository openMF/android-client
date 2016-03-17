package com.mifos.mifosxdroid.online.clientdetailsfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.noncore.DataTable;
import java.io.File;
import java.util.List;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public class ClientDetailsPresenter implements Presenter<ClientDetailsMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private ClientDetailsMvpView mClientDetailsMvpView;

    public ClientDetailsPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientDetailsMvpView mvpView) {
        mClientDetailsMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientDetailsMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void deleteclientimage(int clientid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.deleteClientImage(clientid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientDetailsMvpView.showError("Failed to delete image");
                    }

                    @Override
                    public void onNext(Response response) {
                        mClientDetailsMvpView.showSuccessfullRequest("Image deleted");
                    }
                });
    }


    public void uploadclientimage(int clientid, final File file){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.uploadclientimage(clientid, new TypedFile("image/png", file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientDetailsMvpView.showfailedtouploadedimage();
                    }

                    @Override
                    public void onNext(Response response) {
                        mClientDetailsMvpView.showsuccessfullimageuploaded(file.getAbsolutePath());
                    }
                });
    }

    public void getclientdetails(int clientid){
        mClientDetailsMvpView.showClientDetailsProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getclientdetails(clientid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                        mClientDetailsMvpView.showError("Client not found.");
                    }

                    @Override
                    public void onNext(Client client) {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                        mClientDetailsMvpView.showclientdetails(client);
                    }
                });
    }

    public void getclientaccount(int clientid){
        mClientDetailsMvpView.showClientDetailsProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getclientaccount(clientid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAccounts>() {
                    @Override
                    public void onCompleted() {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                        mClientDetailsMvpView.showError("Accounts not found.");
                    }

                    @Override
                    public void onNext(ClientAccounts clientAccounts) {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                        mClientDetailsMvpView.showaccountdetails(clientAccounts);
                    }
                });

    }

    public void getClientDataTable(){
        mClientDetailsMvpView.showClientDetailsProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientDataTable("m_client")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DataTable>>() {
                    @Override
                    public void onCompleted() {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);
                        mClientDetailsMvpView.showError("Error Retrieving Client DataTable");
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        mClientDetailsMvpView.showClientDetailsProgressBar(false);

                    }
                });
    }

}
