package com.mifos.objects.runreports.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 03-08-17.
 */
@Parcelize
data class ClientReportTypeItem(
    val parameter_id: Int,
    val parameter_name: String,
    val report_category: String,
    val report_id: Int,
    val report_name: String,
    val report_parameter_name: String,
    val report_subtype: String,
    val report_type: String
) : Parcelable {

    override fun toString(): String {
        return "{paramId: $parameter_id" +
                "\nparamName: $parameter_name" +
                "\nreportCategory: $report_category" +
                "\nreportId: $report_id" +
                "\nreportName: $report_name" +
                "\nreportType: $report_type"
    }
}