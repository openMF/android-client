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

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize

/**
 * Created by Tarun on 03-08-17.
 */
@Parcelize
data class ClientReportTypeItem(
    var parameterId: Int? = null,

    var parameterName: String? = null,

    var reportCategory: String? = null,

    var reportId: Int? = null,

    var reportName: String? = null,

    var reportParameterName: String? = null,

    var reportSubtype: String? = null,

    var reportType: String? = null,
) : Parcelable
