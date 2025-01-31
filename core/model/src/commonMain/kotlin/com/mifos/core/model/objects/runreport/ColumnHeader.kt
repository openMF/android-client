/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.runreport

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize

/**
 * Created by Tarun on 03-08-17.
 */
@Parcelize
data class ColumnHeader(
    var columnDisplayType: String,
    var columnName: String,
    var columnType: String,
    var columnValues: List<String>,
    var isColumnNullable: Boolean,
    var isColumnPrimaryKey: Boolean,
) : Parcelable
