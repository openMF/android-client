/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cmp.navigation.generated.resources.Res
import cmp.navigation.generated.resources.cd_notifications
import cmp.navigation.generated.resources.cd_open_navigation_drawer
import cmp.navigation.generated.resources.cd_search
import cmp.navigation.generated.resources.topbarlogo
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.base.ui.AppInfo
import kpt.core.designsystem.component.AppCard
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.DesignToken
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The dashboard top bar — ported from `6b66e8a43:cmp-navigation/components/MifosTopAppBar.kt`.
 *
 * ## Ported, not redesigned
 *
 * An earlier pass rebuilt this bar from the template's idiom instead of porting the original:
 * `containerColor = colorScheme.primary` with `onPrimary` content, and the app name stacked under
 * the logo as `labelMedium`. That produced a dark-blue bar — legible, themed, and not the app. The
 * original's bar is LIGHT (`surfaceContainerLowest`) with `onSurface` icons and the wordmark beside
 * the logo in its own brand blue, sitting in a card with rounded bottom corners. Every value below
 * comes from that file; none of them are a judgement call made here.
 *
 * The one deliberate difference is the SEARCH action, which the original also had — it just led to
 * the first TAB rather than to a destination, because Search was tab one. See
 * `AuthenticatedNavBarTabItem.HomeTab` for why that moved.
 *
 * ## Brand-neutral on purpose
 *
 * The original hardcodes a "Mifos" string resource and a literal `AppColors.titleColorTopAbbBar`
 * (0xFF0773BB). This shell is white-label, so neither is copied: the name comes from
 * `AppInfo.appDisplayName` (app-profile → BuildKonfig) and the colour from `colorScheme.primary`.
 * The brand hex still exists — it lives in this fork's palette, which is where a brand value goes.
 * The LOGO is a fork-supplied drawable, carried across from `6b66e8a43` with the drawer assets;
 * another fork replaces the file, not this code.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardTopBar(
    onNavigationIconClick: () -> Unit,
    onSearchIconClick: () -> Unit,
    onNotificationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        shape = RoundedCornerShape(
            bottomStart = DesignToken.spacing.large,
            bottomEnd = DesignToken.spacing.large,
        ),
        elevation = DesignToken.elevation.appBar,
        modifier = Modifier.fillMaxWidth().testTag(DASHBOARD_TOP_BAR_TAG),
    ) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.topbarlogo),
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(DesignToken.spacing.mediumSmall))
                    Text(
                        // The FORK's display name, from app-profile via BuildKonfig — not a
                        // hardcoded wordmark. The original read a literal "Mifos" string resource,
                        // which is the one thing in that file a white-label shell must not copy.
                        text = AppInfo.appDisplayName,
                        style = KptTheme.typography.titleLarge,
                        // `primary`, not the original's literal 0xFF0773BB. The hex is a brand
                        // value and belongs in the palette, where `Color.kt` already carries it for
                        // this fork; reading it from the theme is what lets another fork's name
                        // come out in that fork's colour.
                        color = KptTheme.colorScheme.primary,
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigationIconClick,
                    modifier = Modifier.testTag(DASHBOARD_MENU_ACTION_TAG),
                ) {
                    Icon(
                        imageVector = AppIcons.Menu,
                        contentDescription = stringResource(Res.string.cd_open_navigation_drawer),
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onSearchIconClick,
                    modifier = Modifier.testTag(DASHBOARD_SEARCH_ACTION_TAG),
                ) {
                    Icon(
                        AppIcons.Search,
                        contentDescription = stringResource(Res.string.cd_search),
                    )
                }
                IconButton(onClick = onNotificationIconClick) {
                    Icon(
                        AppIcons.Notifications,
                        contentDescription = stringResource(Res.string.cd_notifications),
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = KptTheme.colorScheme.surfaceContainerLowest,
                navigationIconContentColor = KptTheme.colorScheme.onSurface,
                actionIconContentColor = KptTheme.colorScheme.onSurface,
            ),
            expandedHeight = DesignToken.sizes.topAppBarHeight,
        )
    }
}

internal const val DASHBOARD_TOP_BAR_TAG: String = "shell:top-bar"
internal const val DASHBOARD_SEARCH_ACTION_TAG: String = "shell:top-bar:search"
internal const val DASHBOARD_MENU_ACTION_TAG: String = "shell:top-bar:menu"
