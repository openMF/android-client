package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.core.objects.organisation.Office

data class CreateNewCenterUiState(
    val isLoading: Boolean = false,
    val offices: List<Office> = emptyList(),
    val message: String? = null
)
