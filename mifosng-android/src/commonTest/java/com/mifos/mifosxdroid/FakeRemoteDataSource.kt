package com.mifos.mifosxdroid

import com.google.gson.reflect.TypeToken
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

/**
 * Created by Rajan Maurya on 18/6/16.
 */
object FakeRemoteDataSource {
    private val mTestDataFactory = TestDataFactory()
    @JvmStatic
    val clientList: Page<Client>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<Page<Client>>() {}, FakeJsonName.CLIENTS_JSON)
    @JvmStatic
    val searchedEntity: List<SearchedEntity>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<List<SearchedEntity>>() {}, FakeJsonName.SEARCHED_ENTITY_JSON)
    @JvmStatic
    val centers: Page<Center>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<Page<Center>>() {}, FakeJsonName.CENTERS_JSON)
    @JvmStatic
    val centersGroupAndMeeting: CenterWithAssociations
        get() = mTestDataFactory.getObjectTypePojo(
            CenterWithAssociations::class.java,
            FakeJsonName.CENTER_WITH_ASSOCIATIONS_JSON
        )
    @JvmStatic
    val clientCharges: Page<Charges>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<Page<Charges>>() {}, FakeJsonName.CHARGES_JSON)
    @JvmStatic
    val loanCharges: List<Charges>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<List<Charges>>() {}, FakeJsonName.LOAN_CHARGES_JSON)
    @JvmStatic
    val documents: List<Document>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<List<Document>>() {}, FakeJsonName.DOCUMENTS_JSON)
    @JvmStatic
    val groups: Page<Group>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<Page<Group>>() {}, FakeJsonName.GROUPS_JSON)
    @JvmStatic
    val clientPayloads: List<ClientPayload>
        get() = mTestDataFactory.getListTypePojo(object :
            TypeToken<List<ClientPayload>>() {}, FakeJsonName.CLIENT_PAYLOADS)
    @JvmStatic
    val failureServerResponse: MifosError
        get() = mTestDataFactory.getObjectTypePojo(
            MifosError::class.java,
            FakeJsonName.FAILURE_SERVER_RESPONSE
        )
}