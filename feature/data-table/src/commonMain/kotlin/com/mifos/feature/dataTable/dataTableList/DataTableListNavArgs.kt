/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

/**
 * Navigation arguments for `DataTableListScreen`.
 *
 * The legacy `formWidget` parameter (a pre-built `List<List<FormWidgetDTO>>`) has
 * been removed. The screen now derives form state directly from
 * `DataTableEntity.columnHeaderData`, mirroring the Mifos WebApp's approach.
 * This eliminates the `FormWidgetDTO` serialisation dependency which was never
 * ported to the KMP codebase.
 */
@Serializable
data class DataTableListNavArgs(

    val dataTableList: List<DataTableEntity>,

    val requestType: Int,

    @Polymorphic
    val payload: Any?,
)
