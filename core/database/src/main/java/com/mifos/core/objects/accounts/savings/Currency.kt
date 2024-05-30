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
@Table(database = MifosDatabase::class, name = "SavingAccountCurrency")
@ModelContainer
data class Currency(
    @PrimaryKey
    var code: String? = null,

    @Column
    var name: String? = null,

    @Column
    var decimalPlaces: Int? = null,

    @Column
    var inMultiplesOf: Int? = null,

    @Column
    var displaySymbol: String? = null,

    @Column
    var nameCode: String? = null,

    @Column
    var displayLabel: String? = null
) : MifosBaseModel(), Parcelable