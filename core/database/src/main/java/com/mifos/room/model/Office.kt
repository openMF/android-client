/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.model

import com.mifos.core.model.objects.databaseobjects.office.Office
import com.mifos.core.model.objects.databaseobjects.office.OfficeOpeningDate
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.OfficeOpeningDateEntity

fun OfficeEntity.toOffice(): Office {
    return Office(
        id = id,
        externalId = externalId,
        name = name,
        nameDecorated = nameDecorated,
        officeOpeningDate = OfficeOpeningDate(
            officeId = officeOpeningDate?.officeId,
            year = officeOpeningDate?.year,
            month = officeOpeningDate?.month,
            day = officeOpeningDate?.day,
        ),
        openingDate = openingDate,
    )
}

fun Office.toOfficeEntity(): OfficeEntity {
    return OfficeEntity(
        id = id ?: 0,
        externalId = externalId,
        name = name,
        nameDecorated = nameDecorated,
        officeOpeningDate = OfficeOpeningDateEntity(
            officeId = officeOpeningDate?.officeId,
            year = officeOpeningDate?.year,
            month = officeOpeningDate?.month,
            day = officeOpeningDate?.day,
        ),
        openingDate = openingDate,
    )
}
