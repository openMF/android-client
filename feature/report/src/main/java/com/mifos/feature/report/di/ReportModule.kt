package com.mifos.feature.report.di

import org.koin.dsl.module
import com.mifos.feature.report.report.ReportViewModel
import com.mifos.feature.report.reportDetail.ReportDetailViewModel
import com.mifos.feature.report.runReport.RunReportViewModel
import org.koin.core.module.dsl.viewModelOf

val ReportModule = module {
    viewModelOf(::ReportViewModel)
    viewModelOf(::ReportDetailViewModel)
    viewModelOf(::RunReportViewModel)
}