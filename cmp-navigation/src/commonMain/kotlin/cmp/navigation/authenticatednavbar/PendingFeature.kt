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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cmp.navigation.generated.resources.Res
import cmp.navigation.generated.resources.pending_message
import cmp.navigation.generated.resources.pending_title
import cmp.navigation.navigation.HomeDestinationsScreen
import kpt.core.designsystem.icon.AppIcons
import org.jetbrains.compose.resources.stringResource

/**
 * The host for a dashboard destination whose FEATURE has not been migrated yet.
 *
 * ## Why this exists rather than hiding the entry
 *
 * The dashboard shell — four bottom-nav tabs and seven drawer entries — is itself a migrated
 * artifact, ported from `6b66e8a43`. Its SHAPE is what a field officer navigates by, and eight of
 * those eleven destinations have no feature module yet. Rendering only the three that work would
 * make the shipped dashboard quietly different from the app being replaced, and the difference
 * would live only in a migration document.
 *
 * This codebase's standing rule is "absent, never inert" — `ClientNavigation` passes `null` so a
 * row is not drawn rather than drawn dead. That rule is about never shipping a control that LOOKS
 * functional and is not. A row that states plainly that Centers has not been ported is not a dead
 * control; it is an accurate one, and it is the only option under which the dashboard is finished
 * at the end of this slice.
 *
 * ## What it must never become
 *
 * A fake. It renders NO data, offers NO action, and names the feature it is standing in for. When
 * the real feature lands, its route replaces this one in `HomeDestinationsScreen` /
 * `AuthenticatedNavBarTabItem` and this host loses one caller. When it loses its last caller, delete
 * it — a pending host with no pending features is itself a stub.
 *
 * Tracked in `idea-layer/state/CAPABILITY_GAPS.yaml` so the gap is machine-visible, not only
 * legible to whoever opens the drawer.
 */
/**
 * One LITERAL route per pending feature — no nav arguments.
 *
 * The bottom-nav tabs identify themselves by a plain `startDestinationRoute: String` (the
 * `NavigationItem` contract the navbar matches against), so the route has to be a stable literal
 * anyway. Parameterising it would mean reading the argument back out of `NavBackStackEntry` to
 * render a value the call site already had in hand.
 */
fun pendingFeatureRoute(featureName: String): String = "pending/$featureName"

fun NavController.navigateToPendingFeature(featureName: String) =
    navigate(pendingFeatureRoute(featureName))

/**
 * Register the host for ONE pending feature. The shell registers every pending tab and drawer entry.
 *
 * Plain `composable`, so the NavHost's own fade-through applies. That is right for BOTH callers:
 * the two pending TABS are sibling tab switches (a push would animate on top of the fade), and a
 * pending DRAWER entry is a jump across the drawer rather than a drill-down into a hierarchy.
 */
fun NavGraphBuilder.pendingFeatureDestination(featureName: String) {
    composable(route = pendingFeatureRoute(featureName)) {
        PendingFeatureScreen(featureName = featureName)
    }
}

@Composable
internal fun PendingFeatureScreen(
    featureName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag(PENDING_FEATURE_TAG),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = AppIcons.Info,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = stringResource(Res.string.pending_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            // Names the FEATURE. "Something went wrong" would be a lie; "coming soon" would be a
            // promise this file cannot keep. Stating what is missing is the only honest option.
            text = stringResource(Res.string.pending_message, featureName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/** Asserted by shell tests to prove a destination is pending rather than silently empty. */
internal const val PENDING_FEATURE_TAG: String = "shell:pending-feature"

/**
 * Every dashboard destination still waiting on its feature module — now the six unported DRAWER
 * entries only.
 *
 * The two pending TABS have left: `feature/center` and `feature/groups` landed and declare their own
 * `@FeatureTab`s, so Centers and Groups route to real screens. Nothing had to be removed from this
 * list by hand — it is DERIVED from [HomeDestinationsScreen.entries] filtered on `isMigrated`, and
 * the tab half was two named constants that went with their placeholders.
 *
 * `get()`, NOT a stored `val`. A top-level `val` here is initialized by THIS FILE's class
 * initializer, which [AuthenticatedNavBarTabItem]'s companion triggers by calling
 * [pendingFeatureRoute] — while this list reads that same companion's `CENTERS_FEATURE`. The two
 * initializers wait on each other and the first class touched dies with
 * `ExceptionInInitializerError`; on device it surfaced as a blank dashboard, in tests as three
 * unrelated failures. Computing on access removes the cycle, and the list is built once per read of
 * eight short strings — nothing worth caching into a deadlock.
 */
internal val PENDING_DESTINATIONS: List<String> get() =
    HomeDestinationsScreen.entries.filterNot { it.isMigrated }.map { it.featureName }
