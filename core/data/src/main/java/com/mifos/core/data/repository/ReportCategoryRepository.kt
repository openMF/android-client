package com.mifos.core.data.repository

import com.mifos.core.objects.runreports.client.ClientReportTypeItem

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface ReportCategoryRepository {

    suspend fun getReportCategories(
        reportCategory: String,
        genericResultSet: Boolean,
        parameterType: Boolean
    ): List<ClientReportTypeItem>

}