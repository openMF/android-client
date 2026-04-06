/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun LegendToggleItem(
    legend: ChartLegendItem,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { legend.onToggle() }
            .padding(horizontal = DesignToken.padding.small, vertical = DesignToken.padding.extraSmall),
    ) {
        Box(
            modifier = Modifier
                .size(DesignToken.sizes.boxDp12)
                .background(
                    color = if (legend.isVisible) legend.color else Color.LightGray,
                    shape = DesignToken.shapes.circle,
                ),
        )
        Spacer(modifier = Modifier.width(DesignToken.padding.small))
        Text(
            text = stringResource(legend.title),
            style = MifosTypography.labelMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
            textDecoration = if (legend.isVisible) TextDecoration.None else TextDecoration.LineThrough,
        )
    }
}

@Composable
fun StackedBarChart(
    bars: List<StackedBarItem>,
    graphMaxY: Float,
    modifier: Modifier = Modifier,
    chartHeight: Dp = DesignToken.sizes.chartHeight,
) {
    val safeMaxY = graphMaxY.takeIf { it > 0f } ?: 0f
    val ySteps = (0..4).map { safeMaxY * (it / 4f) }

    var activeTooltip by remember { mutableStateOf<TooltipInfo?>(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .height(chartHeight)
                .padding(end = DesignToken.padding.small, bottom = DesignToken.spacing.dp40),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
        ) {
            ySteps.reversed().forEach { step ->
                Text(
                    text = formatGraphValue(step).substringBefore('.'),
                    style = MifosTypography.labelSmall,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.height(DesignToken.padding.large),
                )
            }
        }

        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .weight(1f)
                .height(chartHeight),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight - DesignToken.spacing.dp40),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                ySteps.forEach { _ ->
                    HorizontalDivider(color = AppColors.borderColorOne, thickness = DesignToken.strokes.thin)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(start = DesignToken.padding.medium, end = DesignToken.padding.large),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.largeMediumIncreased),
            ) {
                bars.forEachIndexed { barIndex, bar ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(DesignToken.sizes.imageDp40),
                    ) {
                        Column(
                            modifier = Modifier
                                .height(chartHeight - DesignToken.spacing.dp40)
                                .width(DesignToken.sizes.iconDp20),
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Column(
                                modifier = Modifier.clip(DesignToken.shapes.extraSmall),
                            ) {
                                val reversedSegments = bar.segments.reversed()
                                reversedSegments.forEachIndexed { segmentIndex, segment ->
                                    val segmentHeightRatio = if (safeMaxY == 0f) 0f else segment.value / safeMaxY
                                    val targetHeightDp = (chartHeight - DesignToken.spacing.dp40) * segmentHeightRatio

                                    val animatedHeightDp by animateDpAsState(
                                        targetValue = targetHeightDp,
                                        animationSpec = tween(durationMillis = 500),
                                        label = "barAnimation",
                                    )

                                    val isTooltipActive =
                                        activeTooltip?.barIndex == barIndex && activeTooltip?.segmentIndex == segmentIndex

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(animatedHeightDp)
                                            .background(segment.color)
                                            .clickable(
                                                enabled = segment.value > 0f,
                                            ) {
                                                activeTooltip = if (isTooltipActive) {
                                                    null
                                                } else {
                                                    TooltipInfo(
                                                        barIndex,
                                                        segmentIndex,
                                                        bar.xLabel,
                                                        segment.name,
                                                        segment.value,
                                                        segment.color,
                                                    )
                                                }
                                            },
                                    ) {
                                        if (isTooltipActive && segment.value > 0f) {
                                            val density = LocalDensity.current
                                            val offsetPx = with(density) { DesignToken.spacing.negativeDp20.roundToPx() }
                                            Popup(
                                                alignment = Alignment.TopCenter,
                                                offset = IntOffset(0, offsetPx),
                                                properties = PopupProperties(
                                                    dismissOnClickOutside = true,
                                                    focusable = true,
                                                ),
                                                onDismissRequest = { activeTooltip = null },
                                            ) {
                                                ChartTooltip(info = activeTooltip!!)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(DesignToken.padding.dp14))
                        Text(
                            text = bar.xLabel,
                            style = KptTheme.typography.labelSmall,
                            color = KptTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier
                                .wrapContentWidth(unbounded = true)
                                .rotate(-45f)
                                .offset(x = DesignToken.spacing.negativeDp8, y = DesignToken.spacing.negativeDp2),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChartTooltip(
    info: TooltipInfo,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = DesignToken.shapes.large,
        color = AppColors.borderColor,
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = modifier.padding(horizontal = DesignToken.padding.medium, vertical = DesignToken.padding.small),
        ) {
            Text(
                text = info.xLabel,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = KptTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(DesignToken.padding.extraSmall))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(DesignToken.padding.dp10)
                        .background(info.color, DesignToken.shapes.medium),
                )
                Spacer(modifier = Modifier.width(DesignToken.padding.dp6))
                Text(
                    text = "${info.segmentName}: ${formatGraphValue(info.segmentValue)}",
                    color = Color.White,
                    style = KptTheme.typography.bodySmall,
                )
            }
        }
    }
}

fun calculateNiceMax(maxValue: Float): Float {
    if (maxValue == 0f) return 100f
    val order = floor(log10(maxValue.toDouble())).toInt()
    val magnitude = 10.0.pow(order.toDouble())
    val normalized = maxValue / magnitude

    val step = when {
        normalized <= 1.0 -> 1.0
        normalized <= 2.0 -> 2.0
        normalized <= 5.0 -> 5.0
        else -> 10.0
    }
    return (step * magnitude).toFloat()
}

fun formatGraphValue(value: Float): String {
    val scaled = (value * 100).roundToInt()
    val isNegative = scaled < 0
    val absScaled = scaled.absoluteValue
    val whole = absScaled / 100
    val fraction = absScaled % 100
    val integerPart = whole.toString().reversed().chunked(3).joinToString(",").reversed()
    val prefix = if (isNegative) "-" else ""
    return "$prefix$integerPart.${fraction.toString().padStart(2, '0')}"
}

data class ChartLegendItem(
    val title: StringResource,
    val color: Color,
    val isVisible: Boolean,
    val onToggle: () -> Unit,
)

data class StackedBarItem(
    val xLabel: String,
    val segments: List<BarSegment>,
)

data class BarSegment(
    val name: String,
    val value: Float,
    val color: Color,
)

data class TooltipInfo(
    val barIndex: Int,
    val segmentIndex: Int,
    val xLabel: String,
    val segmentName: String,
    val segmentValue: Float,
    val color: Color,
)
