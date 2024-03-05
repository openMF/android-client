package com.mifos.core.objects.client

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 2/15/2016.
 */
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@Parcelize
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class Charges(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var clientId: Int? = null,

    @Column
    var loanId: Int? = null,

    @Column
    var chargeId: Int? = null,

    @Column
    var name: String? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var chargeTimeType: ChargeTimeType? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var chargeDueDate: ClientDate? = null,

    var dueDate: List<Int> = ArrayList(),

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var chargeCalculationType: ChargeCalculationType? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var currency: Currency? = null,

    @Column
    var amount: Double? = null,

    @Column
    var amountPaid: Double? = null,

    @Column
    var amountWaived: Double? = null,

    @Column
    var amountWrittenOff: Double? = null,

    @Column
    var amountOutstanding: Double? = null,

    @Column
    var penalty: Boolean? = null,

    @Column
    var active: Boolean? = null,

    @Column
    var paid: Boolean? = null,

    @Column
    var waived: Boolean? = null
) : MifosBaseModel(), Parcelable {

    val formattedDueDate: String
        get() {
            val pattern = "%s-%s-%s"
            return String.format(
                pattern,
                dueDate[0], dueDate[1], dueDate[2]
            )
        }
}