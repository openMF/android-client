package com.mifos.feature.loan.assignLoanOfficer

import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity

internal data class AssignLoanOfficerUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val loan: LoanWithAssociationsEntity? = null,
    val officers: List<StaffEntity> = emptyList(),
    val selectedOfficerIndex: Int = -1,
    val officerShowError: Boolean = false,
    val assignmentDateMillis: Long = 0L,
    val submitInProgress: Boolean = false,
    val submitError: String? = null,
    val completed: Boolean = false,
)