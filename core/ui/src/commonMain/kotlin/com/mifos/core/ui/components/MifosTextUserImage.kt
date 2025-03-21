package com.mifos.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import kotlin.math.min

@Composable
fun MifosTextUserImage(
    text: String,
    modifier: Modifier = Modifier,
) {
    var boxSize by remember { mutableStateOf(Size.Zero) }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .onGloballyPositioned { coordinates ->
                boxSize = Size(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat(),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = with(LocalDensity.current) {
                (min(boxSize.width, boxSize.height) / 2).toSp()
            },
        )
    }
}

@DevicePreview
@Composable
fun MifosTextUserImagePreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosTextUserImage(
            text = "A",
            modifier = modifier,
        )
    }
}
