package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Pronay Sarker on 16/08/2024 (7:17 AM)
 */
@Parcelize
data class LoanApprovalData(
    val loanID: Int,
    val loanWithAssociations: LoanWithAssociations
) : Parcelable
