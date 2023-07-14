/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.templates.savings

import android.os.Parcelable
import com.mifos.api.local.MifosBaseModel
import com.mifos.api.local.MifosDatabase
import com.mifos.objects.PaymentTypeOption
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 12/06/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
class SavingsAccountTransactionTemplate(
    @PrimaryKey
    var accountId: Int? = null,

    @Column
    var accountNo: String? = null,

    var date: List<Int> = ArrayList(),

    @Column
    var reversed: Boolean? = null,

    var paymentTypeOptions: List<PaymentTypeOption> = ArrayList()

) : MifosBaseModel(), Parcelable {

    fun isReversed(): Boolean? {
        return reversed
    }
}