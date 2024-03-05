/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class SavingsAccount(
    @Column
    @Transient
    var clientId: Long = 0,

    @Column
    @Transient
    var groupId: Long = 0,

    @Column
    var centerId: Long = 0,

    @PrimaryKey
    var id: Int? = null,

    @Column
    var accountNo: String? = null,

    @Column
    var productId: Int? = null,

    @Column
    var productName: String? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var status: Status? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var currency: Currency? = null,

    @Column
    var accountBalance: Double? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var depositType: DepositType? = null
) : MifosBaseModel(), Parcelable
