/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.note.navigation

import kotlinx.serialization.Serializable

@Serializable
data class NoteRoute(
    val resourceId: Int,
    val resourceType: String?,
)

@Serializable
data class AddEditNoteRoute(
    val resourceId: Int,
    val resourceType: String?,
    val noteId: Long?,
)
