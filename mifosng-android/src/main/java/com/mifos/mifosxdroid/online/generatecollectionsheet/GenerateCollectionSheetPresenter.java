package com.mifos.mifosxdroid.online.generatecollectionsheet;

import android.content.Context;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerCollectionSheet;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.mifosxdroid.injection.ActivityContext;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.collectionsheet.AttendanceTypeOption;
import com.mifos.objects.collectionsheet.CenterDetail;
import com.mifos.objects.collectionsheet.CollectionSheetPayload;
import com.mifos.objects.collectionsheet.CollectionSheetResponse;
import com.mifos.objects.collectionsheet.CollectionSheetRequestPayload;
import com.mifos.objects.collectionsheet.ProductiveCollectionSheetPayload;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.utils.MFErrorParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class GenerateCollectionSheetPresenter
        extends BasePresenter<GenerateCollectionSheetMvpView> {

    private final DataManager mDataManager;
    private CompositeDisposable compositeDisposable;
    private DataManagerCollectionSheet collectionDataManager;
    private Context c;

    @Inject
    public GenerateCollectionSheetPresenter(DataManager dataManager,
                                            DataManagerCollectionSheet sheetManager,
                                            @ActivityContext Context context) {
        mDataManager = dataManager;
        collectionDataManager = sheetManager;
        c = context;
        compositeDisposable = new CompositeDisposable();

    }

    @Override
    public void attachView(GenerateCollectionSheetMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManager.getOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Office>>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showOffices(offices);
                    }
                }));
    }

    public void loadStaffInOffice(final int officeId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManager.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Staff>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(List<Staff> staffs) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showStaffInOffice(staffs, officeId);
                    }
                }));
    }

    public void loadCentersInOffice(int id, Map<String, Object> params) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManager.getCentersInOffice(id, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Center>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(List<Center> centers) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCentersInOffice(centers);
                    }
                }));
    }

    public void loadGroupsInOffice(int office, Map<String, Object> params) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManager.getGroupsByOffice(office, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Group>>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(List<Group> groups) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupsInOffice(groups);
                    }
                }));
    }

    public void loadGroupByCenter(int centerId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(collectionDataManager.fetchGroupsAssociatedWithCenter(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<CenterWithAssociations>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupByCenter(centerWithAssociations);
                    }
                }));
    }

      /**
     * Method to hit the endpoint so as to fetch Center Details which are required to retrieve
     * Productive CollectionSheet for that center for some meeting date.
     */
    public void loadCenterDetails(String format, String locale, String meetingDate,
                                  int officeId, int staffId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(collectionDataManager.fetchCenterDetails(format, locale,
                meetingDate, officeId, staffId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<CenterDetail>>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(List<CenterDetail> centerDetails) {
                        getMvpView().showProgressbar(false);
                        getMvpView().onCenterLoadSuccess(centerDetails);
                    }
                }));
    }

    /**
     * Method to fetch Productive Collection Sheet
     */
    public void loadProductiveCollectionSheet(int centerId,
                                              CollectionSheetRequestPayload payload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(collectionDataManager.fetchProductiveCollectionSheet(centerId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<CollectionSheetResponse>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(CollectionSheetResponse collectionSheetResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showProductive(collectionSheetResponse);
                    }
                }));
    }

    public void loadCollectionSheet(int groupId,
                                              CollectionSheetRequestPayload payload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(collectionDataManager.fetchCollectionSheet(groupId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<CollectionSheetResponse>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(CollectionSheetResponse collectionSheetResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCollection(collectionSheetResponse);
                    }
                }));
    }

    /**
     * Method to submit the ProductiveCollectionSheet
     */
    public void submitProductiveSheet(int centerId, ProductiveCollectionSheetPayload payload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(collectionDataManager.submitProductiveSheet(centerId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(c.getResources().getString(
                                R.string.collectionsheet_submit_success));
                    }
                }));
    }

    /**
     * Method to submit the CollectionSheet
     */
    public void submitCollectionSheet(int groupId, CollectionSheetPayload payload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(collectionDataManager.submitCollectionSheet(groupId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(c.getResources().getString(
                                R.string.collectionsheet_submit_success));
                    }
                }));
    }

    /**
     * This method takes extracts the values of the office names and the corresponding ID
     * and returns a HashMap of the same.
     * It also fills the array officeNames with the names of the offices.
     * @param offices List of offices from which the values have to be extracted
     * @param officeNames List of Offices' names
     * @return HashMap of Office name string with the corresponding Id.
     */
    HashMap<String, Integer> createOfficeNameIdMap(List<Office> offices,
                                                   final List<String> officeNames) {
        final HashMap<String, Integer> officeMap = new HashMap<>();
        officeMap.put(c.getResources().getString(R.string.spinner_office), -1);
        officeNames.clear();
        officeNames.add(c.getResources().getString(R.string.spinner_office));
        Observable.fromIterable(offices)
                .subscribe(new Consumer<Office>() {
                    @Override
                    public void accept(Office office) {
                        officeMap.put(office.getName(), office.getId());
                        officeNames.add(office.getName());
                    }
                });
        return officeMap;
    }

    /**
     * This method takes a list of Staff and extracts Staff Name and Staff ID pair
     * in a HashMap.
     * It also fills the staffName array with the staff names
     * @param staffs List of Staff
     * @param staffNames List which is filled with the names of the staff
     * @return HashMap of Staff name string and the corresponding Id.
     */
    HashMap<String, Integer> createStaffIdMap(List<Staff> staffs, final List<String> staffNames) {
        final HashMap<String, Integer> staffMap = new HashMap<>();
        staffMap.put(c.getResources().getString(R.string.spinner_staff), -1);
        staffNames.clear();
        staffNames.add(c.getResources().getString(R.string.spinner_staff));
        Observable.fromIterable(staffs)
                .subscribe(new Consumer<Staff>() {
                    @Override
                    public void accept(Staff staff) {
                        staffMap.put(staff.getDisplayName(), staff.getId());
                        staffNames.add(staff.getDisplayName());
                    }
                });
        return staffMap;
    }

    /**
     * This method takes list of centers and extracts te Center Name and Center Id pair
     * in a HashMap.
     * It also fills the centerNames array wit the names of the center.
     * @param centers List of centers
     * @param centerNames List which is filled with the names of the centers.
     * @return HashMap of Center name string and the corresponding Id.
     */
    HashMap<String, Integer> createCenterIdMap(List<Center> centers,
                                               final List<String> centerNames) {
        final HashMap<String, Integer> centerMap = new HashMap<>();
        centerMap.put(c.getResources().getString(R.string.spinner_center), -1);
        centerNames.clear();
        centerNames.add(c.getResources().getString(R.string.spinner_center));
        Observable.fromIterable(centers)
                .subscribe(new Consumer<Center>() {
                    @Override
                    public void accept(Center center) {
                        centerMap.put(center.getName(), center.getId());
                        centerNames.add(center.getName());
                    }
                });
        return centerMap;
    }

    /**
     * This method takes list of centers and extracts te Center Name and Center Id pair
     * in a HashMap.
     * It also fills the centerNames array wit the names of the center.
     * @param groups List of groups.
     * @param groupNames List which is filled with the names of the groups.
     * @return HashMap of Group name string and the corresponding Id.
     */
    HashMap<String, Integer> createGroupIdMap(List<Group> groups, final List<String> groupNames) {
        final HashMap<String, Integer> groupMap = new HashMap<>();
        groupMap.put(c.getResources().getString(R.string.spinner_group), -1);
        groupNames.clear();
        groupNames.add(c.getResources().getString(R.string.spinner_group));
        Observable.fromIterable(groups)
                .subscribe(new Consumer<Group>() {
                    @Override
                    public void accept(Group group) {
                        groupMap.put(group.getName(), group.getId());
                        groupNames.add(group.getName());
                    }
                });
        return groupMap;
    }
    
    HashMap<String, Integer> filterAttendanceTypes (final List<AttendanceTypeOption> attendance,
                                                   final List<String> attendanceTypeNames) {
        final HashMap<String, Integer> options = new HashMap<>();
        Observable.fromIterable(attendance)
                .subscribe(new Consumer<AttendanceTypeOption>() {
                    @Override
                    public void accept(AttendanceTypeOption attendanceTypeOption) {
                        options.put(attendanceTypeOption.getValue(), attendanceTypeOption.getId());
                        attendanceTypeNames.add(attendanceTypeOption.getValue());
                    }
                });
        return options;
    }

    HashMap<String, Integer> filterPaymentTypes (List<PaymentTypeOption> paymentTypeOptions,
                                                 final List<String> paymentTypeNames) {
        final HashMap<String, Integer> paymentMap = new HashMap<>();
        paymentMap.put(c.getResources().getString(R.string.payment_type), -1);
        paymentTypeNames.clear();
        paymentTypeNames.add(c.getResources().getString(R.string.payment_type));
        Observable.fromIterable(paymentTypeOptions)
                .subscribe(new Consumer<PaymentTypeOption>() {
                    @Override
                    public void accept(PaymentTypeOption paymentTypeOption) {
                        paymentMap.put(paymentTypeOption.getName(), paymentTypeOption.getId());
                        paymentTypeNames.add(paymentTypeOption.getName());
                    }
                });
        return paymentMap;
    }

}
