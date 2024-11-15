/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.navigation

import com.mifos.core.common.utils.Constants

sealed class ClientScreens(val route: String) {

    data object ClientListScreen : ClientScreens("client_list_screen")

    data object ClientDetailScreen :
        ClientScreens("client_detail_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId: Int) = "client_detail_screen/$clientId"
    }

    data object ClientChargesScreen :
        ClientScreens("client_charges_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId: Int) = "client_charges_screen/$clientId"
    }

    data object ClientIdentifierScreen :
        ClientScreens("client_identifier_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId: Int) = "client_identifier_screen/$clientId"
    }

    data object ClientPinPointScreen :
        ClientScreens("client_pin_point_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId: Int) = "client_pin_point_screen/$clientId"
    }

    data object ClientSignatureScreen :
        ClientScreens("client_signature_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId: Int) = "client_signature_screen/$clientId"
    }

    data object ClientSurveyListScreen :
        ClientScreens("client_survey_list_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId: Int) = "client_survey_list_screen/$clientId"
    }

    data object ClientSurveyQuestionScreen : ClientScreens("client_survey_question_screen")

    data object CreateClientScreen : ClientScreens("create_client_screen/{${Constants.CLIENT_ID}}")
}
