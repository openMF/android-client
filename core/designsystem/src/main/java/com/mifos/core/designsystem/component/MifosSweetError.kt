package com.mifos.core.designsystem.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mifos.core.designsystem.R
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray

@Composable
fun MifosSweetError(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(18.dp)
        .semantics { contentDescription = "MifosSweetError" },
    message: String,
    isRetryEnabled: Boolean = true,
    buttonText: String = stringResource(id = R.string.core_designsystem_try_again),
    onclick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier.size(70.dp),
            model = R.drawable.core_designsystem_ic_error_black_24dp,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.core_designsystem_unable_to_load),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = DarkGray
            )
        )
        Text(
            text = message,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = DarkGray
            )
        )
        if (isRetryEnabled) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onclick() },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                )
            ) {
                Text(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    text = buttonText,
                    fontSize = 15.sp
                )
            }
        }
    }
}


@Composable
fun MifosPaginationSweetError(
    modifier: Modifier = Modifier,
    onclick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info Image",
            tint = Color.Gray
        )
        Text(
            text = stringResource(id = R.string.core_designsystem_unable_to_load),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = DarkGray
            )
        )
        Button(
            onClick = { onclick() },
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            )
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp),
                text = stringResource(id = R.string.core_designsystem_try_again),
                fontSize = 15.sp
            )
        }
    }
}