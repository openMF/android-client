package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class ReportCategoryRepositoryImp @Inject constructor(private val dataManager: DataManagerRunReport) :
    ReportCategoryRepository {

    override suspend fun getReportCategories(
        reportCategory: String,
        genericResultSet: Boolean,
        parameterType: Boolean
    ): List<ClientReportTypeItem> {
        return dataManager.getReportCategories(reportCategory, genericResultSet, parameterType)
    }
}