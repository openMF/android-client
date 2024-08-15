package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Pronay Sarker on 15/08/2024 (11:12 PM)
 */
@Parcelize
data class SavingsTransactionData(
    val savingsAccountWithAssociations: SavingsAccountWithAssociations,
    val depositType: DepositType?,
    val transactionType: String
) : Parcelable
