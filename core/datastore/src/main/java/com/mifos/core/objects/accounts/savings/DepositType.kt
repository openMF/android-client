package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.APIEndPoint
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@Parcelize
@Table(database = MifosDatabase::class, name = "SavingAccountDepositType")
@ModelContainer
class DepositType(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var code: String? = null,

    @Column
    var value: String? = null
) : MifosBaseModel(), Parcelable {

    val isRecurring: Boolean
        get() = ServerTypes.RECURRING.id == id
    val endpoint: String
        get() = ServerTypes.fromId(id!!).endpoint
    val serverType: ServerTypes
        get() = ServerTypes.fromId(id!!)

    enum class ServerTypes(val id: Int, val code: String, val endpoint: String) {
        // TODO: Are these all the types?
        SAVINGS(100, "depositAccountType.savingsDeposit", APIEndPoint.SAVINGS_ACCOUNTS), FIXED(
            200,
            "depositAccountType.fixedDeposit",
            APIEndPoint.SAVINGS_ACCOUNTS
        ),
        RECURRING(300, "depositAccountType.recurringDeposit", APIEndPoint.RECURRING_ACCOUNTS);

        companion object {
            fun fromId(id: Int): ServerTypes {
                for (type in values()) {
                    if (type.id == id) {
                        return type
                    }
                }
                return SAVINGS
            }
        }
    }
}