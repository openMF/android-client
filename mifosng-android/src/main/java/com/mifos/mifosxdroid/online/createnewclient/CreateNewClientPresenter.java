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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

                        if (client.getClientId() != null) {
                            getMvpView().showClientCreatedSuccessfully(
                                    R.string.client_created_successfully);
                            getMvpView().setClientId(client.getClientId());
                        } else {
                            getMvpView().showWaitingForCheckerApproval(
                                    R.string.waiting_for_checker_approval
                            );
                        }
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

    public void uploadImage(int id, File pngFile) {
        checkViewAttached();
        getMvpView().showProgress("Uploading Client's Picture...");
        final String imagePath = pngFile.getAbsolutePath();

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/png"), pngFile);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", pngFile.getName(), requestFile);

        mSubscriptions.add(mDataManagerClient.uploadClientImage(id, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().hideProgress();
                        getMvpView().showMessage(R.string.Image_Upload_Failed);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        getMvpView().hideProgress();
                        getMvpView().showMessage(R.string.Image_Upload_Successful);
                    }
                }));
    }
}
