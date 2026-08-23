/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountProfile.components

import kpt.core.ui.generated.resources.account_details
import kpt.core.ui.generated.resources.autorenew
import kpt.core.ui.generated.resources.charges
import kpt.core.ui.generated.resources.collateral
import kpt.core.ui.generated.resources.dashboard
import kpt.core.ui.generated.resources.design_services
import kpt.core.ui.generated.resources.documents
import kpt.core.ui.generated.resources.notes
import kpt.core.ui.generated.resources.repayment_schedule
import kpt.core.ui.generated.resources.reschedules
import kpt.core.ui.generated.resources.term_variations
import kpt.core.ui.generated.resources.transaction
import kpt.feature.loan.generated.resources.feature_loan_profile_item_account_details_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_account_details_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_charges_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_charges_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_collateral_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_collateral_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_dashboard_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_dashboard_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_documents_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_documents_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_general_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_general_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_notes_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_notes_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_repayment_schedule_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_repayment_schedule_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_reschedules_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_reschedules_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_standing_instructions_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_standing_instructions_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_term_variations_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_term_variations_title
import kpt.feature.loan.generated.resources.feature_loan_profile_item_transactions_subtitle
import kpt.feature.loan.generated.resources.feature_loan_profile_item_transactions_title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kpt.core.ui.generated.resources.Res as UiRes
import kpt.feature.loan.generated.resources.Res as LoanRes

sealed class LoanAccountProfileActionItem(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {
    data object General : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_general_title,
        subTitle = LoanRes.string.feature_loan_profile_item_general_subtitle,
        icon = UiRes.drawable.design_services,
    )
    data object Dashboard : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_dashboard_title,
        subTitle = LoanRes.string.feature_loan_profile_item_dashboard_subtitle,
        icon = UiRes.drawable.dashboard,
    )
    data object AccountDetails : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_account_details_title,
        subTitle = LoanRes.string.feature_loan_profile_item_account_details_subtitle,
        icon = UiRes.drawable.account_details,
    )
    data object RepaymentSchedule : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_repayment_schedule_title,
        subTitle = LoanRes.string.feature_loan_profile_item_repayment_schedule_subtitle,
        icon = UiRes.drawable.repayment_schedule,
    )
    data object Transactions : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_transactions_title,
        subTitle = LoanRes.string.feature_loan_profile_item_transactions_subtitle,
        icon = UiRes.drawable.transaction,
    )
    data object Charges : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_charges_title,
        subTitle = LoanRes.string.feature_loan_profile_item_charges_subtitle,
        icon = UiRes.drawable.charges,
    )
    data object Collateral : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_collateral_title,
        subTitle = LoanRes.string.feature_loan_profile_item_collateral_subtitle,
        icon = UiRes.drawable.collateral,
    )
    data object TermVariations : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_term_variations_title,
        subTitle = LoanRes.string.feature_loan_profile_item_term_variations_subtitle,
        icon = UiRes.drawable.term_variations,
    )
    data object Reschedules : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_reschedules_title,
        subTitle = LoanRes.string.feature_loan_profile_item_reschedules_subtitle,
        icon = UiRes.drawable.reschedules,
    )
    data object Documents : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_documents_title,
        subTitle = LoanRes.string.feature_loan_profile_item_documents_subtitle,
        icon = UiRes.drawable.documents,
    )
    data object Notes : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_notes_title,
        subTitle = LoanRes.string.feature_loan_profile_item_notes_subtitle,
        icon = UiRes.drawable.notes,
    )
    data object StandingInstructions : LoanAccountProfileActionItem(
        title = LoanRes.string.feature_loan_profile_item_standing_instructions_title,
        subTitle = LoanRes.string.feature_loan_profile_item_standing_instructions_subtitle,
        icon = UiRes.drawable.autorenew,
    )
}

internal val loanProfileActionItems: ImmutableList<LoanAccountProfileActionItem> = persistentListOf(
    LoanAccountProfileActionItem.General,
    LoanAccountProfileActionItem.Dashboard,
    LoanAccountProfileActionItem.AccountDetails,
    LoanAccountProfileActionItem.RepaymentSchedule,
    LoanAccountProfileActionItem.Transactions,
    LoanAccountProfileActionItem.Charges,
    LoanAccountProfileActionItem.Collateral,
    LoanAccountProfileActionItem.TermVariations,
    LoanAccountProfileActionItem.Reschedules,
    LoanAccountProfileActionItem.Documents,
    LoanAccountProfileActionItem.Notes,
    LoanAccountProfileActionItem.StandingInstructions,
)
