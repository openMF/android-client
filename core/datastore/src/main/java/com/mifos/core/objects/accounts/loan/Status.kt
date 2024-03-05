/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Table(database = MifosDatabase::class, name = "LoanStatus")
@ModelContainer
class Status(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var code: String? = null,

    @Column
    var value: String? = null,

    @Column
    var pendingApproval: Boolean? = null,

    @Column
    var waitingForDisbursal: Boolean? = null,

    @Column
    var active: Boolean? = null,

    @Column
    var closedObligationsMet: Boolean? = null,

    @Column
    var closedWrittenOff: Boolean? = null,

    @Column
    var closedRescheduled: Boolean? = null,

    @Column
    var closed: Boolean? = null,

    @Column
    var overpaid: Boolean? = null
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