/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDetailsProfile.components

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.add_charge_subtitle
import androidclient.feature.client.generated.resources.add_charge_title
import androidclient.feature.client.generated.resources.adf_scanner
import androidclient.feature.client.generated.resources.apply_new_application_subtitle
import androidclient.feature.client.generated.resources.apply_new_application_title
import androidclient.feature.client.generated.resources.assign_staff_subtitle
import androidclient.feature.client.generated.resources.assign_staff_title
import androidclient.feature.client.generated.resources.client_screen_reports_subtitle
import androidclient.feature.client.generated.resources.client_screen_reports_title
import androidclient.feature.client.generated.resources.closure_application_subtitle
import androidclient.feature.client.generated.resources.closure_application_title
import androidclient.feature.client.generated.resources.create_collateral_subtitle
import androidclient.feature.client.generated.resources.create_collateral_title
import androidclient.feature.client.generated.resources.create_new_folder
import androidclient.feature.client.generated.resources.create_self_service_users_subtitle
import androidclient.feature.client.generated.resources.create_self_service_users_title
import androidclient.feature.client.generated.resources.create_standing_instructions_subtitle
import androidclient.feature.client.generated.resources.create_standing_instructions_title
import androidclient.feature.client.generated.resources.directions
import androidclient.feature.client.generated.resources.manage_accounts
import androidclient.feature.client.generated.resources.meeting_room
import androidclient.feature.client.generated.resources.person_remove
import androidclient.feature.client.generated.resources.request_quote
import androidclient.feature.client.generated.resources.room_preferences
import androidclient.feature.client.generated.resources.switch_account
import androidclient.feature.client.generated.resources.text_snippet
import androidclient.feature.client.generated.resources.transfer_client_subtitle
import androidclient.feature.client.generated.resources.transfer_client_title
import androidclient.feature.client.generated.resources.transfer_within_a_station
import androidclient.feature.client.generated.resources.update_default_account_subtitle
import androidclient.feature.client.generated.resources.update_default_account_title
import androidclient.feature.client.generated.resources.view_standing_instructions_subtitle
import androidclient.feature.client.generated.resources.view_standing_instructions_title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class ClientProfileDetailsActionItem(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {

    data object ApplyNewApplication : ClientProfileDetailsActionItem(
        title = Res.string.apply_new_application_title,
        subTitle = Res.string.apply_new_application_subtitle,
        icon = Res.drawable.text_snippet,
    )

    data object ClosureApplication : ClientProfileDetailsActionItem(
        title = Res.string.closure_application_title,
        subTitle = Res.string.closure_application_subtitle,
        icon = Res.drawable.person_remove,
    )

    data object TransferClient : ClientProfileDetailsActionItem(
        title = Res.string.transfer_client_title,
        subTitle = Res.string.transfer_client_subtitle,
        icon = Res.drawable.transfer_within_a_station,
    )

    data object AssignStaff : ClientProfileDetailsActionItem(
        title = Res.string.assign_staff_title,
        subTitle = Res.string.assign_staff_subtitle,
        icon = Res.drawable.manage_accounts,
    )

    data object AddCharge : ClientProfileDetailsActionItem(
        title = Res.string.add_charge_title,
        subTitle = Res.string.add_charge_subtitle,
        icon = Res.drawable.request_quote,
    )

    data object CreateCollateral : ClientProfileDetailsActionItem(
        title = Res.string.create_collateral_title,
        subTitle = Res.string.create_collateral_subtitle,
        icon = Res.drawable.create_new_folder,
    )

    data object UpdateDefaultAccount : ClientProfileDetailsActionItem(
        title = Res.string.update_default_account_title,
        subTitle = Res.string.update_default_account_subtitle,
        icon = Res.drawable.switch_account,
    )

    data object ClientScreenReports : ClientProfileDetailsActionItem(
        title = Res.string.client_screen_reports_title,
        subTitle = Res.string.client_screen_reports_subtitle,
        icon = Res.drawable.adf_scanner,
    )

    data object CreateStandingInstructions : ClientProfileDetailsActionItem(
        title = Res.string.create_standing_instructions_title,
        subTitle = Res.string.create_standing_instructions_subtitle,
        icon = Res.drawable.meeting_room,
    )

    data object ViewStandingInstructions : ClientProfileDetailsActionItem(
        title = Res.string.view_standing_instructions_title,
        subTitle = Res.string.view_standing_instructions_subtitle,
        icon = Res.drawable.room_preferences,
    )

    data object CreateSelfServiceUsers : ClientProfileDetailsActionItem(
        title = Res.string.create_self_service_users_title,
        subTitle = Res.string.create_self_service_users_subtitle,
        icon = Res.drawable.directions,
    )
}

internal val clientsDetailsActionItems: ImmutableList<ClientProfileDetailsActionItem> =
    persistentListOf(
        ClientProfileDetailsActionItem.ApplyNewApplication,
        ClientProfileDetailsActionItem.ClosureApplication,
        ClientProfileDetailsActionItem.TransferClient,
        ClientProfileDetailsActionItem.AssignStaff,
        ClientProfileDetailsActionItem.AddCharge,
        ClientProfileDetailsActionItem.CreateCollateral,
        ClientProfileDetailsActionItem.UpdateDefaultAccount,
//        ClientProfileDetailsActionItem.ClientScreenReports,
        ClientProfileDetailsActionItem.CreateStandingInstructions,
//        ClientProfileDetailsActionItem.ViewStandingInstructions,
//        ClientProfileDetailsActionItem.CreateSelfServiceUsers,
    )
