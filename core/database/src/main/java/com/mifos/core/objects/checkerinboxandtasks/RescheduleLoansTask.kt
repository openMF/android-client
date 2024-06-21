package com.mifos.core.objects.checkerinboxandtasks

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RescheduleLoansTask(
    var id: Int,
    var clientName: String,
    var loanAccountNumber: String,
    var rescheduleFromDate: Array<Int>,
    var actionName: String,
    var rescheduleReasonCodeValue: RescheduleReasonCodeValue
) : Parcelable