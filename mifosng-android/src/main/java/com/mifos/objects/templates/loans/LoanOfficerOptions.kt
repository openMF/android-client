package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class LoanOfficerOptions(
    var id: Int,
    var firstname: String,
    var lastname: String,
    var displayName: String,
    var mobileNo: String,
    var officeId: Int,
    var officeName: String,
    var isLoanOfficer: Boolean,
    var isActive: Boolean,
    var joiningDate: List<Int>
) : Parcelable {
    override fun toString(): String {
        return "LoanOfficerOptions{" +
                "id=$id, " +
                "firstname='$firstname', " +
                "lastname='$lastname', " +
                "displayName='$displayName', " +
                "mobileNo=$mobileNo, " +
                "officeId=$officeId, " +
                "officeName=$officeName, " +
                "isLoanOfficer=$isLoanOfficer, " +
                "isActive=$isActive, " +
                "joiningDate=$joiningDate" +
                '}'
    }
}