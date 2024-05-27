package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.noncore.DataTable
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
/**
 * Created by rajan on 13/3/16.
 */

@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class ClientsTemplate(
    var activationDate: IntArray = intArrayOf(),
    @PrimaryKey
    var officeId: Int = 0,
    var officeOptions: List<OfficeOptions> = ArrayList(),
    var staffOptions: List<StaffOptions> = ArrayList(),
    var savingProductOptions: List<SavingProductOptions> = ArrayList(),
    var genderOptions: List<Options> = ArrayList(),
    var clientTypeOptions: List<Options> = ArrayList(),
    var clientClassificationOptions: List<Options> = ArrayList(),
    var clientLegalFormOptions: List<InterestType> = ArrayList(),
    var dataTables: List<DataTable> = ArrayList()
) : MifosBaseModel(), Parcelable {

    override fun toString(): String {
        return "ClientsTemplate{" +
                "activationDate=" + activationDate.contentToString() +
                ", officeId=" + officeId +
                ", officeOptions=" + officeOptions +
                ", staffOptions=" + staffOptions +
                ", savingProductOptions=" + savingProductOptions +
                ", genderOptions=" + genderOptions +
                ", clientTypeOptions=" + clientTypeOptions +
                ", clientClassificationOptions=" + clientClassificationOptions +
                ", clientLegalFormOptions=" + clientLegalFormOptions +
                '}'
    }
}