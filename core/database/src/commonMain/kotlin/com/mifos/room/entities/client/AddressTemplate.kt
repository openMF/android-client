package com.mifos.room.entities.client

import com.mifos.room.entities.templates.clients.OptionsEntity

data class AddressTemplate(
    val addressTypeIdOptions: List<OptionsEntity> = emptyList(),
    val countryIdOptions: List<OptionsEntity> = emptyList(),
    val stateProvinceIdOptions: List<OptionsEntity> = emptyList(),
)

data class AddressConfiguration(
    val enabled: Boolean = false,
)