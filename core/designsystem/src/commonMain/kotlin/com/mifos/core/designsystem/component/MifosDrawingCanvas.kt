/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import com.mifos.core.designsystem.utils.PathState

@ExperimentalComposeUiApi
@Composable
fun MifosDrawingCanvas(
    drawColor: Color,
    drawBrush: Float,
    modifier: Modifier = Modifier,
    drawingState: DrawingState = remember { DrawingState() },
) {
    val movePath = remember { mutableStateOf<Offset?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        drawingState.updateCurrentPath { path ->
                            path.moveTo(it.x, it.y)
                        }
                        drawingState.addUsedColor(drawColor)
                        drawingState.addPath(PathState(drawingState.currentPath.value, drawColor, drawBrush))
                    }

                    MotionEvent.ACTION_MOVE -> {
                        movePath.value = Offset(it.x, it.y)
                    }

                    else -> {
                        movePath.value = null
                        drawingState.resetCurrentPath()
                    }
                }
                true
            },
    ) {
        movePath.value?.let {
            drawingState.updateCurrentPath { path ->
                path.lineTo(it.x, it.y)
            }
            drawPath(
                path = drawingState.currentPath.value,
                color = drawColor,
                style = Stroke(drawBrush),
            )
        }

        drawingState.paths.forEach {
            drawPath(
                path = it.path,
                color = it.color,
                style = Stroke(it.stroke),
            )
        }
    }
}
