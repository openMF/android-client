/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientGeneral

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_profile_general_action_subtitle_collateral_data
import androidclient.feature.client.generated.resources.client_profile_general_action_subtitle_fixed_deposit_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_subtitle_loan_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_subtitle_recurring_deposit_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_subtitle_saving_account
import androidclient.feature.client.generated.resources.client_profile_general_action_subtitle_shares_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_subtitle_upcoming_charges
import androidclient.feature.client.generated.resources.client_profile_general_action_title_collateral_data
import androidclient.feature.client.generated.resources.client_profile_general_action_title_fixed_deposit_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_title_loan_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_title_recurring_deposit_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_title_saving_account
import androidclient.feature.client.generated.resources.client_profile_general_action_title_shares_accounts
import androidclient.feature.client.generated.resources.client_profile_general_action_title_upcoming_charges
import androidclient.feature.client.generated.resources.collateral_data
import androidclient.feature.client.generated.resources.fixed_deposit_accounts
import androidclient.feature.client.generated.resources.loan_account
import androidclient.feature.client.generated.resources.recurring_deposit_accounts
import androidclient.feature.client.generated.resources.saving_account
import androidclient.feature.client.generated.resources.shares_accounts
import androidclient.feature.client.generated.resources.upcoming_charges
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class ClientProfileGeneralActionItem(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {

    data object UpcomingCharges : ClientProfileGeneralActionItem(
        title = Res.string.client_profile_general_action_title_upcoming_charges,
        subTitle = Res.string.client_profile_general_action_subtitle_upcoming_charges,
        icon = Res.drawable.upcoming_charges,
    )
    data object LoanAccount : ClientProfileGeneralActionItem(
        title = Res.string.client_profile_general_action_title_loan_accounts,
        subTitle = Res.string.client_profile_general_action_subtitle_loan_accounts,
        icon = Res.drawable.loan_account,
    )
    data object SavingAccounts : ClientProfileGeneralActionItem(
        title = Res.string.client_profile_general_action_title_saving_account,
        subTitle = Res.string.client_profile_general_action_subtitle_saving_account,
        icon = Res.drawable.saving_account,
    )
    data object FixedDepositAccounts : ClientProfileGeneralActionItem(
        title = Res.string.client_profile_general_action_title_fixed_deposit_accounts,
        subTitle = Res.string.client_profile_general_action_subtitle_fixed_deposit_accounts,
        icon = Res.drawable.fixed_deposit_accounts,
    )
    data object RecurringDepositAccounts : ClientProfileGeneralActionItem(
        title = Res.string.client_profile_general_action_title_recurring_deposit_accounts,
        subTitle = Res.string.client_profile_general_action_subtitle_recurring_deposit_accounts,
        icon = Res.drawable.recurring_deposit_accounts,
    )
    data object SharesAccounts : ClientProfileGeneralActionItem(
        title = Res.string.client_profile_general_action_title_shares_accounts,
        subTitle = Res.string.client_profile_general_action_subtitle_shares_accounts,
        icon = Res.drawable.shares_accounts,
    )
    data object CollateralData : ClientProfileGeneralActionItem(
        title = Res.string.client_profile_general_action_title_collateral_data,
        subTitle = Res.string.client_profile_general_action_subtitle_collateral_data,
        icon = Res.drawable.collateral_data,
    )
}

internal val clientProfileGeneralActions: ImmutableList<ClientProfileGeneralActionItem> =
    persistentListOf(
        ClientProfileGeneralActionItem.UpcomingCharges,
        ClientProfileGeneralActionItem.LoanAccount,
        ClientProfileGeneralActionItem.SavingAccounts,
        ClientProfileGeneralActionItem.FixedDepositAccounts,
        ClientProfileGeneralActionItem.RecurringDepositAccounts,
        ClientProfileGeneralActionItem.SharesAccounts,
        ClientProfileGeneralActionItem.CollateralData,
    )
