/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mapper.loan

import com.mifos.core.model.entity.accounts.loan.CreateGuarantorRequest
import com.mifos.core.model.entity.accounts.loan.GuarantorClientOption
import com.mifos.core.model.entity.accounts.loan.GuarantorRelationshipOption
import com.mifos.core.model.entity.accounts.loan.GuarantorTemplate
import com.mifos.core.network.model.guarantor.CreateGuarantorRequestDto
import com.mifos.core.network.model.guarantor.GuarantorClientOptionDto
import com.mifos.core.network.model.guarantor.GuarantorRelationshipOptionDto
import com.mifos.core.network.model.guarantor.GuarantorTemplateDto

fun GuarantorTemplateDto.toModel(): GuarantorTemplate =
    GuarantorTemplate(
        clientOptions = clientOptions.map { it.toModel() },
        relationshipOptions = (relationshipOptions + allowedClientRelationshipTypes)
            .distinctBy { it.id }
            .map { it.toModel() },
    )

fun GuarantorClientOptionDto.toModel(): GuarantorClientOption =
    GuarantorClientOption(
        id = id,
        displayName = displayName.orEmpty().ifBlank { accountNo.orEmpty() },
    )

fun GuarantorRelationshipOptionDto.toModel(): GuarantorRelationshipOption =
    GuarantorRelationshipOption(
        id = id,
        value = value.orEmpty().ifBlank { name.orEmpty() },
    )

fun CreateGuarantorRequest.toDto(): CreateGuarantorRequestDto =
    CreateGuarantorRequestDto(
        existingClientId = existingClientId,
        clientRelationshipTypeId = clientRelationshipTypeId,
        firstname = firstname,
        lastname = lastname,
        dateOfBirth = dateOfBirth,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        city = city,
        zip = zip,
        mobileNumber = mobileNumber,
        housePhoneNumber = housePhoneNumber,
    )
