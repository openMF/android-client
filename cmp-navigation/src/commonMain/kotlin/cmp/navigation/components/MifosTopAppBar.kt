/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import core.designsystem.generated.resources.Res
import core.designsystem.generated.resources.core_designsystem_app_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.mifos.navigation.generated.resources.topbarlogo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosTopAppBar(
    onNavigationIconClick: () -> Unit,
    onSearchIconClick: () -> Unit,
    onNotificationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        shape = RoundedCornerShape(
            bottomStart = DesignToken.spacing.large,
            bottomEnd = DesignToken.spacing.large,
        ),
        elevation = DesignToken.elevation.appBar,
        modifier = Modifier.fillMaxWidth(),
    ) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(org.mifos.navigation.generated.resources.Res.drawable.topbarlogo),
                        contentDescription = "Mifos Logo",
                    )
                    Spacer(Modifier.width(DesignToken.spacing.mediumSmall))
                    Text(
                        text = stringResource(Res.string.core_designsystem_app_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.titleColorTopAbbBar,
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = { onNavigationIconClick() },
                ) {
                    Icon(
                        imageVector = MifosIcons.Menu,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSearchIconClick() }) {
                    Icon(MifosIcons.Search, contentDescription = "Search")
                }
                IconButton(onClick = { onNotificationIconClick() }) {
                    Icon(MifosIcons.Notifications, contentDescription = "Notifications")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            expandedHeight = DesignToken.sizes.topAppBarHeight,
        )
    }
}
