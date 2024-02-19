package com.mifos.core.model

class TimelineDb(
    var submittedOnDate: MutableList<Int> = ArrayList(),

    var submittedByUsername: String? = null,

    var submittedByFirstname: String? = null,

    var submittedByLastname: String? = null,

    var activatedOnDate: MutableList<Int> = ArrayList(),

    var activatedByUsername: String? = null,

    var activatedByFirstname: String? = null,

    var activatedByLastname: String? = null,

    var closedOnDate: MutableList<Int> = ArrayList(),

    var closedByUsername: String? = null,

    var closedByFirstname: String? = null,

    var closedByLastname: String? = null
)
