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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cmp.navigation.authenticated.NavigationItem
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.component.MifosNavigationBarItem
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import core.designsystem.generated.resources.Res
import core.designsystem.generated.resources.core_designsystem_app_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifos.navigation.generated.resources.powered_by
import org.mifos.navigation.generated.resources.topbarlogo

@Composable
fun MifosBottomBar(
    navigationItems: List<NavigationItem>,
    selectedItem: NavigationItem?,
    onClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) {
    BottomAppBar(
        windowInsets = windowInsets,
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.customWhite),
        tonalElevation = 0.dp,
    ) {
        navigationItems.forEach { navigationItem ->
            MifosNavigationBarItem(
                contentDescriptionRes = navigationItem.contentDescriptionRes,
                selectedIconRes = navigationItem.iconResSelected,
                unselectedIconRes = navigationItem.iconRes,
                label = navigationItem.labelRes,
                isSelected = selectedItem == navigationItem,
                onClick = { onClick(navigationItem) },
                modifier = Modifier.testTag(tag = navigationItem.testTag),
            )
        }
    }
}

@Composable
fun MifosBottomBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) {
    Box(
        modifier = Modifier.dropShadow(
            shape = DesignToken.shapes.bottomSheet,
            shadow = Shadow(
                radius = 10.dp,
                spread = 5.dp,
                color = Color.Gray.copy(alpha = 0.40f),
                offset = DpOffset(x = 4.dp, 4.dp),
            ),
        )
    ) {
        BottomAppBar(
            windowInsets = windowInsets,
            modifier = modifier.fillMaxWidth().height(DesignToken.sizes.bottomAppBarHeight)
                .clip(
                    DesignToken.shapes.bottomSheet,
                ),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
            Box(
                modifier.fillMaxSize().padding(DesignToken.padding.small),
            ) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(org.mifos.navigation.generated.resources.Res.string.powered_by) +" ",
                        style = MifosTypography.tag,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Image(
                        painter = painterResource(org.mifos.navigation.generated.resources.Res.drawable.topbarlogo),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.primary,
                            blendMode = BlendMode.SrcIn,
                        ),
                        modifier = Modifier.size(DesignToken.sizes.iconMiny),
                    )

                    Text(
                        text = " " + stringResource(Res.string.core_designsystem_app_title),
                        style = MifosTypography.tag,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

    }
}