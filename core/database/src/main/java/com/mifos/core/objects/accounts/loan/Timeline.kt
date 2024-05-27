/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * This Model Time Object of LoanWithAssociations.
 *
 * Here
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
class Timeline(
    @PrimaryKey
    @Transient
    var loanId: Int? = null,

    var submittedOnDate: List<Int>? = null,

    var submittedByUsername: String? = null,

    var submittedByFirstname: String? = null,

    var submittedByLastname: String? = null,

    var approvedOnDate: List<Int>? = null,

    var approvedByUsername: String? = null,

    var approvedByFirstname: String? = null,

    var approvedByLastname: String? = null,

    var expectedDisbursementDate: List<Int>? = null,

    //This Object for saving the actualDisbursementDate, Not belong to any POST and GET Request
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @Transient
    var actualDisburseDate: ActualDisbursementDate? = null,

    var actualDisbursementDate: List<Int?>? = null,

    var disbursedByUsername: String? = null,

    var disbursedByFirstname: String? = null,

    var disbursedByLastname: String? = null,

    var closedOnDate: List<Int>? = null,

    var expectedMaturityDate: List<Int>? = null
) : MifosBaseModel(), Parcelable