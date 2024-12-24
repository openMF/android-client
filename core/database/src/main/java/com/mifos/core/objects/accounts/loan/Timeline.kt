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
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * This Model Time Object of LoanWithAssociations.
 *
 * Here
 */
@Parcelize
@Entity("TimeLine")
class Timeline(
    @PrimaryKey
    @Transient
    var loanId: Int? = null,

    var submittedOnDate: List<Int>? = null,

    var submittedByUsername: String? = null,

    var submittedByFirstname: String? = null,

    var submittedByLastname: String? = null,

    var approvedOnDate: List<Int>? = null,

    var approvedByUsername: String? = null,

    var approvedByFirstname: String? = null,

    var approvedByLastname: String? = null,

    var expectedDisbursementDate: List<Int>? = null,

    // This Object for saving the actualDisbursementDate, Not belong to any POST and GET Request
    @ColumnInfo("actualDisburseDate")
    @Embedded
    @Transient
    var actualDisburseDate: ActualDisbursementDate? = null,

    var actualDisbursementDate: List<Int?>? = null,

    var disbursedByUsername: String? = null,

    var disbursedByFirstname: String? = null,

    var disbursedByLastname: String? = null,

    var closedOnDate: List<Int>? = null,

    var expectedMaturityDate: List<Int>? = null,
) : MifosBaseModel(), Parcelable
