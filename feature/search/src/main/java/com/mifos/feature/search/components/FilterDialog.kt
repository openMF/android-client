package com.mifos.feature.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.mifos.core.ui.util.DevicePreviews
import com.mifos.feature.search.FilterOption
import com.mifos.feature.search.R
import com.mifos.feature.search.SearchScreenEvent

@Composable
internal fun FilterDialog(
    modifier: Modifier = Modifier,
    selected: FilterOption?,
    onEvent: (SearchScreenEvent.UpdateSelectedFilter) -> Unit,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.feature_search_filter))
        },
        text = {
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                FilterOption(
                    text = "All",
                    selected = selected == null,
                    onSelected = {
                        onEvent(SearchScreenEvent.UpdateSelectedFilter(null))
                        onDismiss()
                    }
                )

                HorizontalDivider()

                FilterOption.values.forEachIndexed { index, option ->
                    FilterOption(
                        text = option.label,
                        selected = option == selected,
                        onSelected = {
                            onEvent(SearchScreenEvent.UpdateSelectedFilter(option))
                            onDismiss()
                        }
                    )

                    if (index != FilterOption.values.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
internal fun FilterOption(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSelected()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onSelected()
            }
        )
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}

@DevicePreviews
@Composable
private fun FilterDialogPreview(
    modifier: Modifier = Modifier
) {
    FilterDialog(
        modifier = modifier,
        selected = null,
        onEvent = {},
        onDismiss = {}
    )
}

@DevicePreviews
@Composable
private fun FilterOptionPreview(
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        FilterOption(
            modifier = modifier,
            text = "Search Option",
            selected = true,
            onSelected = {}
        )
    }
}