package cmp.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SharedApp(
    modifier: Modifier = Modifier,
) {
    Box(modifier=modifier.fillMaxSize())
    {
        Text("Hello",modifier=Modifier.align(Alignment.Center))
    }
}
