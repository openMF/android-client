/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.authenticatednavbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cmp.navigation.generated.resources.Res
import cmp.navigation.generated.resources.cd_profile_picture
import cmp.navigation.generated.resources.drawer_profile_header
import cmp.navigation.generated.resources.ic_dp_placeholder
import cmp.navigation.navigation.HomeDestinationsScreen
import kpt.core.base.ui.AppInfo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The dashboard's navigation DRAWER — ported from
 * `6b66e8a43:cmp-navigation/.../navigation/HomeNavigation.kt`, which drew it inline inside the
 * navigation host. (The top bar had its own component in the original and is ported as one, in
 * `components/AppTopAppBar.kt`.)
 *
 * ## Why they live in the SHELL rather than in `feature/home`
 *
 * The original renders ONE bar above ALL four tabs, with the hamburger reachable from every one of
 * them. `feature/home` is one tab; a bar it owned would vanish on Clients, Centers and Groups, and
 * the drawer with it. So the chrome belongs to the shell that outlives any single tab — which is
 * also why `homeGraph` now takes a `topBar` slot the shell passes empty (see that change's note).
 */

/** Height of the drawer header, and of the avatar inside it — the original's values. */
private val HEADER_HEIGHT = 200.dp
private val AVATAR_SIZE = 64.dp

/**
 * Fixed sheet width. The original pins it rather than letting the sheet track the longest label:
 * "Checker Inbox & Tasks" would otherwise stretch the sheet nearly edge to edge on a phone.
 */
private val DRAWER_WIDTH = 320.dp

internal const val DASHBOARD_DRAWER_TAG: String = "shell:drawer"

/**
 * The drawer sheet: a photo header that opens Profile, then [HomeDestinationsScreen.entries] in
 * their declared order.
 *
 * The divider sits above the LAST entry (`index == size - 1`), separating Offline Sync from the
 * six navigational entries above it — it is a mode switch, not another destination. Its position is
 * computed from the list, so reordering `entries` moves it; see that file's note.
 */
@Composable
internal fun DashboardDrawerSheet(
    selected: HomeDestinationsScreen?,
    onEntryClick: (HomeDestinationsScreen) -> Unit,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier.requiredWidth(DRAWER_WIDTH).testTag(DASHBOARD_DRAWER_TAG),
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            DrawerHeader(onClick = onHeaderClick)

            val entries = HomeDestinationsScreen.entries
            entries.forEachIndexed { index, entry ->
                NavigationDrawerItem(
                    label = { Text(text = stringResource(entry.title)) },
                    icon = { Icon(imageVector = entry.icon, contentDescription = null) },
                    selected = entry == selected,
                    onClick = { onEntryClick(entry) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
                if (index == entries.size - 2) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

/**
 * The header. The photo + avatar are the original's own assets, carried across from `6b66e8a43`
 * rather than substituted with a tinted box — a solid rectangle would have read as a port that
 * "mostly" matched while being the one part of the drawer a user looks at first.
 */
@Composable
private fun DrawerHeader(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HEADER_HEIGHT),
    ) {
        Image(
            painter = painterResource(Res.drawable.drawer_profile_header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(AVATAR_SIZE),
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_dp_placeholder),
                    contentDescription = stringResource(Res.string.cd_profile_picture),
                    modifier = Modifier.size(AVATAR_SIZE).clip(CircleShape),
                )
            }
            Text(
                // The app's display name from app-profile, NOT a hardcoded brand string — the
                // drawer header had the literal "Mifos" in `drawer_profile_name`. Same source
                // DashboardTopBar reads, so the two cannot disagree.
                text = AppInfo.appDisplayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                // Fixed white, not onPrimary: the text sits on a PHOTO, so it must contrast with
                // the image rather than with any theme surface. Same reason the original hardcodes it.
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
