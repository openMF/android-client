package com.mifos.mifosxdroid.online.clientidentifiers;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.noncore.Identifier;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class ClientIdentifiersPresenter extends BasePresenter<ClientIdentifiersMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public ClientIdentifiersPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientIdentifiersMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadIdentifiers(int clientid) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getIdentifiers(clientid)
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
                        getMvpView().showFetchingError("Error to Load Identifiers");
                    }

                    @Override
                    public void onNext(List<Identifier> identifiers) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientIdentifiers(identifiers);
                    }
                });
    }

    public void deleteIdentifier(final int clientId, int identifierId, final int position) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.deleteIdentifier(clientId, identifierId)
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
                        getMvpView().showFetchingError("Failed to delete Identifier");
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().identifierDeletedSuccessfully("Successfully deleted"
                                , position);
                        getMvpView().showProgressbar(false);
                    }
                });
    }

}