/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.runReport

import com.mifos.core.modelobjects.runreport.client.ClientReportTypeItem

sealed class RunReportUiState {

    data object Loading : RunReportUiState()

    data class Error(val message: Int) : RunReportUiState()

    data class RunReports(val runReports: List<ClientReportTypeItem>) : RunReportUiState()
}
