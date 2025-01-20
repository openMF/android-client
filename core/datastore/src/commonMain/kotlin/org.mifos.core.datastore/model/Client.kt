/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.core.datastore

import org.mifos.core.datastore.Parcelable
import org.mifos.core.datastore.Parcelize

@Parcelize
data class Client(
    val id: Long,
    val accountNo: String,
    val externalId: String,
    val active: Boolean,
    val activationDate: List<Long>,
    val firstname: String,
    val lastname: String,
    val displayName: String,
    val mobileNo: String,
    val emailAddress: String,
    val dateOfBirth: List<Long>,
    val isStaff: Boolean,
    val officeId: Long,
    val officeName: String,
    val savingsProductName: String,
    val timeline: ClientTimeline = ClientTimeline(),
    val status: ClientStatus = ClientStatus(),
    val legalForm: ClientStatus = ClientStatus(),
) : Parcelable
