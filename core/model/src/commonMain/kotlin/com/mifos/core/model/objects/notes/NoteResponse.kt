package com.mifos.core.model.objects.notes

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse(

    val officeId: Int? = null,

    val clientId: Int? = null,

    val resourceId: Int? = null,

    val changes: NoteChanges? = null
)

@Serializable
data class NoteChanges(

    val note: String? = null
)