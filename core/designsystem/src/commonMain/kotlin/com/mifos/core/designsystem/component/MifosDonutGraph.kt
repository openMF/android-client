/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import kotlinx.coroutines.launch
import template.core.base.designsystem.theme.KptTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun MifosDonutGraph(
    segments: List<DonutSegment>,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = DesignToken.strokes.dp10,
    showLegends: Boolean = true,
) {
    var visibleByLabel by remember(segments) {
        mutableStateOf(segments.associate { it.label to true })
    }

    val onToggleSegment: (String) -> Unit = { label ->
        visibleByLabel = visibleByLabel.toMutableMap().also { map ->
            map[label] = !(map[label] ?: true)
        }
    }

    var activeTooltip by remember { mutableStateOf<DonutSegment?>(null) }
    val density = LocalDensity.current
    val popupOffsetY = with(density) { DesignToken.spacing.negativeDp20.roundToPx() }

    val scope = rememberCoroutineScope()

    val animatedVisibleValues = remember { LinkedHashMap<String, Animatable<Float, *>>() }

    val segmentLabels = segments.map { it.label }

    LaunchedEffect(segmentLabels) {
        visibleByLabel = buildMap {
            segmentLabels.forEach { label ->
                put(label, visibleByLabel[label] ?: true)
            }
        }
    }

    LaunchedEffect(segmentLabels) {
        val keys = segmentLabels.toSet()
        animatedVisibleValues.keys.toList().forEach { key ->
            if (key !in keys) animatedVisibleValues.remove(key)
        }
        segments.forEach { seg ->
            if (animatedVisibleValues[seg.label] == null) {
                val isVisible = visibleByLabel[seg.label] ?: true
                animatedVisibleValues[seg.label] = Animatable(if (isVisible) seg.value else 0f)
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { visibleByLabel to segments }
            .collect { latest ->
                val (visibility, segs) = latest
                segs.forEach { seg ->
                    val anim = animatedVisibleValues[seg.label] ?: return@forEach
                    val target = if (visibility[seg.label] != false) seg.value else 0f
                    scope.launch {
                        anim.animateTo(
                            targetValue = target,
                            animationSpec = tween(durationMillis = 450),
                        )
                    }
                }
            }
    }

    val segmentsForAngles = segments.map { seg ->
        val isVisible = visibleByLabel[seg.label] != false
        val v = animatedVisibleValues[seg.label]?.value ?: if (isVisible) seg.value else 0f
        seg.copy(value = v)
    }

    val visibleSegments = segmentsForAngles.filter { it.value > 0f }
    val total = visibleSegments.sumOf { it.value.toDouble() }.toFloat()
    val hasAnyVisibleSegment = visibleSegments.isNotEmpty() && total > 0f

    val segmentAngles = remember(visibleSegments, total) {
        computeSegmentAngles(
            segments = visibleSegments,
            total = total,
        )
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier.size(DesignToken.sizes.boxDp100),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier
                    .size(DesignToken.sizes.boxDp100)
                    .pointerInput(segmentAngles, strokeWidth) {
                        detectTapGestures { tapOffset ->
                            val tappedSegment = detectTappedSegment(
                                tapOffset = tapOffset,
                                sizeCenter = Offset(size.width / 2f, size.height / 2f),
                                sizeMinDimension = min(size.width, size.height).toFloat(),
                                strokeWidthPx = strokeWidth.toPx(),
                                segmentAngles = segmentAngles,
                            )

                            activeTooltip = when {
                                tappedSegment == null -> null
                                activeTooltip?.label == tappedSegment.label -> null
                                else -> tappedSegment
                            }
                        }
                    },
            ) {
                drawArc(
                    color = if (hasAnyVisibleSegment) backgroundColor else AppColors.borderColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt),
                )

                segmentAngles.forEach { segmentAngle ->
                    drawArc(
                        color = segmentAngle.segment.color,
                        startAngle = segmentAngle.startAngle,
                        sweepAngle = segmentAngle.sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt),
                    )
                }
            }

            activeTooltip?.let { tooltipInfo ->
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(0, popupOffsetY),
                    properties = PopupProperties(
                        dismissOnClickOutside = true,
                        focusable = true,
                    ),
                    onDismissRequest = { activeTooltip = null },
                ) {
                    DonutChartTooltip(segmentInfo = tooltipInfo)
                }
            }
        }

        if (showLegends) {
            Spacer(modifier = Modifier.height(DesignToken.padding.medium))
            Row(verticalAlignment = Alignment.CenterVertically) {
                segments.forEach { seg ->
                    DonutLegendToggleItem(
                        label = seg.label,
                        amount = seg.valueLabel,
                        color = seg.color,
                        isVisible = visibleByLabel[seg.label] != false,
                        onToggle = { onToggleSegment(seg.label) },
                    )
                }
            }
        }
    }
}

