package com.mifos.mifosxdroid.online.runreports.reportcategory;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.runreports.client.ClientReportTypeItem;

import java.util.List;

/**
 * Created by Tarun on 03-08-17.
 */

public interface ReportCategoryMvpView extends MvpView {

    void showError(String error);

    void showReportCategories(List<ClientReportTypeItem> reportTypes);
}
