package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.theme.DarkGray

/**
 * Created by Aditya Gupta on 21/02/24.
 */

@Composable
fun MifosPagingAppendProgress() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .padding(8.dp),
            strokeWidth = 4.dp
        )
    }

}

@Composable
fun MifosCircularProgress(
    contentDesc: String = "loadingIndicator"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = contentDesc },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .testTag("loadingWheel")
                .width(60.dp)
                .height(60.dp)
                .padding(8.dp),
            strokeWidth = 4.dp,
            color = DarkGray
        )
    }

}