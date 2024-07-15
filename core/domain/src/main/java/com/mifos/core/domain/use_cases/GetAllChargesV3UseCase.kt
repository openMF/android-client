package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.LoanChargeDialogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import javax.inject.Inject

class GetAllChargesV3UseCase @Inject constructor(private val repository: LoanChargeDialogRepository) {

    suspend operator fun invoke(loanId: Int): Flow<Resource<ResponseBody>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getAllChargesV3(loanId)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }

}