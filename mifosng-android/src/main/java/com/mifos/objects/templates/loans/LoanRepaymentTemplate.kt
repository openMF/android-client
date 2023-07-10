/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.mifos.api.local.MifosBaseModel
import com.mifos.api.local.MifosDatabase
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.savings.Currency
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
    var loanId: Int = 0,

    var type: Type? = null,

    var date: MutableList<Int> = mutableListOf(),

    var currency: Currency? = null,

    @Column
    var amount: Double? = null,

    @Column
    var principalPortion: Double? = null,

    @Column
    var interestPortion: Double = 0.0,

    @Column
    var feeChargesPortion: Double? = null,

    @Column
    var penaltyChargesPortion: Double? = null,

    var paymentTypeOptions: MutableList<PaymentTypeOption> = mutableListOf()
) : MifosBaseModel(), Parcelable {

    override fun toString(): String {
        return "LoanRepaymentTemplate{" +
                "loanId=" + loanId +
                ", type=" + type +
                ", date=" + date +
                ", currency=" + currency +
                ", amount=" + amount +
                ", principalPortion=" + principalPortion +
                ", interestPortion=" + interestPortion +
                ", feeChargesPortion=" + feeChargesPortion +
                ", penaltyChargesPortion=" + penaltyChargesPortion +
                ", paymentTypeOptions=" + paymentTypeOptions +
                '}'
    }
}