package com.mifos.feature.client.clientSignature

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import com.mifos.core.designsystem.component.MifosDrawingCanvas

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun SignatureDrawingArea(
    drawColor: Color,
    drawBrush: Float
) {
    MifosDrawingCanvas(
        drawColor = drawColor,
        drawBrush = drawBrush,
    )
}