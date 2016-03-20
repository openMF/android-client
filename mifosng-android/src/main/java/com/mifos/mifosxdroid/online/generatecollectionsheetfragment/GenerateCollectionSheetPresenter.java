package com.mifos.mifosxdroid.online.generatecollectionsheetfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import java.util.List;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class GenerateCollectionSheetPresenter implements Presenter<GenerateCollectionSheetMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private GenerateCollectionSheetMvpView mGenerateCollectionSheetMvpView;

    public GenerateCollectionSheetPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }
    @Override
    public void attachView(GenerateCollectionSheetMvpView mvpView) {
        mGenerateCollectionSheetMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mGenerateCollectionSheetMvpView = null;
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
                        mGenerateCollectionSheetMvpView.ResponseError("Failed to fetch OfficeList");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        mGenerateCollectionSheetMvpView.showofficeList(offices);
                    }
                });
    }

    public void loadStafflist(int officeid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getStaffForOffice(officeid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Staff>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mGenerateCollectionSheetMvpView.ResponseError("Failed to Fetch Staff List");
                    }

                    @Override
                    public void onNext(List<Staff> staffs) {
                        mGenerateCollectionSheetMvpView.showStaffList(staffs);
                    }
                });
    }

    public void loadCentersInOffice(int officeid,Map<String, Object> params){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getCentersInOffice(officeid, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Center>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mGenerateCollectionSheetMvpView.ResponseError("Failed to fetch CenterInoffice");
                    }

                    @Override
                    public void onNext(List<Center> centers) {
                        mGenerateCollectionSheetMvpView.showCenterOffice(centers);

                    }
                });

    }

    public void loadGroupByoffice(int officeid ,  Map<String, Object> params){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllGroupsInOffice(officeid, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Group>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mGenerateCollectionSheetMvpView.ResponseError("Failed TO fetch GroupByoffice");
                    }

                    @Override
                    public void onNext(List<Group> groups) {
                        mGenerateCollectionSheetMvpView.showGroupByOffice(groups);
                    }
                });

    }

    public void loadGroupByCenter(int centerid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllGroupsForCenter(centerid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mGenerateCollectionSheetMvpView.ResponseError("Failed to Fetch GroupByCenter");
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        mGenerateCollectionSheetMvpView.showGroupsByCenter(centerWithAssociations);
                    }
                });
    }
}
