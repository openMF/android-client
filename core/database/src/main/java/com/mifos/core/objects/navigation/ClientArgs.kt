package com.mifos.core.objects.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
 * Created by Aditya Gupta on 22/7/23.
*/

@Parcelize
data class ClientArgs(
    var clientId :Int? = null,

    var savingsAccountNumber : Int? = null,

    var loanAccountNumber : Int? = null
) : Parcelable