package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class ProductOptions(
    var id: Int,
    var name: String,
    var includeInBorrowerCycle: Boolean,
    var useBorrowerCycle: Boolean,
    var isLinkedToFloatingInterestRates: Boolean,
    var isFloatingInterestRateCalculationAllowed: Boolean,
    var allowVariableInstallments: Boolean,
    var isInterestRecalculationEnabled: Boolean,
    var canDefineInstallmentAmount: Boolean,
    var holdGuaranteeFunds: Boolean,
    var accountMovesOutOfNPAOnlyOnArrearsCompletion: Boolean
) : Parcelable {
    override fun toString(): String {
        return "ProductOptions(id=$id, name=$name, includeInBorrowerCycle=$includeInBorrowerCycle, " +
                "useBorrowerCycle=$useBorrowerCycle, isLinkedToFloatingInterestRates=$isLinkedToFloatingInterestRates, " +
                "isFloatingInterestRateCalculationAllowed=$isFloatingInterestRateCalculationAllowed, " +
                "allowVariableInstallments=$allowVariableInstallments, " +
                "isInterestRecalculationEnabled=$isInterestRecalculationEnabled, " +
                "canDefineInstallmentAmount=$canDefineInstallmentAmount, holdGuaranteeFunds=$holdGuaranteeFunds, " +
                "accountMovesOutOfNPAOnlyOnArrearsCompletion=$accountMovesOutOfNPAOnlyOnArrearsCompletion)"
    }
}