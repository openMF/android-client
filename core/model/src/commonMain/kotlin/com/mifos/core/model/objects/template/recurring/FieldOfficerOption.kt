/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.template.recurring

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FieldOfficerOption(
    val displayName: String? = null,
    val externalId: String? = null,
    val firstname: String? = null,
    val id: Int? = null,
    val isActive: Boolean? = null,
    val isLoanOfficer: Boolean? = null,
    val joiningDate: List<Int>? = null,
    val lastname: String? = null,
    val mobileNo: String? = null,
    val officeId: Int? = null,
    val officeName: String? = null,
) : Parcelable
