/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.dbobjects.accounts.loan

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * This Model is for saving the ActualDisbursementDate of LoanWithAssociations's Timeline
 * This Model is only for Database use.
 * Created by Rajan Maurya on 26/07/16.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class ActualDisbursementDate(
    @PrimaryKey
    var loanId: Int? = null,

    @Column
    var year: Int? = null,

    @Column
    var month: Int? = null,

    @Column
    var date: Int? = null,
) : MifosBaseModel(), Parcelable
