package com.mifos.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.designsystem.theme.BlueSecondary

@Composable
fun MifosFAB(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: Color = BlueSecondary,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = containerColor
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "MifosFab"
            )
        }
    }
}