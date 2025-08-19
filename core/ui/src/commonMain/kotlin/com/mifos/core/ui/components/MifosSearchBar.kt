/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = DesignToken.shapes.full,
            )
            .padding(horizontal = DesignToken.padding.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = MifosIcons.ChevronLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Enter Search Text Here",
                    style = MifosTypography.bodyMediumEmphasized,
                )
            },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Box(
            modifier = Modifier
                .padding(start = DesignToken.padding.small)
                .size(DesignToken.sizes.iconExtraLarge)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = { onSearchClick(query) }, modifier = Modifier.size(DesignToken.sizes.iconMedium)) {
                Icon(
                    imageVector = MifosIcons.Search,
                    contentDescription = "Search",
                    tint = AppColors.customWhite,
                )
            }
        }
    }
}

@Preview
@Composable
fun MifosSearchBarPreview() {
    var text by remember { mutableStateOf("") }

    MifosSearchBar(
        query = text,
        onQueryChange = { text = it },
        onBackClick = { },
        onSearchClick = { println("Searching for $it") },
    )
}
