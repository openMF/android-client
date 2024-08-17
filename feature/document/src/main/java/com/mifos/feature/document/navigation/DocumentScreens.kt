package com.mifos.feature.document.navigation

import com.mifos.core.common.utils.Constants

/**
 * Created by Pronay Sarker on 17/08/2024 (4:00 AM)
 */
sealed class DocumentScreens(val route: String) {
    data object DocumentListScreen : DocumentScreens("document_list_screen/{${Constants.ENTITY_ID}}/{${Constants.ENTITY_TYPE}}") {
        fun argument(entityId : Int, entityType : String) = "document_list_screen/$entityId/$entityType"
    }
}
