package com.mifos.mifosxdroid.online.runreports.reportdetail;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.runreports.FullParameterListResponse;

/**
 * Created by Tarun on 04-08-17.
 */

public interface ReportDetailMvpView extends MvpView {

    void showError(String error);

    void showFullParameterResponse(FullParameterListResponse response);

    void showParameterDetails(FullParameterListResponse response, String identifier);

    void showRunReport(FullParameterListResponse response);

    void showOffices(FullParameterListResponse response, String identifier);

    void showProduct(FullParameterListResponse response, String identifier);
}
