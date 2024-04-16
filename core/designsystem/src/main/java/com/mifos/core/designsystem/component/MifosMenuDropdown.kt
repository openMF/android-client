package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.R

@Composable
fun MifosMenuDropDownItem(option: String, onClick: () -> Unit) {
    DropdownMenuItem(text = {
        Text(
            modifier = Modifier.padding(6.dp),
            text = option, style = TextStyle(
                fontSize = 17.sp
            )
        )
    }, onClick = { onClick() })
}