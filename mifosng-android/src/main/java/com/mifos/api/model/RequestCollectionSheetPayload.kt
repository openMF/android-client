package com.mifos.api.model

/**
 * Created by Tarun on 06-07-2017.
 */
data class RequestCollectionSheetPayload(
    var dateFormat: String = "dd MMMM yyyy",
    var locale: String = "en",
    var officeId: Int = 0,
    var staffId: Int = 0,
    var transactionDate: String = ""
)