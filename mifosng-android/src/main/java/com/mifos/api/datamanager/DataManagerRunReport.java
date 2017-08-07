package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperRunReport;
import com.mifos.objects.group.CenterInfo;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@Singleton
public class DataManagerRunReport {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperRunReport mDatabaseHelperRunReport;

    @Inject
    public DataManagerRunReport(BaseApiManager baseApiManager,
                                DatabaseHelperRunReport databaseHelperRunReport) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperRunReport = databaseHelperRunReport;
    }

    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the center information. The response will pass Presenter to show in the view
     * <p/>
     */
    public Observable<List<CenterInfo>> getCenterSummaryInfo(int centerId,
                                                            boolean genericResultSet) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getRunReportsService()
                        .getCenterSummaryInfo(centerId, genericResultSet);

            case 1:
                return mDatabaseHelperRunReport.readCenterSummaryInfo(centerId);

            default:
                return Observable.just((List<CenterInfo>) new ArrayList<CenterInfo>());
        }
    }

    /**
     * This Method Fetching the center information from REST API
     * and then Saving all center information into the Database and
     * then returns the list of center info
     * @param centerId Center Id
     * @return List of CenterInfo
     */
    public Observable<List<CenterInfo>> syncCenterSummaryInfo(final int centerId) {
        return mBaseApiManager.getRunReportsService().getCenterSummaryInfo(centerId, false)
                .concatMap(new Func1<List<CenterInfo>, Observable<? extends List<CenterInfo>>>() {
                    @Override
                    public Observable<? extends List<CenterInfo>> call(List<CenterInfo>
                                                                                     centerInfos) {
                        return mDatabaseHelperRunReport.saveCenterSummaryInfo(
                                centerInfos, centerId);
                    }
                });
    }
}
