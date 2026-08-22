/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.designsystem.component.AppCard
import kpt.core.base.designsystem.component.HeroCard
import kpt.core.base.ui.AppInfo
import kpt.core.designsystem.component.SectionHeader
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.spacing
import kpt.feature.home.generated.resources.Res
import kpt.feature.home.generated.resources.screens_home_feature_centers_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_centers_title
import kpt.feature.home.generated.resources.screens_home_feature_checker_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_checker_title
import kpt.feature.home.generated.resources.screens_home_feature_clients_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_clients_title
import kpt.feature.home.generated.resources.screens_home_feature_collection_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_collection_title
import kpt.feature.home.generated.resources.screens_home_feature_groups_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_loans_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_loans_title
import kpt.feature.home.generated.resources.screens_home_feature_groups_title
import kpt.feature.home.generated.resources.screens_home_feature_open_cd
import kpt.feature.home.generated.resources.screens_home_feature_pathtracking_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_pathtracking_title
import kpt.feature.home.generated.resources.screens_home_feature_search_subtitle
import kpt.feature.home.generated.resources.screens_home_feature_search_title
import kpt.feature.home.generated.resources.screens_home_hero_greeting
import kpt.feature.home.generated.resources.screens_home_hero_subtitle
import kpt.feature.home.generated.resources.screens_home_section_fieldwork
import kpt.feature.home.generated.resources.screens_home_section_manage
import kpt.feature.home.ui.HomeAction
import kpt.feature.home.ui.HomeEvent
import kpt.feature.home.ui.HomeFeature
import kpt.feature.home.ui.HomeSection
import kpt.feature.home.ui.HomeViewModel
import kpt.feature.home.ui.TestTags
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinNavViewModel as retainedKoinViewModel

/**
 * The field-officer home board — the default body for the home tab.
 *
 * Rendered by `cmp-navigation`'s fork-owned `BackboneRegistry.homeBody` seam (public, not
 * `internal`, so the navigation module can reach it). Shows a hero greeting and two bands of
 * navigable entry-point cards for the officer's real features: portfolio management (clients,
 * groups, centers) and daily field work (collection sheet, checker inbox, path tracking,
 * search). Every card is a pure navigation affordance — tapping it dispatches
 * [HomeAction.FeatureClicked], the ViewModel emits a [HomeEvent.NavigateTo], and this composable
 * forwards the target to [onNavigate], which the navigation layer wires to the real destination.
 *
 * `onNavigate` defaults to a no-op so the board renders standalone (previews / the shell before
 * the navigation layer wires it); production always supplies a real router.
 */
@Composable
fun HomeDashboard(
    onNavigate: (HomeFeature) -> Unit = {},
    viewModel: HomeViewModel = retainedKoinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    // One-shot navigation events flow off the ViewModel's event channel (BaseViewModel<S,E,A>).
    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is HomeEvent.NavigateTo -> onNavigate(event.target)
            }
        }
    }

    val sp = MaterialTheme.spacing
    val manage = state.features.filter { it.section == HomeSection.MANAGE }
    val field = state.features.filter { it.section == HomeSection.FIELD }

    // `rememberScrollState()` internally uses `rememberSaveable` with `ScrollState.Saver`, so the
    // board's scroll position survives tab-switch (Navigation saveState/restoreState) and
    // config-change without any per-screen retention code.
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = sp.lg)
            .verticalScroll(scrollState)
            .testTag(TestTags.Home.DASHBOARD_SCROLL),
        verticalArrangement = Arrangement.spacedBy(sp.md),
    ) {
        Spacer(Modifier.height(sp.xs))

        HomeHeroHeader()

        SectionHeader(title = stringResource(Res.string.screens_home_section_manage))
        manage.forEach { feature ->
            FeatureTile(
                feature = feature,
                onClick = { viewModel.trySendAction(HomeAction.FeatureClicked(feature)) },
            )
        }

        SectionHeader(title = stringResource(Res.string.screens_home_section_fieldwork))
        field.forEach { feature ->
            FeatureTile(
                feature = feature,
                onClick = { viewModel.trySendAction(HomeAction.FeatureClicked(feature)) },
            )
        }

        Spacer(Modifier.height(sp.lg))
    }
}

/** Hero greeting card — the officer's name/app identity + a one-line orientation subtitle. */
@Composable
private fun HomeHeroHeader() {
    HeroCard {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
            Text(
                text = stringResource(Res.string.screens_home_hero_greeting),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                // App display name from the common AppInfo accessor (BuildKonfig → fork.properties),
                // never a hardcoded brand string.
                text = AppInfo.appDisplayName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = stringResource(Res.string.screens_home_hero_subtitle),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * One navigable entry-point card. Renders the feature's icon in a tinted circle, its title +
 * subtitle, and a trailing chevron. The whole card is clickable and tagged for UI tests.
 */
@Composable
private fun FeatureTile(feature: HomeFeature, onClick: () -> Unit) {
    val presentation = feature.presentation()
    val title = stringResource(presentation.title)
    AppCard(
        modifier = Modifier
            .testTag(TestTags.Home.featureTile(feature))
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(MaterialTheme.spacing.lg),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = presentation.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = stringResource(presentation.subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = AppIcons.ChevronRight,
                contentDescription = stringResource(Res.string.screens_home_feature_open_cd, title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** UI presentation (icon + copy) for a feature tile — resolved in the view layer, off state. */
private data class FeaturePresentation(
    val icon: ImageVector,
    val title: StringResource,
    val subtitle: StringResource,
)

private fun HomeFeature.presentation(): FeaturePresentation = when (this) {
    HomeFeature.CLIENTS -> FeaturePresentation(
        icon = AppIcons.Person,
        title = Res.string.screens_home_feature_clients_title,
        subtitle = Res.string.screens_home_feature_clients_subtitle,
    )
    HomeFeature.GROUPS -> FeaturePresentation(
        icon = AppIcons.Contact,
        title = Res.string.screens_home_feature_groups_title,
        subtitle = Res.string.screens_home_feature_groups_subtitle,
    )
    HomeFeature.CENTERS -> FeaturePresentation(
        icon = AppIcons.Bank,
        title = Res.string.screens_home_feature_centers_title,
        subtitle = Res.string.screens_home_feature_centers_subtitle,
    )
    HomeFeature.COLLECTION_SHEET -> FeaturePresentation(
        icon = Icons.AutoMirrored.Filled.ReceiptLong,
        title = Res.string.screens_home_feature_collection_title,
        subtitle = Res.string.screens_home_feature_collection_subtitle,
    )
    HomeFeature.CHECKER_INBOX -> FeaturePresentation(
        icon = AppIcons.OutlinedDoneAll,
        title = Res.string.screens_home_feature_checker_title,
        subtitle = Res.string.screens_home_feature_checker_subtitle,
    )
    HomeFeature.PATH_TRACKING -> FeaturePresentation(
        icon = Icons.Filled.LocationOn,
        title = Res.string.screens_home_feature_pathtracking_title,
        subtitle = Res.string.screens_home_feature_pathtracking_subtitle,
    )
    HomeFeature.SEARCH -> FeaturePresentation(
        icon = AppIcons.Search,
        title = Res.string.screens_home_feature_search_title,
        subtitle = Res.string.screens_home_feature_search_subtitle,
    )
    HomeFeature.LOANS -> FeaturePresentation(
        icon = Icons.Filled.AccountBalanceWallet,
        title = Res.string.screens_home_feature_loans_title,
        subtitle = Res.string.screens_home_feature_loans_subtitle,
    )
}
