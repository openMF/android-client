package com.mifos.objects.navigation

import android.os.Parcelable
import com.mifos.objects.client.Client
import kotlinx.parcelize.Parcelize

/*
 * Created by Aditya Gupta on 22/7/23.
*/

@Parcelize
data class ClientListArgs(
    var clientsList : List<Client> = ArrayList(),

    var isParentFragment : Boolean = false
) : Parcelable
