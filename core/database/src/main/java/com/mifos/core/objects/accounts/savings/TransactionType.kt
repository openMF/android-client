/*
 * This project is licensed under the open source MPL V2.
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
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class TransactionType(
    @PrimaryKey
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null,

    @Column
    var deposit: Boolean? = null,

    @Column
    var withdrawal: Boolean? = null,

    var interestPosting: Boolean? = null,

    var feeDeduction: Boolean? = null,

    var initiateTransfer: Boolean? = null,

    var approveTransfer: Boolean? = null,

    var withdrawTransfer: Boolean? = null,

    var rejectTransfer: Boolean? = null,

    var overdraftInterest: Boolean? = null,

    var writtenoff: Boolean? = null,

    var overdraftFee: Boolean? = null,
) : MifosBaseModel(), Parcelable
