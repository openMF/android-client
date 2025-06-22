/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.reportDetail

import com.mifos.core.model.objects.runreport.FullParameterListResponse

interface FileHelper {
    suspend fun exportCsv(report: FullParameterListResponse): Boolean
}

expect fun getFileHelper(reportName: String): FileHelper
