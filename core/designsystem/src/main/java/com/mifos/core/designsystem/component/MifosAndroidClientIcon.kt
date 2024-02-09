package com.mifos.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MifosAndroidClientIcon(id: Int) {

    Image(
        painter = painterResource(id = id),
        contentDescription = null,
        modifier = Modifier
            .size(200.dp,100.dp)
    )
}