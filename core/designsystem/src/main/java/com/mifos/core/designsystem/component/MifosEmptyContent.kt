package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp


@Composable
fun MifosErrorContent(
    message: String,
    isRefreshEnabled: Boolean = false,
    imageVector: ImageVector? = null,
    onRefresh: () -> Unit = {},
    refreshButtonText: String = ""
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = imageVector ?: Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        if (isRefreshEnabled) {
            Text(text = message, modifier = Modifier.padding(vertical = 16.dp))
            Button(onClick = onRefresh) {
                Text(text = refreshButtonText)
            }
        }
    }
}