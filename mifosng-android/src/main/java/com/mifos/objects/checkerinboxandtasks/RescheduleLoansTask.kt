package com.mifos.objects.checkerinboxandtasks

import com.google.gson.annotations.SerializedName

data class RescheduleLoansTask (@SerializedName("id") var id: Int,
                        @SerializedName("clientName") var clientName: String,
                        @SerializedName("loanAccountNumber") var loanAccountNo: String,
                        @SerializedName("rescheduleFromDate") var rescheduleFromDate: Array<Int>,
                        @SerializedName("actionName") var action: String,
                        @SerializedName("rescheduleReasonCodeValue")
                                var rescheduleReasonCodeValue: RescheduleReasonCodeValue)