@Composable
fun DonutLegendToggleItem(
    label: String,
    amount: String,
    color: Color,
    isVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .clickable { onToggle() }
            .padding(
                horizontal = DesignToken.padding.small,
                vertical = DesignToken.padding.extraSmall,
            ),
    ) {
        Box(
            modifier = Modifier
                .padding(top = DesignToken.padding.extraSmall)
                .size(DesignToken.sizes.boxDp12)
                .background(
                    color = if (isVisible) color else Color.LightGray,
                    shape = DesignToken.shapes.circle,
                ),
        )
        Spacer(modifier = Modifier.width(DesignToken.padding.small))
        Column {
            Text(
                text = label,
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
                textDecoration = if (isVisible) TextDecoration.None else TextDecoration.LineThrough,
            )
            Spacer(modifier = Modifier.height(KptTheme.spacing.xs))
            Text(
                text = amount,
                style = KptTheme.typography.labelLarge,
                color = KptTheme.colorScheme.onSurface,
                textDecoration = if (isVisible) TextDecoration.None else TextDecoration.LineThrough,
            )
        }
    }
}

private fun detectTappedSegment(
    tapOffset: Offset,
    sizeCenter: Offset,
    sizeMinDimension: Float,
    strokeWidthPx: Float,
    segmentAngles: List<SegmentAngle>,
): DonutSegment? {
    val dx = tapOffset.x - sizeCenter.x
    val dy = tapOffset.y - sizeCenter.y
    val distance = sqrt((dx * dx) + (dy * dy))
    val outerRadius = sizeMinDimension / 2f
    val innerRadius = (outerRadius - strokeWidthPx).coerceAtLeast(0f)

    if (distance !in innerRadius..outerRadius) return null

    val angleFromXAxis = (atan2(dy, dx) * 180f / PI.toFloat())
    val angleFromTopClockwise = (angleFromXAxis + 90f + 360f) % 360f

    return segmentAngles
        .firstOrNull { it.contains(angleFromTopClockwise) }
        ?.segment
}

private fun computeSegmentAngles(
    segments: List<DonutSegment>,
    total: Float,
): List<SegmentAngle> {
    if (segments.isEmpty() || total <= 0f) return emptyList()

    var currentStart = -90f
    return segments.mapNotNull { segment ->
        val ratio = (segment.value / total).coerceIn(0f, 1f)
        val sweep = ratio * 360f
        if (sweep <= 0f) {
            null
        } else {
            val start = currentStart
            currentStart += sweep
            SegmentAngle(
                segment = segment,
                startAngle = start,
                sweepAngle = sweep,
            )
        }
    }
}

private data class SegmentAngle(
    val segment: DonutSegment,
    val startAngle: Float,
    val sweepAngle: Float,
) {
    val startAngleNormalized: Float
        get() = ((startAngle + 90f) % 360f + 360f) % 360f

    val endAngleNormalized: Float
        get() = ((startAngle + sweepAngle + 90f) % 360f + 360f) % 360f

    fun contains(angleFromTopClockwise: Float): Boolean {
        val start = startAngleNormalized
        val end = endAngleNormalized
        return if (end >= start) {
            angleFromTopClockwise in start..end
        } else {
            angleFromTopClockwise >= start || angleFromTopClockwise <= end
        }
    }
}

@Composable
private fun DonutChartTooltip(
    segmentInfo: DonutSegment,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = DesignToken.shapes.large,
        color = AppColors.borderColor,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(
                horizontal = DesignToken.padding.medium,
                vertical = DesignToken.padding.small,
            ),
        ) {
            Box(
                modifier = Modifier
                    .size(DesignToken.padding.dp10)
                    .background(segmentInfo.color, DesignToken.shapes.medium),
            )
            Spacer(modifier = Modifier.width(DesignToken.padding.dp6))
            Text(
                text = "${segmentInfo.label}: ${segmentInfo.valueLabel}",
                style = KptTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
    }
}

data class DonutSegment(
    val label: String,
    val valueLabel: String,
    val value: Float,
    val color: Color,
)
