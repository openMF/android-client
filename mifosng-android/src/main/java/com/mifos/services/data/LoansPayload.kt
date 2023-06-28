/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.services.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.objects.noncore.DataTablePayload

/**
 * Created by nellyk on 2/20/2016.
 */
class LoansPayload : Parcelable {
    var isAllowPartialPeriodInterestCalculation = false
    var amortizationType = 0
    var clientId = 0
    var dateFormat: String? = null

    /* public String getDisbursementData() {
       return disbursementData;
   }

   public void setDisbursementData(String disbursementData) {
       this.disbursementData = disbursementData;
   }*/
    var expectedDisbursementDate: String? = null
    var interestCalculationPeriodType = 0
    var interestRatePerPeriod: Double? = null
    var interestType = 0
    var loanTermFrequency = 0
    var loanTermFrequencyType = 0
    var loanType: String? = null
    var locale: String? = null
    var numberOfRepayments: String? = null
    var principal: String? = null
    var productId = 0
    var repaymentEvery: String? = null
    var repaymentFrequencyType = 0
    var repaymentFrequencyDayOfWeekType: Int? = null
    var repaymentFrequencyNthDayType: Int? = null
    var submittedOnDate: String? = null
    var transactionProcessingStrategyId = 0
    var loanPurposeId = 0
    var loanOfficerId = 0
    var fundId = 0
    var linkAccountId: Int? = null
    var dataTables: ArrayList<DataTablePayload>? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(if (isAllowPartialPeriodInterestCalculation) 1.toByte() else 0.toByte())
        dest.writeInt(amortizationType)
        dest.writeInt(clientId)
        dest.writeString(dateFormat)
        dest.writeString(expectedDisbursementDate)
        dest.writeInt(interestCalculationPeriodType)
        dest.writeValue(interestRatePerPeriod)
        dest.writeInt(interestType)
        dest.writeInt(loanTermFrequency)
        dest.writeInt(loanTermFrequencyType)
        dest.writeString(loanType)
        dest.writeString(locale)
        dest.writeString(numberOfRepayments)
        dest.writeString(principal)
        dest.writeInt(productId)
        dest.writeString(repaymentEvery)
        dest.writeInt(repaymentFrequencyType)
        dest.writeValue(repaymentFrequencyDayOfWeekType)
        dest.writeValue(repaymentFrequencyNthDayType)
        dest.writeString(submittedOnDate)
        dest.writeInt(transactionProcessingStrategyId)
        dest.writeInt(loanPurposeId)
        dest.writeInt(loanOfficerId)
        dest.writeInt(fundId)
        dest.writeValue(linkAccountId)
        dest.writeTypedList(dataTables)
    }

    protected constructor(`in`: Parcel) {
        isAllowPartialPeriodInterestCalculation = `in`.readByte().toInt() != 0
        amortizationType = `in`.readInt()
        clientId = `in`.readInt()
        dateFormat = `in`.readString()
        expectedDisbursementDate = `in`.readString()
        interestCalculationPeriodType = `in`.readInt()
        interestRatePerPeriod = `in`.readValue(Double::class.java.classLoader) as Double?
        interestType = `in`.readInt()
        loanTermFrequency = `in`.readInt()
        loanTermFrequencyType = `in`.readInt()
        loanType = `in`.readString()
        locale = `in`.readString()
        numberOfRepayments = `in`.readString()
        principal = `in`.readString()
        productId = `in`.readInt()
        repaymentEvery = `in`.readString()
        repaymentFrequencyType = `in`.readInt()
        repaymentFrequencyDayOfWeekType = `in`.readValue(
            Int::class.java.classLoader
        ) as Int?
        repaymentFrequencyNthDayType = `in`.readValue(Int::class.java.classLoader) as Int?
        submittedOnDate = `in`.readString()
        transactionProcessingStrategyId = `in`.readInt()
        loanPurposeId = `in`.readInt()
        loanOfficerId = `in`.readInt()
        fundId = `in`.readInt()
        linkAccountId = `in`.readValue(Int::class.java.classLoader) as Int?
        dataTables = `in`.createTypedArrayList(DataTablePayload.CREATOR)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<LoansPayload> = object : Creator<LoansPayload> {
            override fun createFromParcel(source: Parcel): LoansPayload? {
                return LoansPayload(source)
            }

            override fun newArray(size: Int): Array<LoansPayload?> {
                return arrayOfNulls(size)
            }
        }
    }
}