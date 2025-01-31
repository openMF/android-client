/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.collectionsheets

import com.mifos.core.common.utils.Parcel
import com.mifos.core.common.utils.Parcelable

/**
 * Created by Tarun on 06-07-2017.
 */
// TODO migrate
class LoanCollectionSheet() : Parcelable {
    var accountId: String? = null
    var accountStatusId: Int = 0
    var currency: Currency? = null
    var interestDue: Double? = null
    var interestPaid: Double? = null
    var loanId: Int = 0
    var principalDue: Double? = null
    var productId: Double? = null
    var totalDue: Double = 0.0
    var chargesDue: Double = 0.0
    var productShortName: String? = null

    constructor(parcel: Parcel) : this() {
        accountId = parcel.readString()
        accountStatusId = parcel.readInt()
        currency = parcel.readParcelable(Currency::class.java.classLoader)
        interestDue = parcel.readDouble()
        interestPaid = parcel.readDouble()
        loanId = parcel.readInt()
        principalDue = parcel.readDouble()
        productId = parcel.readDouble()
        totalDue = parcel.readDouble()
        chargesDue = parcel.readDouble()
        productShortName = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(accountId)
        dest.writeInt(accountStatusId)
        dest.writeParcelable(currency, flags)
        dest.writeDouble(interestDue)
        dest.writeDouble(interestPaid)
        dest.writeInt(loanId)
        dest.writeDouble(principalDue)
        dest.writeDouble(productId)
        dest.writeDouble(totalDue)
        dest.writeDouble(chargesDue)
        dest.writeString(productShortName)
    }

    override fun toString(): String {
        return "LoanCollectionSheet(accountId=$accountId, accountStatusId=$accountStatusId, currency=$currency, " +
            "interestDue=$interestDue, interestPaid=$interestPaid, loanId=$loanId, principalDue=$principalDue, " +
            "productId=$productId, totalDue=$totalDue, chargesDue=$chargesDue, productShortName=$productShortName)"
    }

    companion object CREATOR : Parcelable.Creator<LoanCollectionSheet> {
        override fun createFromParcel(parcel: Parcel): LoanCollectionSheet {
            return LoanCollectionSheet(parcel)
        }

        override fun newArray(size: Int): Array<LoanCollectionSheet?> {
            return arrayOfNulls(size)
        }
    }
}
