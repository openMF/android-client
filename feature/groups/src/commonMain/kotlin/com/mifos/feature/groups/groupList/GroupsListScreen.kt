/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.groupList

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.room.entities.group.GroupEntity
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal expect fun GroupsListRoute(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onAddGroupClick: () -> Unit,
    onGroupClick: (groupId: Int) -> Unit,
    viewModel: GroupsListViewModel = koinViewModel(),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    group: GroupEntity,
    doesSelected: Boolean,
    inSelectionMode: Boolean,
    onGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectItem: () -> Unit,
) {
    val borderStroke = if (doesSelected) {
        BorderStroke(DesignToken.strokes.thin, Color.Blue)
    } else {
        CardDefaults.outlinedCardBorder()
    }
    val containerColor = if (doesSelected) {
        KptTheme.colorScheme.secondaryContainer
    } else {
        Color.Unspecified
    }
    // TODO: replace primary with Green after we define Theme colours of mockups
    val indicatorColor = if (group.active == true) {
        KptTheme.colorScheme.primary
    } else {
        KptTheme.colorScheme.error
    }

    group.name?.let {
        OutlinedCard(
            modifier = modifier
                .testTag(it)
                .padding(KptTheme.spacing.sm)
                .fillMaxWidth()
                .clip(KptTheme.shapes.small)
                .combinedClickable(
                    onClick = {
                        if (inSelectionMode) {
                            onSelectItem()
                        } else {
                            onGroupClick()
                        }
                    },
                    onLongClick = onSelectItem,
                ),
            shape = KptTheme.shapes.small,
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor,
            ),
            border = borderStroke,
        ) {
            ListItem(
                leadingContent = {
                    Canvas(
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                        onDraw = {
                            drawCircle(
                                color = indicatorColor,
                            )
                        },
                    )
                },
                headlineContent = {
                    Text(text = it)
                },
                supportingContent =
                {
                    Text(text = group.accountNo ?: "")
                },
                overlineContent =
                {
                    Text(text = group.officeName ?: "")
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        if (group.sync) {
                            Icon(imageVector = MifosIcons.DoneAll, contentDescription = "Sync")
                        }

                        Icon(
                            imageVector = MifosIcons.ArrowForward,
                            contentDescription = "Arrow Forward Icon",
                        )
                    }
                },
            )
        }
    }
}
