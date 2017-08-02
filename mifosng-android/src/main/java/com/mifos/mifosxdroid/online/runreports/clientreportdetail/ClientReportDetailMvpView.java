package com.mifos.mifosxdroid.online.runreports.clientreportdetail;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.runreports.FullParameterListResponse;

/**
 * Created by Tarun on 04-08-17.
 */

public interface ClientReportDetailMvpView extends MvpView {

    void showError(String error);

    void showFullParameterResponse(FullParameterListResponse response);

    void showParameterDetails(FullParameterListResponse response, String identifier);

    void showRunReport(FullParameterListResponse response);
}
