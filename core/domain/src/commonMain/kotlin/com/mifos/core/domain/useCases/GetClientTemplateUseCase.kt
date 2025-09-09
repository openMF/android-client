package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import kotlinx.coroutines.flow.Flow

class GetClientTemplateUseCase(
    private val newClientRepository: CreateNewClientRepository
) {
    operator fun invoke() : Flow<DataState<ClientsTemplateEntity>> {
        return newClientRepository.clientTemplate()
    }
}