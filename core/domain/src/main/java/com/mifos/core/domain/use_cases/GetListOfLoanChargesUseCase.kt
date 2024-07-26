package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.objects.client.Charges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListOfLoanChargesUseCase @Inject constructor(private val repository: LoanChargeRepository) {

    suspend operator fun invoke(loanId: Int): Flow<Resource<List<Charges>>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getListOfLoanCharges(loanId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}