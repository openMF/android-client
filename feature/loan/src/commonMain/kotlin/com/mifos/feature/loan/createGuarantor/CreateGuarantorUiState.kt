/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.createGuarantor

import com.mifos.core.model.entity.accounts.loan.GuarantorClientOption
import com.mifos.core.model.entity.accounts.loan.GuarantorRelationshipOption

internal sealed interface CreateGuarantorUiState {
    data object Loading : CreateGuarantorUiState

    data class Error(
        val message: String,
    ) : CreateGuarantorUiState

    data class Content(
        val loanId: Int,
        val existingClient: Boolean = true,
        val clientOptions: List<GuarantorClientOption> = emptyList(),
        val relationshipOptions: List<GuarantorRelationshipOption> = emptyList(),
        val selectedClientIndex: Int = -1,
        val selectedRelationshipIndex: Int = -1,
        val firstName: String = "",
        val lastName: String = "",
        val dateOfBirthMillis: Long? = null,
        val addressLine1: String = "",
        val addressLine2: String = "",
        val city: String = "",
        val zip: String = "",
        val mobile: String = "",
        val residencePhone: String = "",
        val clientError: String? = null,
        val relationshipError: String? = null,
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val submitInProgress: Boolean = false,
    ) : CreateGuarantorUiState {
        val selectedClientId: Int?
            get() = clientOptions.getOrNull(selectedClientIndex)?.id

        val selectedRelationshipId: Int?
            get() = relationshipOptions.getOrNull(selectedRelationshipIndex)?.id

        val canSubmit: Boolean
            get() = selectedRelationshipId != null &&
                if (existingClient) {
                    selectedClientId != null
                } else {
                    firstName.isNotBlank() && lastName.isNotBlank()
                }
    }
}

internal sealed interface CreateGuarantorEffect {
    data class ShowMessage(val message: String) : CreateGuarantorEffect
    data object NavigateBack : CreateGuarantorEffect
}

internal sealed interface CreateGuarantorAction {
    data object Load : CreateGuarantorAction
    data object Retry : CreateGuarantorAction
    data class ToggleExistingClient(val checked: Boolean) : CreateGuarantorAction
    data class SelectClient(val index: Int) : CreateGuarantorAction
    data class SelectRelationship(val index: Int) : CreateGuarantorAction
    data class UpdateFirstName(val value: String) : CreateGuarantorAction
    data class UpdateLastName(val value: String) : CreateGuarantorAction
    data class UpdateDateOfBirth(val millis: Long?) : CreateGuarantorAction
    data class UpdateAddressLine1(val value: String) : CreateGuarantorAction
    data class UpdateAddressLine2(val value: String) : CreateGuarantorAction
    data class UpdateCity(val value: String) : CreateGuarantorAction
    data class UpdateZip(val value: String) : CreateGuarantorAction
    data class UpdateMobile(val value: String) : CreateGuarantorAction
    data class UpdateResidencePhone(val value: String) : CreateGuarantorAction
    data object Submit : CreateGuarantorAction
}
