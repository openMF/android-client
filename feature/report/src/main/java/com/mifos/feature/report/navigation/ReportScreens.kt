package com.mifos.feature.report.navigation

import com.mifos.core.common.utils.Constants

sealed class ReportScreens(val route: String) {

    data object RunReportScreen : ReportScreens(route = "run_report_screen")

    data object ReportDetailScreen :
        ReportScreens(route = "report_detail_screen/{${Constants.REPORT_TYPE_ITEM}}") {
        fun argument(reportTypeItem: String) = "report_detail_screen/${reportTypeItem}"
    }

    data object ReportScreen :
        ReportScreens(route = "report_screen/{${Constants.REPORT_PARAMETER_RESPONSE}}") {
        fun argument(reportParameter: String) = "report_screen/${reportParameter}"
    }
}