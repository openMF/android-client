package com.mifos.feature.client.navigation

import com.mifos.core.common.utils.Constants

sealed class ClientScreens(val route : String) {

    data object ClientListScreen : ClientScreens("client_list_screen")

    data object ClientDetailScreen : ClientScreens("client_detail_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId : Int) = "client_detail_screen/${clientId}"
    }

    data object ClientChargesScreen : ClientScreens("client_charges_screen")

    data object ClientIdentifierScreen : ClientScreens("client_identifier_screen")

    data object ClientPinPointScreen : ClientScreens("client_pin_point_screen")

    data object ClientSignatureScreen : ClientScreens("client_signature_screen")

    data object ClientSurveyListScreen : ClientScreens("client_survey_list_screen")

    data object ClientSurveyQuestionScreen : ClientScreens("client_survey_question_screen")
}