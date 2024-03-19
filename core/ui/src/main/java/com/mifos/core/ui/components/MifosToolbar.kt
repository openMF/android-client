package com.mifos.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mifos.core.designsystem.theme.BlueSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionModeTopAppBar(
    modifier: Modifier = Modifier,
    itemCount: Int,
    syncClicked: () -> Unit,
    resetSelectionMode: () -> Unit,
    containerColor: Color = BlueSecondary,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor
        ),
        title = {
            Text(text = "$itemCount selected")
        },
        navigationIcon = {
            IconButton(
                onClick = resetSelectionMode,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "reset selection",
                )
            }
        },
        actions = {
            FilledTonalButton(
                onClick = {
                    syncClicked()
                    resetSelectionMode()
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sync,
                    contentDescription = "Sync Items",
                )
                Text(text = "Sync")
            }
        }
    )
}