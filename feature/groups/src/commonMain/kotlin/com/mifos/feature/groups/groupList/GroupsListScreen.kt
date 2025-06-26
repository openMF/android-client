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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import com.mifos.room.entities.group.GroupEntity
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal expect fun GroupsListRoute(
    paddingValues: PaddingValues,
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
        BorderStroke(1.dp, Color.Blue)
    } else {
        CardDefaults.outlinedCardBorder()
    }
    val containerColor = if (doesSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Unspecified
    }
    // TODO: replace primary with Green after we define Theme colours of mockups
    val indicatorColor = if (group.active == true) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    group.name?.let {
        OutlinedCard(
            modifier = modifier
                .testTag(it)
                .padding(8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
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
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor,
            ),
            border = borderStroke,
        ) {
            ListItem(
                leadingContent = {
                    Canvas(
                        modifier = Modifier.size(16.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
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
