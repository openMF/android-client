package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.ChargesPayload
import com.mifos.core.data.repository.LoanChargeDialogRepository
import com.mifos.core.objects.client.ChargeCreationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateLoanChargesUseCase @Inject constructor(private val repository: LoanChargeDialogRepository) {

    suspend operator fun invoke(
        loanId: Int,
        chargesPayload: ChargesPayload
    ): Flow<Resource<ChargeCreationResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.createLoanCharges(loanId, chargesPayload)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}