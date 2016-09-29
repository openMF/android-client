package com.mifos.mifosxdroid.dialogfragments.identifierdialog;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.noncore.IdentifierPayload;
import com.mifos.objects.noncore.IdentifierTemplate;
import com.mifos.objects.noncore.IdentifierType;
import com.mifos.utils.MFErrorParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 01/10/16.
 */

public class IdentifierDialogPresenter extends BasePresenter<IdentifierDialogMvpView> {

    private final DataManagerClient mDataManagerClient;
    private CompositeSubscription mSubscriptions;

    @Inject
    public IdentifierDialogPresenter(DataManagerClient dataManagerClient) {
        mDataManagerClient = dataManagerClient;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(IdentifierDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }

    public void loadClientIdentifierTemplate(int clientId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.getClientIdentifierTemplate(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<IdentifierTemplate>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_fetch_identifier_template);
                    }

                    @Override
                    public void onNext(IdentifierTemplate identifierTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientIdentifierTemplate(identifierTemplate);
                    }
                })
        );
    }

    public void createClientIdentifier(int clientId, IdentifierPayload identifierPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.createClientIdentifier(clientId, identifierPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        try {
                            if (e instanceof HttpException) {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                getMvpView().showMessage(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showIdentifierCreatedSuccessfully();
                    }
                })
        );
    }

    public List<String> getIdentifierDocumentTypeNames(List<IdentifierType> identifierTypes) {
        final ArrayList<String> documentType = new ArrayList<>();
        Observable.from(identifierTypes)
                .subscribe(new Action1<IdentifierType>() {
                    @Override
                    public void call(IdentifierType identifierType) {
                        documentType.add(identifierType.getName());
                    }
                });
        return documentType;
    }
}
