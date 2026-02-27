/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountProfile.components

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.account_details
import androidclient.feature.loan.generated.resources.autorenew
import androidclient.feature.loan.generated.resources.charges
import androidclient.feature.loan.generated.resources.collateral
import androidclient.feature.loan.generated.resources.dashboard
import androidclient.feature.loan.generated.resources.design_services
import androidclient.feature.loan.generated.resources.documents
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_account_details_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_account_details_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_charges_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_charges_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_collateral_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_collateral_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_dashboard_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_dashboard_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_documents_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_documents_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_general_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_general_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_notes_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_notes_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_originators_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_originators_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_repayment_schedule_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_repayment_schedule_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_reschedules_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_reschedules_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_standing_instructions_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_standing_instructions_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_term_variations_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_term_variations_title
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_transactions_subtitle
import androidclient.feature.loan.generated.resources.feature_loan_profile_item_transactions_title
import androidclient.feature.loan.generated.resources.notes
import androidclient.feature.loan.generated.resources.originators
import androidclient.feature.loan.generated.resources.repayment_schedule
import androidclient.feature.loan.generated.resources.reschedules
import androidclient.feature.loan.generated.resources.term_variations
import androidclient.feature.loan.generated.resources.transaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class LoanAccountProfileActionItem(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {
    data object General : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_general_title,
        subTitle = Res.string.feature_loan_profile_item_general_subtitle,
        icon = Res.drawable.design_services,
    )
    data object Dashboard : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_dashboard_title,
        subTitle = Res.string.feature_loan_profile_item_dashboard_subtitle,
        icon = Res.drawable.dashboard,
    )
    data object AccountDetails : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_account_details_title,
        subTitle = Res.string.feature_loan_profile_item_account_details_subtitle,
        icon = Res.drawable.account_details,
    )
    data object RepaymentSchedule : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_repayment_schedule_title,
        subTitle = Res.string.feature_loan_profile_item_repayment_schedule_subtitle,
        icon = Res.drawable.repayment_schedule,
    )
    data object Transactions : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_transactions_title,
        subTitle = Res.string.feature_loan_profile_item_transactions_subtitle,
        icon = Res.drawable.transaction,
    )
    data object Charges : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_charges_title,
        subTitle = Res.string.feature_loan_profile_item_charges_subtitle,
        icon = Res.drawable.charges,
    )
    data object Originators : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_originators_title,
        subTitle = Res.string.feature_loan_profile_item_originators_subtitle,
        icon = Res.drawable.originators,
    )
    data object Collateral : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_collateral_title,
        subTitle = Res.string.feature_loan_profile_item_collateral_subtitle,
        icon = Res.drawable.collateral,
    )
    data object TermVariations : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_term_variations_title,
        subTitle = Res.string.feature_loan_profile_item_term_variations_subtitle,
        icon = Res.drawable.term_variations,
    )
    data object Reschedules : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_reschedules_title,
        subTitle = Res.string.feature_loan_profile_item_reschedules_subtitle,
        icon = Res.drawable.reschedules,
    )
    data object Documents : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_documents_title,
        subTitle = Res.string.feature_loan_profile_item_documents_subtitle,
        icon = Res.drawable.documents,
    )
    data object Notes : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_notes_title,
        subTitle = Res.string.feature_loan_profile_item_notes_subtitle,
        icon = Res.drawable.notes,
    )
    data object StandingInstructions : LoanAccountProfileActionItem(
        title = Res.string.feature_loan_profile_item_standing_instructions_title,
        subTitle = Res.string.feature_loan_profile_item_standing_instructions_subtitle,
        icon = Res.drawable.autorenew,
    )
}

internal val loanProfileActionItems: ImmutableList<LoanAccountProfileActionItem> = persistentListOf(
    LoanAccountProfileActionItem.General,
    LoanAccountProfileActionItem.Dashboard,
    LoanAccountProfileActionItem.AccountDetails,
    LoanAccountProfileActionItem.RepaymentSchedule,
    LoanAccountProfileActionItem.Transactions,
    LoanAccountProfileActionItem.Charges,
    LoanAccountProfileActionItem.Originators,
    LoanAccountProfileActionItem.Collateral,
    LoanAccountProfileActionItem.TermVariations,
    LoanAccountProfileActionItem.Reschedules,
    LoanAccountProfileActionItem.Documents,
    LoanAccountProfileActionItem.Notes,
    LoanAccountProfileActionItem.StandingInstructions,
)
