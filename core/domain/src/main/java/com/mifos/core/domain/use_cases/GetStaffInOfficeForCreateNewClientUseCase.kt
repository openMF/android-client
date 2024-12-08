package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.objects.organisation.Staff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 10/08/2024 (8:43 PM)
 */
class GetStaffInOfficeForCreateNewClientUseCase @Inject constructor(private val repository: CreateNewClientRepository) {

    suspend operator fun invoke(officeId: Int): Flow<Resource<List<Staff>>> =
        flow {
            try {
                emit(Resource.Loading())
                val response = repository.getStaffInOffice(officeId)
                emit(Resource.Success(response))
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
}
