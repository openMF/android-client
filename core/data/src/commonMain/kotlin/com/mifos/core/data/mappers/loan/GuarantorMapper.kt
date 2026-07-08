/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.loan

import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantor
import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantorInput
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorAccountTemplate
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorRelationshipOption
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorTemplate
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorType
import com.mifos.core.network.dto.loans.CreateGuarantorResponseDto
import com.mifos.core.network.dto.loans.GuarantorAccountTemplateDto
import com.mifos.core.network.dto.loans.GuarantorRelationshipOptionDto
import com.mifos.core.network.dto.loans.GuarantorRequestDto
import com.mifos.core.network.dto.loans.GuarantorTemplateDto
import com.mifos.core.network.dto.loans.GuarantorTypeDto

fun GuarantorTemplateDto.toDomain(): GuarantorTemplate =
    GuarantorTemplate(
        allowedClientRelationshipTypes = allowedClientRelationshipTypes.map { it.toDomain() },
    )

fun GuarantorRelationshipOptionDto.toDomain(): GuarantorRelationshipOption =
    GuarantorRelationshipOption(
        id = id,
        name = name,
    )

fun CreateGuarantorResponseDto.toDomain(): CreateGuarantor =
    CreateGuarantor(
        resourceId = resourceId,
        loanId = loanId,
        officeId = officeId,
    )

fun CreateGuarantorInput.toDto(): GuarantorRequestDto {
    return GuarantorRequestDto(
        clientRelationshipTypeId = clientRelationshipTypeId,
        dateFormat = dateFormat,
        entityId = entityId,
        guarantorTypeId = guarantorTypeId,
        locale = locale,
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
}

fun GuarantorAccountTemplateDto.toDomain(): GuarantorAccountTemplate {
    return GuarantorAccountTemplate(
        guarantorType = guarantorType.toDomain(),
        status = status,
        externalGuarantor = externalGuarantor,
        existingGroup = existingGroup,
        existingClient = existingClient,
        staffMember = staffMember,
    )
}

fun GuarantorTypeDto.toDomain(): GuarantorType {
    return GuarantorType(
        id = id,
        code = code,
        value = value,
    )
}
