package com.mifos.objects.checkerinboxandtasks

import com.google.gson.annotations.SerializedName

data class CheckerInboxSearchTemplate(@SerializedName("actionNames") var actionNames: List<String>,
                                       @SerializedName("entityNames") var entityNames: List<String>)