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

import org.mifospay.core.datastore.Parcelable
import org.mifospay.core.datastore.Parcelize

@Parcelize
data class UpdatedClient(
    val firstname: String,
    val lastname: String,
    val externalId: String,
    val mobileNo: String,
    val emailAddress: String,
) : Parcelable
