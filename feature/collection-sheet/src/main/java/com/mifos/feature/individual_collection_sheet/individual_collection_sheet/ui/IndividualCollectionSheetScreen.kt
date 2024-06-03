@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.mifos.feature.individual_collection_sheet.individual_collection_sheet.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTabRow
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.core.designsystem.utility.TabContent
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.feature.collection_sheet.R
import com.mifos.feature.individual_collection_sheet.new_individual_collection_sheet.ui.NewIndividualCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.saved_individual_collection_sheet.ui.SavedIndividualCollectionSheetCompose

@Composable
fun IndividualCollectionSheetScreen(
    onBackPressed: () -> Unit,
    onDetail: (String, IndividualCollectionSheet) -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    val pagerState = rememberPagerState(
        pageCount = { IndividualCollectionSheetScreenContents.entries.size }
    )

    val tabContents = listOf(
        TabContent(IndividualCollectionSheetScreenContents.NEW.name) {
            NewIndividualCollectionSheetScreen(onDetail = onDetail)
        },
        TabContent(IndividualCollectionSheetScreenContents.SAVED.name) {
            SavedIndividualCollectionSheetCompose()
        }
    )

    MifosScaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
                navigationIcon = {
                    IconButton(
                        onClick = { onBackPressed() },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null,
                            tint = Black,
                        )
                    }

                },
                title = {
                    Text(
                        text = stringResource(id = R.string.feature_collection_sheet_individual_collection_sheet),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Black,
                        textAlign = TextAlign.Start
                    )

                }
            )
        },
        snackbarHostState = snackbarHostState,
        bottomBar = { }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            MifosTabRow(tabContents = tabContents, pagerState = pagerState)
        }
    }
}

enum class IndividualCollectionSheetScreenContents {
    NEW,
    SAVED
}

@Preview(showBackground = true)
@Composable
private fun IndividualCollectionSheetScreenPreview() {
    IndividualCollectionSheetScreen(onBackPressed = {}, onDetail = { _, _ -> })
}