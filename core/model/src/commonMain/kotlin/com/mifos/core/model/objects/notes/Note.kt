/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.notes

import kotlinx.serialization.Serializable

/**
* GetResourceTypeResourceIdNotesNoteIdResponse
*
* @param clientId
* @param createdById
* @param createdByUsername
* @param createdOn
* @param id
* @param note
* @param updatedById
* @param updatedByUsername
* @param updatedOn
*/

@Serializable
data class Note(

    val clientId: Long? = null,

    val createdById: Long? = null,

    val createdByUsername: String? = null,

    val createdOn: Long? = null,

    val id: Long? = null,

    val note: String? = null,

    val updatedById: Long? = null,

    val updatedByUsername: String? = null,

    val updatedOn: Long? = null,

)
