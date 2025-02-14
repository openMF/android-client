/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.databaseobjects.office

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Office(
    val id: Int?,
    val externalId: String?,
    val name: String?,
    val nameDecorated: String?,
    val officeOpeningDate: OfficeOpeningDate?,
    val openingDate: List<Int?> = emptyList(),
) : Parcelable
