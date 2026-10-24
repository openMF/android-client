/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings.serverconfig

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.data.testing.ExperimentalDataTestingApi
import kpt.core.data.testing.FakeServerConfigRepository
import kpt.core.domain.validation.ValidationFailure
import kpt.core.model.utils.ServerConfig
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Locks the server-config editor's validation and save behaviour.
 *
 * The screen it replaces (`6b66e8a43:.../updateServer/`) validated only reactively, per keystroke,
 * and had no combined view of validity — so nothing stopped `UpdateServerConfig` from writing while
 * a field was invalid. The submit-path tests below are the ones that would have caught that.
 */
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalDataTestingApi::class)
class ServerConfigViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    /** A model whose one-shot hydrate from the stream has already run. */
    private fun kotlinx.coroutines.test.TestScope.newModel(repository: FakeServerConfigRepository = FakeServerConfigRepository()) =
        ServerConfigViewModel(repository).also { runCurrent() }

    @Test
    fun theDefaultConfigIsValid() = runTest(dispatcher) {
        val model = newModel()
        model.actionChannel.trySend(ServerConfigAction.UseDefaults)
        runCurrent()
        assertEquals(emptyMap(), model.stateFlow.value.failures, "ServerConfig.DEFAULT must pass its own rules")
        assertTrue(model.stateFlow.value.isValid)
    }

    @Test
    fun theLocalhostPresetIsAlsoValid() = runTest(dispatcher) {
        val model = newModel()
        model.actionChannel.trySend(ServerConfigAction.UseLocalhost)
        runCurrent()
        assertEquals(emptyMap(), model.stateFlow.value.failures, "ServerConfig.LOCALHOST must pass its own rules")
    }

    @Test
    fun aHostNameWithASchemeIsRejectedOnTheEndpointField() = runTest(dispatcher) {
        val model = newModel()
        model.actionChannel.trySend(ServerConfigAction.EndPointChanged("https://demo.mifos.org"))
        runCurrent()
        // The scheme belongs in the protocol field; attributing it to Endpoint is what tells the user where.
        assertEquals(
            ValidationFailure.MalformedHostname,
            model.stateFlow.value.failures[ServerConfigField.EndPoint],
        )
    }

    @Test
    fun anOutOfRangePortIsAttributedToThePortField() = runTest(dispatcher) {
        val model = newModel()
        model.actionChannel.trySend(ServerConfigAction.PortChanged("70000"))
        runCurrent()
        val failure = model.stateFlow.value.failures[ServerConfigField.Port]
        assertTrue(failure is ValidationFailure.PortOutOfRange, "expected PortOutOfRange, got $failure")
    }

    @Test
    fun anApiPathMissingItsTrailingSlashIsDistinguishedFromAMissingLeadingSlash() = runTest(dispatcher) {
        val model = newModel()

        model.actionChannel.trySend(ServerConfigAction.ApiPathChanged("/fineract-provider/api/v1"))
        runCurrent()
        assertEquals(
            ValidationFailure.ApiPathMissingTrailingSlash,
            model.stateFlow.value.failures[ServerConfigField.ApiPath],
        )

        model.actionChannel.trySend(ServerConfigAction.ApiPathChanged("fineract-provider/api/v1/"))
        runCurrent()
        assertEquals(
            ValidationFailure.ApiPathMissingLeadingSlash,
            model.stateFlow.value.failures[ServerConfigField.ApiPath],
        )
    }

    /**
     * The test the original could not have had: submit must refuse while any field is invalid.
     *
     * The original computed five independent error flows and no aggregate, so `UpdateServerConfig`
     * wrote whatever was in the fields regardless.
     */
    @Test
    fun submitWithAnInvalidFieldDoesNotPersistAnything() = runTest(dispatcher) {
        val prefs = FakeServerConfigRepository()
        val before = prefs.saved.value
        val model = newModel(prefs)

        model.actionChannel.trySend(ServerConfigAction.TenantChanged("not valid!"))
        model.actionChannel.trySend(ServerConfigAction.Submit)
        runCurrent()

        assertEquals(before, prefs.saved.value, "an invalid config must not be persisted")
        assertEquals(
            ValidationFailure.MalformedTenant,
            model.stateFlow.value.failures[ServerConfigField.Tenant],
        )
    }

    @Test
    fun submitWithAValidConfigPersistsIt() = runTest(dispatcher) {
        val prefs = FakeServerConfigRepository()
        val model = newModel(prefs)

        model.actionChannel.trySend(ServerConfigAction.UseLocalhost)
        model.actionChannel.trySend(ServerConfigAction.Submit)
        runCurrent()

        assertEquals(ServerConfig.LOCALHOST, prefs.saved.value)
    }

    /**
     * Submitting without touching a field still surfaces why it refused.
     *
     * Validation is computed on edit, so a never-edited invalid config would otherwise submit with an
     * empty `failures` map and fail silently.
     */
    @Test
    fun submitRepublishesFailuresEvenWhenNoFieldWasEdited() = runTest(dispatcher) {
        val model = newModel(FakeServerConfigRepository(initial = ServerConfig.DEFAULT.copy(port = "0")))
        assertEquals(emptyMap(), model.stateFlow.value.failures, "nothing validated yet")

        model.actionChannel.trySend(ServerConfigAction.Submit)
        runCurrent()

        assertTrue(
            model.stateFlow.value.failures[ServerConfigField.Port] is ValidationFailure.PortOutOfRange,
            "submit must publish the failure it refused on",
        )
    }

    @Test
    fun theFieldsAreSeededWithTheStoredConfig() = runTest(dispatcher) {
        val stored = ServerConfig.DEFAULT.copy(endPoint = "branch.example.org", tenant = "branch")
        assertEquals(stored, newModel(FakeServerConfigRepository(initial = stored)).stateFlow.value.config)
    }

    @Test
    fun aFailedSaveIsReportedNotClaimedAsSaved() = runTest(dispatcher) {
        val prefs = FakeServerConfigRepository().apply { failSaves = true }
        val model = newModel(prefs)

        model.actionChannel.trySend(ServerConfigAction.UseLocalhost)
        model.actionChannel.trySend(ServerConfigAction.Submit)
        runCurrent()

        assertEquals(ServerConfigEvent.SaveFailed, model.eventFlow.first())
        assertEquals(ServerConfig.DEFAULT, prefs.saved.value)
    }
}
