/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.center.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.screen.ScreenContent
import kpt.core.model.center.Center
import kpt.core.model.group.Group
import kpt.core.designsystem.component.AppButton
import kpt.core.designsystem.icon.AppIcons
import kpt.feature.center.generated.resources.Res
import kpt.feature.center.generated.resources.feature_center_account_no
import kpt.feature.center.generated.resources.feature_center_activate
import kpt.feature.center.generated.resources.feature_center_back
import kpt.feature.center.generated.resources.feature_center_detail_title
import kpt.feature.center.generated.resources.feature_center_external_id
import kpt.feature.center.generated.resources.feature_center_groups
import kpt.feature.center.generated.resources.feature_center_office
import kpt.feature.center.generated.resources.feature_center_staff
import kpt.feature.center.generated.resources.feature_center_status_active
import kpt.feature.center.generated.resources.feature_center_status_inactive
import kpt.feature.center.generated.resources.feature_center_unnamed
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

internal object CenterDetailTestTags {
    const val ACTIVATE = "center-detail:activate"
    const val SCREEN = "center:detail"
    const val GROUPS = "center:detail:groups"
    const val GROUP_ROW = "center:detail:group-row"
}

@Composable
internal fun CenterDetailScreen(
    onBackClick: () -> Unit,
    onGroupClick: ((Int) -> Unit)?,
    modifier: Modifier = Modifier,
    // Activation is the shared `feature/activate` screen, supplied by the centre graph.
    onActivate: ((Int) -> Unit)? = null,
    viewModel: CenterDetailViewModel = koinViewModel(),
) {
    // Two independent flows — a failure in one must not blank the other.
    val center by viewModel.center.collectAsStateWithLifecycle()
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val canActivate by viewModel.canActivate.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is CenterDetailEvent.OpenGroup -> onGroupClick?.invoke(event.groupId)
                CenterDetailEvent.NavigateBack -> onBackClick()
                is CenterDetailEvent.Activate -> onActivate?.invoke(event.centerId)
            }
        }
    }

    CenterDetailContent(
        center = center,
        groups = groups,
        onAction = { viewModel.actionChannel.trySend(it) },
        // The button needs BOTH an inactive centre and somewhere to send it.
        canActivate = canActivate && onActivate != null,
        groupsClickable = onGroupClick != null,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CenterDetailContent(
    center: ScreenState<Center>,
    groups: ScreenState<List<Group>>,
    onAction: (CenterDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    groupsClickable: Boolean = true,
    canActivate: Boolean = false,
) {
    Scaffold(
        modifier = modifier.fillMaxSize().testTag(CenterDetailTestTags.SCREEN),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.feature_center_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(CenterDetailAction.BackClicked) }) {
                        Icon(
                            AppIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.feature_center_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // The center's own fields and its groups carry SEPARATE ScreenStates and render
            // independently: a groups read that fails must not blank out the center's details, which
            // are already on screen and still true. That independence is the whole reason the two
            // original screens could be merged without making one failure hide both halves.
            ScreenContent(
                state = center,
                onRetry = { },
                modifier = Modifier.fillMaxWidth(),
            ) { loaded, _ ->
                CenterFields(loaded)
            }

            // Drawn only where it can succeed — an already-active centre has no Activate button,
            // and neither does one with nowhere to send the tap.
            if (canActivate) {
                AppButton(
                    onClick = { onAction(CenterDetailAction.ActivateClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CenterDetailTestTags.ACTIVATE),
                ) {
                    Text(stringResource(Res.string.feature_center_activate))
                }
            }

            Text(
                text = stringResource(Res.string.feature_center_groups),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp),
            )

            ScreenContent(
                state = groups,
                onRetry = { },
                modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp).testTag(CenterDetailTestTags.GROUPS),
            ) { rows, _ ->
                LazyColumn {
                    items(rows) { group ->
                        // See CenterListScreen — nullable cross-module property, no smart cast.
                        val groupId = group.id
                        GroupRow(
                            group = group,
                            onClick = if (groupsClickable && groupId != null) {
                                { onAction(CenterDetailAction.GroupClicked(groupId)) }
                            } else {
                                null
                            },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterFields(center: Center) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = center.name ?: stringResource(Res.string.feature_center_unnamed),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(
                if (center.active == true) {
                    Res.string.feature_center_status_active
                } else {
                    Res.string.feature_center_status_inactive
                },
            ),
            style = MaterialTheme.typography.labelLarge,
        )
        center.accountNo?.let { Field(stringResource(Res.string.feature_center_account_no), it) }
        center.officeName?.let { Field(stringResource(Res.string.feature_center_office), it) }
        center.staffName?.let { Field(stringResource(Res.string.feature_center_staff), it) }
        center.externalId?.let { Field(stringResource(Res.string.feature_center_external_id), it) }
    }
}

@Composable
private fun Field(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun GroupRow(group: Group, onClick: (() -> Unit)?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp)
            .testTag(CenterDetailTestTags.GROUP_ROW),
    ) {
        Text(text = group.name.orEmpty(), style = MaterialTheme.typography.bodyLarge)
        group.accountNo?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
    }
}
