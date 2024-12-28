/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.navigation

import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.dbobjects.collectionsheet.IndividualCollectionSheet

/**
 * Created by Pronay Sarker on 20/08/2024 (4:11 PM)
 */
sealed class CollectionSheetScreens(val route: String) {

    data object GenerateCollectionSheetScreen :
        CollectionSheetScreens("generate_collection_sheet_route")

    data object IndividualCollectionSheetScreen :
        CollectionSheetScreens("individual_collection_sheet_route")

    data object PaymentDetailsScreen :
        CollectionSheetScreens(
            "payment_details_route/{${Constants.ADAPTER_POSITION}}/" +
                "{${Constants.PAYLOAD}}/{${Constants.PAYMENT_LIST}}/{${Constants.LOAN_AND_CLIENT}}" +
                "/{${Constants.PAYMENT_OPTIONS}}/{${Constants.CLIENT_ID}}",
        ) {
        fun argument(
            position: Int,
            payload: String,
            paymentTypeOptionsName: String,
            loansAndClientName: String,
            paymentTypeOptions: String,
            clientId: Int,
        ): String {
            return "payment_details_route/$position/$payload/$paymentTypeOptionsName/$loansAndClientName/$paymentTypeOptions/$clientId"
        }
    }

    data object IndividualCollectionSheetDetailScreen :
        CollectionSheetScreens("individual_collection_sheet_detail/{${Constants.INDIVIDUAL_SHEET}}") {
        fun argument(sheet: IndividualCollectionSheet): String {
            val gson = Gson()
            val sheetInGsonString = gson.toJson(sheet)

            return "individual_collection_sheet_detail/$sheetInGsonString"
        }
    }
}
