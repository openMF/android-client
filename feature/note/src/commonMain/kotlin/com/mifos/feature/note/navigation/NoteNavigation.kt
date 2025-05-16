/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifos.core.common.utils.Constants
import com.mifos.feature.note.NoteScreen

/**
 * Created by Pronay Sarker on 17/08/2024 (12:05 AM)
 */
fun NavGraphBuilder.noteScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = NoteScreens.NoteScreen.route,
        arguments = listOf(
            navArgument(name = Constants.ENTITY_ID, builder = { NavType.IntType }),
            navArgument(name = Constants.ENTITY_TYPE, builder = { NavType.StringType }),
        ),
    ) {
        NoteScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToNoteScreen(entityId: Int, entityType: String?) {
    navigate(NoteScreens.NoteScreen.argument(entityId, entityType))
}
