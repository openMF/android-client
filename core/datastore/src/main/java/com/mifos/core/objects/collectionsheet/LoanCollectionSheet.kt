package com.mifos.core.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.Currency

/**
 * Created by Tarun on 06-07-2017.
 */
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
        interestDue = parcel.readValue(Double::class.java.classLoader) as? Double
        interestPaid = parcel.readValue(Double::class.java.classLoader) as? Double
        loanId = parcel.readValue(Int::class.java.classLoader) as Int
        principalDue = parcel.readValue(Double::class.java.classLoader) as? Double
        productId = parcel.readValue(Double::class.java.classLoader) as? Double
        totalDue = parcel.readValue(Double::class.java.classLoader) as Double
        chargesDue = parcel.readValue(Double::class.java.classLoader) as Double
        productShortName = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(accountId)
        dest.writeInt(accountStatusId)
        dest.writeParcelable(currency, flags)
        dest.writeValue(interestDue)
        dest.writeValue(interestPaid)
        dest.writeValue(loanId)
        dest.writeValue(principalDue)
        dest.writeValue(productId)
        dest.writeValue(totalDue)
        dest.writeValue(chargesDue)
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