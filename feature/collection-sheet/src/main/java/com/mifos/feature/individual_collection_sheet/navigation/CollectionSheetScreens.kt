package com.mifos.feature.individual_collection_sheet.navigation

import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.db.CollectionSheet
import com.mifos.feature.individual_collection_sheet.generate_collection_sheet.GenerateCollectionSheetUiState

/**
 * Created by Pronay Sarker on 20/08/2024 (4:11 PM)
 */
sealed class CollectionSheetScreens(val route: String) {

    data object GenerateCollectionSheetScreen : CollectionSheetScreens("generate_collection_sheet_route")

    data object IndividualCollectionSheetScreen : CollectionSheetScreens("individual_collection_sheet_route")

    data object IndividualCollectionSheetDetailScreen : CollectionSheetScreens("individual_collection_sheet_detail/{${Constants.INDIVIDUAL_SHEET}}") {
        fun argument(sheet: IndividualCollectionSheet): String {
            val gson = Gson()
            val sheetInGsonString = gson.toJson(sheet)

            return "individual_collection_sheet_detail/$sheetInGsonString"
        }
    }
}