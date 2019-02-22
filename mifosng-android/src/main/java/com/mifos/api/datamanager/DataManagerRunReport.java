package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.objects.group.CenterInfo;
import com.mifos.objects.runreports.FullParameterListResponse;
import com.mifos.objects.runreports.client.ClientReportTypeItem;

import java.util.List;
import java.util.Map;

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

    public Observable<List<ClientReportTypeItem>> getReportCategories(String reportCategory,
                                                                      boolean genericResultSet,
                                                                      boolean parameterType) {
        return mBaseApiManager.getRunReportsService().getReportCategories(reportCategory,
                genericResultSet, parameterType);
    }

    public Observable<FullParameterListResponse> getReportFullParameterList(
            String reportName, boolean parameterType) {
        return mBaseApiManager.getRunReportsService()
                .getReportFullParameterList(reportName, parameterType);
    }

    public Observable<FullParameterListResponse> getReportParameterDetails(
            String parameterName, boolean parameterType) {
        return mBaseApiManager.getRunReportsService()
                .getReportParameterDetails(parameterName, parameterType);
    }

    public Observable<FullParameterListResponse> getRunReportWithQuery(
            String reportName, Map<String, String> options) {
        return mBaseApiManager.getRunReportsService()
                .getRunReportWithQuery(reportName, options);
    }

    public Observable<List<CenterInfo>> getCenterSummarInfo(int centerId,
                                                            boolean genericResultSet) {
        return mBaseApiManager.getRunReportsService()
                .getCenterSummaryInfo(centerId, genericResultSet);
    }

    public Observable<FullParameterListResponse> getRunReportOffices(
            String parameterName, int officeId, boolean parameterType) {
        return mBaseApiManager.getRunReportsService().
                getReportOffice(parameterName, officeId, parameterType);
    }

    public Observable<FullParameterListResponse> getRunReportProduct(
            String parameterName, String currency, boolean parameterType) {
        return mBaseApiManager.getRunReportsService().
                getReportProduct(parameterName, currency, parameterType);
    }
}
