/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientProfile.components

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_profile_address_subtitle
import androidclient.feature.client.generated.resources.client_profile_address_title
import androidclient.feature.client.generated.resources.client_profile_documents_subtitle
import androidclient.feature.client.generated.resources.client_profile_documents_title
import androidclient.feature.client.generated.resources.client_profile_family_subtitle
import androidclient.feature.client.generated.resources.client_profile_family_title
import androidclient.feature.client.generated.resources.client_profile_general_subtitle
import androidclient.feature.client.generated.resources.client_profile_general_title
import androidclient.feature.client.generated.resources.client_profile_identifiers_subtitle
import androidclient.feature.client.generated.resources.client_profile_identifiers_title
import androidclient.feature.client.generated.resources.client_profile_notes_subtitle
import androidclient.feature.client.generated.resources.client_profile_notes_title
import androidclient.feature.client.generated.resources.contact_emergency
import androidclient.feature.client.generated.resources.design_services
import androidclient.feature.client.generated.resources.note_add
import androidclient.feature.client.generated.resources.note_alt
import androidclient.feature.client.generated.resources.people
import androidclient.feature.client.generated.resources.sign_language
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class ClientProfileActionItem(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {

    data object General : ClientProfileActionItem(
        title = Res.string.client_profile_general_title,
        subTitle = Res.string.client_profile_general_subtitle,
        icon = Res.drawable.design_services,
    )
    data object Address : ClientProfileActionItem(
        title = Res.string.client_profile_address_title,
        subTitle = Res.string.client_profile_address_subtitle,
        icon = Res.drawable.contact_emergency,
    )
    data object FamilyMembers : ClientProfileActionItem(
        title = Res.string.client_profile_family_title,
        subTitle = Res.string.client_profile_family_subtitle,
        icon = Res.drawable.people,
    )
    data object Identifiers : ClientProfileActionItem(
        title = Res.string.client_profile_identifiers_title,
        subTitle = Res.string.client_profile_identifiers_subtitle,
        icon = Res.drawable.sign_language,
    )
    data object Documents : ClientProfileActionItem(
        title = Res.string.client_profile_documents_title,
        subTitle = Res.string.client_profile_documents_subtitle,
        icon = Res.drawable.note_add,
    )
    data object Notes : ClientProfileActionItem(
        title = Res.string.client_profile_notes_title,
        subTitle = Res.string.client_profile_notes_subtitle,
        icon = Res.drawable.note_alt,
    )
}

internal val clientsActionItems: ImmutableList<ClientProfileActionItem> = persistentListOf(
    ClientProfileActionItem.General,
    ClientProfileActionItem.Address,
    ClientProfileActionItem.Identifiers,
    ClientProfileActionItem.Documents,
    ClientProfileActionItem.Notes,
)
