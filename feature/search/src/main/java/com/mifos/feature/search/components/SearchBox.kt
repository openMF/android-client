package com.mifos.feature.search.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.ui.util.DevicePreviews
import com.mifos.feature.search.FilterOption
import com.mifos.feature.search.R
import com.mifos.feature.search.SearchScreenEvent
import com.mifos.feature.search.SearchScreenState

@Composable
internal fun SearchBox(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    onEvent: (SearchScreenEvent) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title And Filter Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.feature_search_title),
                    fontSize = 24.sp
                )

                AssistChip(
                    onClick = {
                        showDialog = true
                    },
                    label = {
                        Text(
                            text = state.selectedFilter?.label ?: "All",
                            fontSize = 16.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "filterIcon"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "changeFilter"
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors().copy(
                        leadingIconContentColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }

            // Search Text Field
            MifosOutlinedTextField(
                value = state.searchText,
                onValueChange = {
                    onEvent(SearchScreenEvent.UpdateSearchText(it))
                },
                leadingIcon = Icons.Default.Search,
                label = stringResource(id = R.string.feature_search_search_hint),
                showClearIcon = state.searchText.isNotEmpty(),
                onClickClearIcon = {
                    onEvent(SearchScreenEvent.ClearSearchText)
                },
                maxLines = 1,
            )

            // Search Button
            Button(
                onClick = {
                    onEvent(SearchScreenEvent.PerformSearch)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "searchIcon"
                )

                Text(
                    text = stringResource(id = R.string.feature_search_title),
                    fontSize = 16.sp
                )
            }

            // Exact Match Checkbox
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onEvent(SearchScreenEvent.UpdateExactMatch)
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
            ) {
                Checkbox(
                    checked = state.exactMatch ?: false,
                    onCheckedChange = {
                        onEvent(SearchScreenEvent.UpdateExactMatch)
                    },
                    modifier = Modifier
                        .size(20.dp)
                )

                Text(
                    text = stringResource(id = R.string.feature_search_exact_match),
                    fontSize = 16.sp
                )
            }
        }
    }

    AnimatedVisibility(
        visible = showDialog
    ) {
        FilterDialog(
            selected = state.selectedFilter,
            onEvent = onEvent,
            onDismiss = {
                showDialog = false
            }
        )
    }
}


@DevicePreviews
@Composable
private fun SearchBoxPreview() {
    SearchBox(
        modifier = Modifier.background(Color.White),
        state = SearchScreenState(),
        onEvent = {}
    )
}


@DevicePreviews
@Composable
private fun SearchBoxWithValuesPreview() {
    SearchBox(
        modifier = Modifier.background(Color.White),
        state = SearchScreenState(
            searchText = "search text",
            selectedFilter = FilterOption.Groups,
            exactMatch = true
        ),
        onEvent = {}
    )
}