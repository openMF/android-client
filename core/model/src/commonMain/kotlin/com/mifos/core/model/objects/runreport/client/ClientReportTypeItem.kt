/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.runreport.client

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Tarun on 03-08-17.
 */
@Parcelize
@Serializable
data class ClientReportTypeItem(
    @SerialName("parameter_id")
    var parameterId: Int? = null,

    @SerialName("parameter_name")
    var parameterName: String? = null,

    @SerialName("report_category")
    var reportCategory: String? = null,

    @SerialName("report_id")
    var reportId: Int? = null,

    @SerialName("report_name")
    var reportName: String? = null,

    @SerialName("report_parameter_name")
    var reportParameterName: String? = null,

    @SerialName("report_subtype")
    var reportSubtype: String? = null,

    @SerialName("report_type")
    var reportType: String? = null,
) : Parcelable
