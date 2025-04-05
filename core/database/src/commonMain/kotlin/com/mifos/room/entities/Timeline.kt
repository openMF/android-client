/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Timeline(
    val submittedOnDate: List<Int> = emptyList(),
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val activatedOnDate: List<Int> = emptyList(),
    val activatedByUsername: String? = null,
    val activatedByFirstname: String? = null,
    val activatedByLastname: String? = null,
    val closedOnDate: List<Int> = emptyList(),
    val closedByUsername: String? = null,
    val closedByFirstname: String? = null,
    val closedByLastname: String? = null,
) : Parcelable
