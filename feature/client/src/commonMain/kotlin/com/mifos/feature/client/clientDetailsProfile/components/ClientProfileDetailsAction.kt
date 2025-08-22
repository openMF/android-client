package com.mifos.feature.client.clientDetailsProfile.components


import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.adf_scanner
import androidclient.feature.client.generated.resources.contact_emergency
import androidclient.feature.client.generated.resources.create_new_folder
import androidclient.feature.client.generated.resources.design_services
import androidclient.feature.client.generated.resources.directions
import androidclient.feature.client.generated.resources.manage_accounts
import androidclient.feature.client.generated.resources.meeting_room
import androidclient.feature.client.generated.resources.note_add
import androidclient.feature.client.generated.resources.note_alt
import androidclient.feature.client.generated.resources.people
import androidclient.feature.client.generated.resources.person_remove
import androidclient.feature.client.generated.resources.request_quote
import androidclient.feature.client.generated.resources.room_preferences
import androidclient.feature.client.generated.resources.sign_language
import androidclient.feature.client.generated.resources.switch_account
import androidclient.feature.client.generated.resources.text_snippet
import androidclient.feature.client.generated.resources.transfer_within_a_station
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource


sealed class ClientProfileDetailsActionItem(
    val title: String,
    val subTitle: String,
    val icon: DrawableResource,
) {

    data object ApplyNewApplication : ClientProfileDetailsActionItem(
        title = "Apply New Application",
        subTitle = "Create and apply for new accounts",
        icon = Res.drawable.text_snippet,
    )

    data object ClosureApplication : ClientProfileDetailsActionItem(
        title = "Closure Application",
        subTitle = "Apply for ID closure",
        icon = Res.drawable.person_remove,
    )

    data object TransferClient : ClientProfileDetailsActionItem(
        title = "Transfer Client",
        subTitle = "Apply for transfer of the ID",
        icon = Res.drawable.transfer_within_a_station,
    )

    data object AssignStaff : ClientProfileDetailsActionItem(
        title = "Assign Staff",
        subTitle = "Assign or change the service staff",
        icon = Res.drawable.manage_accounts,
    )

    data object AddCharge : ClientProfileDetailsActionItem(
        title = "Add Charge",
        subTitle = "Add new charge over the ID",
        icon = Res.drawable.request_quote,
    )

    data object CreateCollateral : ClientProfileDetailsActionItem(
        title = "Create Collateral",
        subTitle = "Add or create a new collateral",
        icon = Res.drawable.create_new_folder,
    )

    data object UpdateDefaultAccount : ClientProfileDetailsActionItem(
        title = "Update Default Account",
        subTitle = "Update the default savings account",
        icon = Res.drawable.switch_account,
    )

    data object ClientScreenReports : ClientProfileDetailsActionItem(
        title = "Client Screen Reports",
        subTitle = "Generate the screening reports",
        icon = Res.drawable.adf_scanner,
    )

    data object CreateStandingInstructions : ClientProfileDetailsActionItem(
        title = "Create Standing Instructions",
        subTitle = "Create new standing instruction over ID",
        icon = Res.drawable.meeting_room,
    )

    data object ViewStandingInstructions : ClientProfileDetailsActionItem(
        title = "View Standing Instructions",
        subTitle = "View standing instruction over ID",
        icon = Res.drawable.room_preferences,
    )

    data object CreateSelfServiceUsers : ClientProfileDetailsActionItem(
        title = "Create Self Service Users",
        subTitle = "Change the status of ID to self service",
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
        ClientProfileDetailsActionItem.ClientScreenReports,
        ClientProfileDetailsActionItem.CreateStandingInstructions,
        ClientProfileDetailsActionItem.ViewStandingInstructions,
        ClientProfileDetailsActionItem.CreateSelfServiceUsers,
    )


