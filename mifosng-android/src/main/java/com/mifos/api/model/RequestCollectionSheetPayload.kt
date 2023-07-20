package com.mifos.api.model

/**
 * Created by Tarun on 06-07-2017.
 */
data class RequestCollectionSheetPayload(
    var dateFormat: String = "dd MMMM yyyy",
    var locale: String = "en",
    var officeId: Int? = null,
    var staffId: Int? = null,
    var transactionDate: String = ""
)