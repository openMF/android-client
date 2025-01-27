/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Composable
fun MifosNavigationBar(
    route: String,
    modifier: Modifier = Modifier,
    onRouteSelected: (targetRoute: String) -> Unit,
) {
    val tabs = rememberSaveable {
        listOf(
            HomeDestinationsScreen.SearchScreen,
            HomeDestinationsScreen.ClientListScreen,
            HomeDestinationsScreen.CenterListScreen,
            HomeDestinationsScreen.GroupListScreen,
        )
    }

    NavigationBar(
        modifier = modifier,
    ) {
        tabs.forEach { item ->
            val targetRoute = item.route
            val selected = route.contains(targetRoute)
            NavigationBarItem(
                icon = {
                    item.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.title,
                        )
                    }
                },
                label = {
                    Text(text = item.title)
                },
                selected = selected,
                onClick = { onRouteSelected(targetRoute) },
            )
        }
    }
}
