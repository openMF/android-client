/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.ReportDetailRepository
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.objects.runreports.FullParameterListResponse
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class ReportDetailRepositoryImp @Inject constructor(private val dataManager: DataManagerRunReport) :
    ReportDetailRepository {

    override suspend fun getReportFullParameterList(
        reportName: String,
        parameterType: Boolean,
    ): FullParameterListResponse {
        return dataManager.getReportFullParameterList(reportName, parameterType)
    }

    override suspend fun getReportParameterDetails(
        parameterName: String,
        parameterType: Boolean,
    ): FullParameterListResponse {
        return dataManager.getReportParameterDetails(parameterName, parameterType)
    }

    override suspend fun getRunReportOffices(
        parameterName: String,
        officeId: Int,
        parameterType: Boolean,
    ): FullParameterListResponse {
        return dataManager.getRunReportOffices(parameterName, officeId, parameterType)
    }

    override suspend fun getRunReportProduct(
        parameterName: String,
        currency: String,
        parameterType: Boolean,
    ): FullParameterListResponse {
        return dataManager.getRunReportProduct(parameterName, currency, parameterType)
    }

    override suspend fun getRunReportWithQuery(
        reportName: String,
        options: Map<String, String>,
    ): FullParameterListResponse {
        return dataManager.getRunReportWithQuery(reportName, options)
    }
}
