/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.di

import com.mifos.feature.report.reportDetail.ReportDetailViewModel
import com.mifos.feature.report.runReport.RunReportViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ReportModule = module {
    viewModelOf(::ReportDetailViewModel)
    viewModelOf(::RunReportViewModel)
}
