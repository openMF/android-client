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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.util.TextUtil
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosRowCard(
    modifier: Modifier = Modifier,
    title: String,
    leftValues: List<TextUtil>,
    rightValues: List<TextUtil>,
    onClick: () -> Unit,
) {
    MifosListingComponentOutline {
        Row(
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                PrintTextUtil(
                    TextUtil(
                        text = title,
                        style = MifosTypography.titleSmallEmphasized,
                    ),
                )
                leftValues.forEach {
                    PrintTextUtil(it)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
                ) {
                    rightValues.forEach {
                        PrintTextUtil(it)
                    }
                }
                Icon(
                    imageVector = MifosIcons.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.sizes.iconSmall),
                )
            }
        }
    }
}

@Composable
fun MifosRowCard(
    title: String,
    leftValues: List<TextUtil>,
    rightValues: List<TextUtil>,
    modifier: Modifier = Modifier,
    byteArray: ByteArray? = null,
    imageVector: DrawableResource? = null,
) {
    Row(
        modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageVector == null) {
            MifosUserImage(
                bitmap = byteArray,
                modifier = Modifier.size(DesignToken.sizes.iconExtraLarge),
            )
        } else {
            Icon(
                painter = painterResource(imageVector),
                modifier = Modifier
                    .size(DesignToken.sizes.iconExtraLarge)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceBright,
                        shape = CircleShape,
                    )
                    .padding(DesignToken.padding.small),
                contentDescription = null,
            )
        }
        Spacer(Modifier.width(DesignToken.padding.medium))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
        ) {
            PrintTextUtil(
                TextUtil(
                    text = title,
                    style = MifosTypography.titleSmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            leftValues.forEach {
                PrintTextUtil(
                    it,
                )
            }
        }
        Spacer(Modifier.width(DesignToken.padding.medium))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
                horizontalAlignment = Alignment.End,
            ) {
                rightValues.forEach {
                    PrintTextUtil(
                        it,
                    )
                }
            }
            Icon(
                imageVector = MifosIcons.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.sizes.iconMiny),
            )
        }
    }
}

@Composable
fun MifosRowTextWithButton(
    title: String,
    description: String,
    size: String,
    btnText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosListingComponentOutline {
        Row(modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                Text(
                    text = title,
                    style = MifosTypography.labelSmallEmphasized,
                )
                Text(
                    text = description,
                    style = MifosTypography.bodySmallEmphasized,
                )
                Text(
                    text = size,
                    style = MifosTypography.labelSmall,
                )
            }
            MifosButton(
                onClick = onClick,
            ) {
                Text(
                    text = btnText,
                    style = MifosTypography.labelMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosRowWithLabels(
    modifier: Modifier = Modifier,
    byteArray: ByteArray?,
    title: String,
    subTitle: String,
    label1: TextUtil,
    label2: TextUtil,
    onClick: () -> Unit,
) {
    MifosListingComponentOutline {
        Row(
            modifier
                .fillMaxWidth()
                .padding(DesignToken.padding.extraSmall)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MifosUserImage(
                bitmap = byteArray,
                modifier = Modifier.size(DesignToken.sizes.iconMedium),
            )
            Row(
                modifier.padding(start = DesignToken.padding.small).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
                ) {
                    PrintTextUtil(
                        TextUtil(
                            text = title,
                            style = MifosTypography.titleSmallEmphasized,
                        ),
                    )
                    PrintTextUtil(
                        TextUtil(
                            text = subTitle,
                            style = MifosTypography.bodySmall,
                        ),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
                    ) {
                        PrintTextUtil(label1)
                        PrintTextUtil(label2)
                    }
                    Icon(
                        imageVector = MifosIcons.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    )
                }
            }
        }
    }
}

@Composable
fun MifosDropDownRow(
    isActive: Boolean,
    title: String,
    accountNo: String,
    office: String,
    date: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(DesignToken.padding.extraSmall)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MifosTextUserImage(
            text = if (isActive)"Active" else "Closed",
            color = if (isActive) AppColors.customEnable else AppColors.lightRed,
            modifier = Modifier.size(DesignToken.sizes.iconExtraLarge),
        )
        Row(
            modifier
                .fillMaxWidth()
                .padding(start = DesignToken.padding.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                PrintTextUtil(
                    TextUtil(
                        text = title,
                        style = MifosTypography.titleSmallEmphasized,
                    ),
                )
                PrintTextUtil(
                    TextUtil(
                        text = accountNo,
                        style = MifosTypography.bodySmall,
                    ),
                )
                PrintTextUtil(
                    TextUtil(
                        text = office,
                        style = MifosTypography.bodySmall,
                    ),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrintTextUtil(
                    TextUtil(
                        text = date,
                        style = MifosTypography.labelSmall,
                    ),
                )
                Icon(
                    imageVector = MifosIcons.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.sizes.iconSmall),
                )
            }
        }
    }
}

@Preview
@Composable
fun MifosRowTextWithButton() {
    MifosTheme {
        MifosRowTextWithButton(
            title = "Title",
            description = "Description",
            size = "Size",
            btnText = "Button",
            onClick = {},
        )
    }
}

@Preview
@Composable
fun PreviewMifosRowCard() {
    MifosTheme {
        MifosRowCard(
            title = "Customer Info",
            leftValues = listOf(
                TextUtil(text = "Name", style = MifosTypography.labelMediumEmphasized),
                TextUtil(text = "Age", style = MifosTypography.labelMedium),
                TextUtil(text = "City", style = MifosTypography.labelMedium),
            ),
            rightValues = listOf(
                TextUtil(text = "453", style = MifosTypography.labelMedium, color = Color.Green),
                TextUtil(text = "455", style = MifosTypography.labelMedium),
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
fun PreviewMifosRowCardWithImage() {
    MifosTheme {
        MifosRowCard(
            title = "Customer Info",
            leftValues = listOf(
                TextUtil(text = "Name", style = MifosTypography.labelMediumEmphasized),
                TextUtil(text = "Age", style = MifosTypography.labelMedium),
                TextUtil(text = "City", style = MifosTypography.labelMedium),
            ),
            rightValues = listOf(
                TextUtil(text = "Closed", style = MifosTypography.labelMedium, color = Color.Red),
                TextUtil(text = "455", style = MifosTypography.labelMedium),
            ),
            byteArray = null,
        )
    }
}

@Preview
@Composable
fun MifosRowWithLabelsPreview() {
    MifosTheme {
        MifosRowWithLabels(
            title = "Title",
            subTitle = "Sub Title",
            label1 = TextUtil("label1", color = Color.Green),
            label2 = TextUtil("label2"),
            onClick = {},
            byteArray = null,
        )
    }
}

@Preview
@Composable
fun MifosRowDropDownPreview() {
    MifosTheme {
        MifosDropDownRow(
            isActive = true,
            title = "John Wault",
            accountNo = "Acc No. 838272",
            office = "Head Office",
            date = "20-04-2020",
            onClick = {},
        )
    }
}
