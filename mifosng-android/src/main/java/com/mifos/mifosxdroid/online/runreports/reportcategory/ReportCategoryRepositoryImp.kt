package com.mifos.mifosxdroid.online.runreports.reportcategory

import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class ReportCategoryRepositoryImp @Inject constructor(private val dataManager: DataManagerRunReport) :
    ReportCategoryRepository {

    override fun getReportCategories(
        reportCategory: String?,
        genericResultSet: Boolean,
        parameterType: Boolean
    ): Observable<List<ClientReportTypeItem>> {
        return dataManager.getReportCategories(reportCategory, genericResultSet, parameterType)
    }


}