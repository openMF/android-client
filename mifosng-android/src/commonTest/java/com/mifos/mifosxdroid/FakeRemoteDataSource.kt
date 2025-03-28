/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid

import com.mifos.objects.SearchedEntity
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.client.Page
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.mifoserror.MifosError
import com.mifos.objects.noncore.Document
import kotlinx.serialization.builtins.ListSerializer

object FakeRemoteDataSource {
    private val mTestDataFactory = TestDataFactory()

    val clientList: Page<Client>
        get() = mTestDataFactory.getObjectTypePojo(
            Page.serializer(Client.serializer()),
            FakeJsonName.CLIENTS_JSON,
        )

    val searchedEntity: List<SearchedEntity>
        get() = mTestDataFactory.getListTypePojo(
            ListSerializer(SearchedEntity.serializer()),
            FakeJsonName.SEARCHED_ENTITY_JSON,
        )

    val centers: Page<Center>
        get() = mTestDataFactory.getObjectTypePojo(
            Page.serializer(Center.serializer()),
            FakeJsonName.CENTERS_JSON,
        )

    val centersGroupAndMeeting: CenterWithAssociations
        get() = mTestDataFactory.getObjectTypePojo(
            CenterWithAssociations.serializer(),
            FakeJsonName.CENTER_WITH_ASSOCIATIONS_JSON,
        )

    val clientCharges: Page<Charges>
        get() = mTestDataFactory.getObjectTypePojo(
            Page.serializer(Charges.serializer()),
            FakeJsonName.CHARGES_JSON,
        )

    val loanCharges: List<Charges>
        get() = mTestDataFactory.getListTypePojo(
            ListSerializer(Charges.serializer()),
            FakeJsonName.LOAN_CHARGES_JSON,
        )

    val documents: List<Document>
        get() = mTestDataFactory.getListTypePojo(
            ListSerializer(Document.serializer()),
            FakeJsonName.DOCUMENTS_JSON,
        )

    val groups: Page<Group>
        get() = mTestDataFactory.getObjectTypePojo(
            Page.serializer(Group.serializer()),
            FakeJsonName.GROUPS_JSON,
        )

    val clientPayloads: List<ClientPayload>
        get() = mTestDataFactory.getListTypePojo(
            ListSerializer(ClientPayload.serializer()),
            FakeJsonName.CLIENT_PAYLOADS,
        )

    val failureServerResponse: MifosError
        get() = mTestDataFactory.getObjectTypePojo(
            MifosError.serializer(),
            FakeJsonName.FAILURE_SERVER_RESPONSE,
        )
}
