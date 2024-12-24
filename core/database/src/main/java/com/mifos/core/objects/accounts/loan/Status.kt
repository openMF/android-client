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
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("Status")
class Status(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("code")
    var code: String? = null,

    @ColumnInfo("value")
    var value: String? = null,

    @ColumnInfo("pendingApproval")
    var pendingApproval: Boolean? = null,

    @ColumnInfo("waitingForDisbursal")
    var waitingForDisbursal: Boolean? = null,

    @ColumnInfo("active")
    var active: Boolean? = null,

    @ColumnInfo("closedObligationsMet")
    var closedObligationsMet: Boolean? = null,

    @ColumnInfo("closedWrittenOff")
    var closedWrittenOff: Boolean? = null,

    @ColumnInfo("closedRescheduled")
    var closedRescheduled: Boolean? = null,

    @ColumnInfo("closed")
    var closed: Boolean? = null,

    @ColumnInfo("overpaid")
    var overpaid: Boolean? = null,
) : MifosBaseModel(), Parcelable {

    fun isPendingApproval(): Boolean? {
        return pendingApproval
    }

    fun isWaitingForDisbursal(): Boolean? {
        return waitingForDisbursal
    }

    fun isActive(): Boolean? {
        return active
    }

    fun isClosedObligationsMet(): Boolean? {
        return closedObligationsMet
    }

    fun isClosedWrittenOff(): Boolean? {
        return closedWrittenOff
    }

    fun isClosedRescheduled(): Boolean? {
        return closedRescheduled
    }

    fun isClosed(): Boolean? {
        return closed
    }

    fun isOverpaid(): Boolean? {
        return overpaid
    }
}
