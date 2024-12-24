/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * This Model is for saving the ActualDisbursementDate of LoanWithAssociations's Timeline
 * This Model is only for Database use.
 * Created by Rajan Maurya on 26/07/16.
 */
@Parcelize
@Entity("ActualDisbursementDate")
data class ActualDisbursementDate(
    @PrimaryKey
    var loanId: Int? = null,

    @ColumnInfo("year")
    var year: Int? = null,

    @ColumnInfo("month")
    var month: Int? = null,

    @ColumnInfo("date")
    var date: Int? = null,
) : MifosBaseModel(), Parcelable
