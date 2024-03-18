package com.mifos.mifosxdroid.online.runreports.reportcategory

import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import rx.Observable

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface ReportCategoryRepository {

    fun getReportCategories(
        reportCategory: String?,
        genericResultSet: Boolean,
        parameterType: Boolean
    ): Observable<List<ClientReportTypeItem>>

}