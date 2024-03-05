/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@ModelContainer
@Table(database = MifosDatabase::class)
data class LoanRepaymentTemplate(
    @PrimaryKey(autoincrement = true)
    var loanId: Int? = null,

    var type: Type? = null,

    var date: MutableList<Int>? = null,

    var currency: Currency? = null,

    @Column
    var amount: Double? = null,

    @Column
    var principalPortion: Double? = null,

    @Column
    var interestPortion: Double? = null,

    @Column
    var feeChargesPortion: Double? = null,

    @Column
    var penaltyChargesPortion: Double? = null,

    var paymentTypeOptions: MutableList<com.mifos.core.objects.PaymentTypeOption>? = null
) : MifosBaseModel(), Parcelable