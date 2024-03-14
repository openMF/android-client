package com.mifos.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.designsystem.theme.BlueSecondary

@Composable
fun MifosFAB(
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: Color = BlueSecondary,
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