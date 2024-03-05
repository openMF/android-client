package com.mifos.core.objects.runreports.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 03-08-17.
 */
@Parcelize
data class ClientReportTypeItem(
    var parameter_id: Int? = null,

    var parameter_name: String? = null,

    var report_category: String? = null,

    var report_id: Int? = null,

    var report_name: String? = null,

    var report_parameter_name: String? = null,

    var report_subtype: String? = null,

    var report_type: String? = null
) : Parcelable