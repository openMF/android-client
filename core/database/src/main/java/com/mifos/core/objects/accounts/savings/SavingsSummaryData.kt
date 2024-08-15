package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Pronay Sarker on 15/08/2024 (9:49 PM)
 */
@Parcelize
data class SavingsSummaryData(val id : Int, val type : DepositType) : Parcelable