package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperStaff;
import com.mifos.api.mappers.staff.StaffMapper;
import com.mifos.objects.organisation.Staff;
import com.mifos.utils.PrefManager;

import org.apache.fineract.client.services.StaffApi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
public class DataManagerStaff {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperStaff mDatabaseHelperStaff;
    public final org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager;

    @Inject
    public DataManagerStaff(BaseApiManager baseApiManager,
                            DatabaseHelperStaff databaseHelperStaff,
                            org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperStaff = databaseHelperStaff;
        this.sdkBaseApiManager = sdkBaseApiManager;
    }

    private StaffApi getStaffApi() {
        return sdkBaseApiManager.getStaffApi();
    }

    /**
     * @param officeId
     * @return
     */
    public Observable<List<Staff>> getStaffInOffice(int officeId) {
        switch (PrefManager.INSTANCE.getUserStatus()) {
            case 0:
                getStaffApi().retrieveAll16((long) officeId, null,
                        null, "all")
                        .map(StaffMapper.INSTANCE::mapFromEntityList)
                        .concatMap(staffs -> {
                                mDatabaseHelperStaff.saveAllStaffOfOffices(staffs);
                                return Observable.just(staffs);
                            });
            case 1:
                /**
                 * return all List of Staffs of Office from DatabaseHelperOffices
                 */
                return mDatabaseHelperStaff.readAllStaffOffices(officeId);

            default:
                List<Staff> staffs = new ArrayList<>();
                return Observable.just(staffs);
        }

    }

}
