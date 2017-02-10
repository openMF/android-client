package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.objects.group.CenterInfo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@Singleton
public class DataManagerRunReport {

    public final BaseApiManager mBaseApiManager;

    @Inject
    public DataManagerRunReport(BaseApiManager baseApiManager) {
        mBaseApiManager = baseApiManager;
    }

    public Observable<List<CenterInfo>> getCenterSummarInfo(int centerId,
                                                            boolean genericResultSet) {
        return mBaseApiManager.getRunReportsService()
                .getCenterSummaryInfo(centerId, genericResultSet);
    }
}
