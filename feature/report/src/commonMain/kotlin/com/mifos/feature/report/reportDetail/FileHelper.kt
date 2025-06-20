package com.mifos.feature.report.reportDetail

import com.mifos.core.model.objects.runreport.FullParameterListResponse

interface FileHelper {
    suspend fun exportCsv(report: FullParameterListResponse): Boolean
}

expect fun getFileHelper(reportName: String): FileHelper