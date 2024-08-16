package com.mifos.feature.note.navigation

import com.mifos.core.common.utils.Constants

/**
 * Created by Pronay Sarker on 17/08/2024 (12:05 AM)
 */
sealed class NoteScreens(val route: String) {
    data object NoteScreen : NoteScreens(route = "note_screen/{${Constants.ENTITY_ID}}/{${Constants.ENTITY_TYPE}}") {
        fun argument(entityId: Int, entityType: String?) = "note_screen/$entityId/$entityType"
    }
}
