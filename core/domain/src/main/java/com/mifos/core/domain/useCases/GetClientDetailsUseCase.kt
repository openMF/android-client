package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.objects.zipmodels.ClientAndClientAccounts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class GetClientDetailsUseCase @Inject constructor(private val repository: ClientDetailsRepository) {

    operator fun invoke(clientId: Int): Flow<Resource<ClientAndClientAccounts>> = flow {
        try {
            emit(Resource.Loading())
            val clientAndClientAccounts = withContext(Dispatchers.IO) {
                val clientAccountsDeferred = async { repository.getClientAccounts(clientId) }
                val clientDeferred = async { repository.getClient(clientId) }

                val clientAccounts = clientAccountsDeferred.await()
                val client = clientDeferred.await()

                ClientAndClientAccounts().apply {
                    this.client = client
                    this.clientAccounts = clientAccounts
                }
            }
            emit(Resource.Success(clientAndClientAccounts))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}