package com.mifos.mifosxdroid.online.clientidentifiers;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.noncore.Identifier;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class ClientIdentifiersPresenter extends BasePresenter<ClientIdentifiersMvpView> {

    private final DataManagerClient mDataManagerClient;
    private CompositeSubscription mSubscriptions;

    @Inject
    public ClientIdentifiersPresenter(DataManagerClient dataManagerClient) {
        mDataManagerClient = dataManagerClient;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(ClientIdentifiersMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }

    public void loadIdentifiers(int clientId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.getClientIdentifiers(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Identifier>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_identifier);
                    }

                    @Override
                    public void onNext(List<Identifier> identifiers) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientIdentifiers(identifiers);
                    }
                }));
    }

    /**
     * Method to call Identifier Delete endpoint to remove an identifier.
     * @param clientId ClientID whose identifier has to be removed
     * @param identifierId Id of the identifier to be removed
     * @param position Position of the identifier to be removed. This will be sent on successful
     *                 request to notify that identifier at this position has been removed.
     */
    public void deleteIdentifier(final int clientId, int identifierId, final int position) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.deleteClientIdentifier(clientId, identifierId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_delete_identifier);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().identifierDeletedSuccessfully(position);
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

}