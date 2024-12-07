/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

import com.mifos.core.objects.noncore.DataTable

data class DataTableListNavArgs(

    val dataTableList: List<DataTable>,

    val requestType: Int,

    val payload: Any?,

    val formWidget: MutableList<List<FormWidget>>,
)
