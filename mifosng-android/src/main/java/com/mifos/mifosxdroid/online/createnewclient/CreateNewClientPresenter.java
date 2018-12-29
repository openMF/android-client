package com.mifos.mifosxdroid.online.createnewclient;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerOffices;
import com.mifos.api.datamanager.DataManagerStaff;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.objects.templates.clients.Options;
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
 * Created by Rajan Maurya on 6/6/16.
 */
public class CreateNewClientPresenter extends BasePresenter<CreateNewClientMvpView> {

    private final DataManagerClient mDataManagerClient;
    private final DataManagerOffices mDataManagerOffices;
    private final DataManagerStaff mDataManagerStaff;
    private CompositeSubscription mSubscriptions;

    @Inject
    public CreateNewClientPresenter(DataManagerClient dataManagerClient,
                                    DataManagerOffices dataManagerOffices,
                                    DataManagerStaff dataManagerStaff) {
        mDataManagerClient = dataManagerClient;
        mDataManagerOffices = dataManagerOffices;
        mDataManagerStaff = dataManagerStaff;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(CreateNewClientMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadClientTemplate() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.getClientTemplate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientsTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                        loadOffices();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.failed_to_fetch_client_template);
                    }

                    @Override
                    public void onNext(ClientsTemplate clientsTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientTemplate(clientsTemplate);
                    }
                }));
    }

    public void loadOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerOffices.getOffices()
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
                        getMvpView().showMessage(R.string.failed_to_fetch_offices);
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showOffices(offices);
                    }
                }));
    }


    public void loadStaffInOffices(int officeId) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerStaff.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Staff>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showMessage(R.string.failed_to_fetch_staffs);
                    }

                    @Override
                    public void onNext(List<Staff> staffs) {
                        getMvpView().showStaffInOffices(staffs);
                    }
                }));
    }

    public void createClient(ClientPayload clientPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.createClient(clientPayload)
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
                    public void onNext(Client client) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientCreatedSuccessfully(
                                R.string.client_created_successfully);
                    }
                }));
    }


    public List<String> filterOptions(List<Options> options) {
        final List<String> filterValues = new ArrayList<>();
        Observable.from(options)
                .subscribe(new Action1<Options>() {
                    @Override
                    public void call(Options options) {
                        filterValues.add(options.getName());
                    }
                });
        return filterValues;
    }

    public List<String> filterOffices(List<Office> offices) {
        final List<String> officesList = new ArrayList<>();
        Observable.from(offices)
                .subscribe(new Action1<Office>() {
                    @Override
                    public void call(Office office) {
                        officesList.add(office.getName());
                    }
                });
        return officesList;
    }

    public List<String> filterStaff(List<Staff> staffs) {
        final List<String> staffList = new ArrayList<>();
        Observable.from(staffs)
                .subscribe(new Action1<Staff>() {
                    @Override
                    public void call(Staff staff) {
                        staffList.add(staff.getDisplayName());
                    }
                });
        return staffList;
    }
}
