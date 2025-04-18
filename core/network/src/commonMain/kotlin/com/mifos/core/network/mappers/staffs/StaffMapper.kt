/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.staffs

import com.mifos.core.network.data.AbstractMapper
import com.mifos.core.network.model.RetrieveOneResponse
import com.mifos.room.entities.organisation.StaffEntity

object StaffMapper : AbstractMapper<RetrieveOneResponse, StaffEntity>() {
    override fun mapFromEntity(entity: RetrieveOneResponse): StaffEntity {
        return StaffEntity(
            id = entity.id!!.toInt(),
            firstname = entity.firstname,
            lastname = entity.lastname,
            displayName = entity.displayName,
            officeId = entity.officeId!!.toInt(),
            officeName = entity.officeName,
            isLoanOfficer = entity.isLoanOfficer,
            isActive = entity.isActive,
        )
    }

    override fun mapToEntity(domainModel: StaffEntity): RetrieveOneResponse {
        return RetrieveOneResponse(
            id = domainModel.id?.toLong(),
            firstname = domainModel.firstname,
            lastname = domainModel.lastname,
            displayName = domainModel.displayName,
            officeId = domainModel.officeId?.toLong(),
            officeName = domainModel.officeName,
            isLoanOfficer = domainModel.isLoanOfficer,
            isActive = domainModel.isActive,
        )
    }
}
