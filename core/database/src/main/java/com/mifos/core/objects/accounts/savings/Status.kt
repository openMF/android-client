/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Table(
    database = MifosDatabase::class,
    name = "SavingsAccountStatus",
    useBooleanGetterSetters = false,
)
@ModelContainer
data class Status(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var code: String? = null,

    @Column
    var value: String? = null,

    @Column
    var submittedAndPendingApproval: Boolean? = null,

    @Column
    var approved: Boolean? = null,

    @Column
    var rejected: Boolean? = null,

    @Column
    var withdrawnByApplicant: Boolean? = null,

    @Column
    var active: Boolean? = null,

    @Column
    var closed: Boolean? = null,
) : MifosBaseModel(), Parcelable
