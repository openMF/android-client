/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.store.submit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineSubmitSyncerTest {

    private val formKey = "test_form"

    // ─── T1: comes online → retries all PENDING ───────────────────────────────

    @Test
    fun `coming online retries all PENDING entries`() = runTest(UnconfinedTestDispatcher()) {
        val outbox = FakeSubmitOutbox<String>()
        outbox.save(formKey, "payload1")
        outbox.save("form2", "payload2")

        val isOnline = MutableStateFlow(false)
        var submitCount = 0

        val syncer = backgroundScope.offlineSubmitSyncer<String, Unit>(
            outbox = outbox,
            isOnlineFlow = isOnline,
            submitBlock = { submitCount++ },
        )
        syncer.start()

        isOnline.value = true

        assertEquals(2, submitCount)
        assertTrue(outbox.getAllPending().isEmpty(), "All entries should be SUBMITTED after sync")
    }

    // ─── T2: edge-triggered — no duplicate retries ───────────────────────────

    @Test
    fun `emitting true twice only retries once per batch`() = runTest(UnconfinedTestDispatcher()) {
        val outbox = FakeSubmitOutbox<String>()
        outbox.save(formKey, "payload")

        val isOnline = MutableStateFlow(true)
        var submitCount = 0

        val syncer = backgroundScope.offlineSubmitSyncer<String, Unit>(
            outbox = outbox,
            isOnlineFlow = isOnline,
            submitBlock = { submitCount++ },
        )
        syncer.start()

        // Emitting true again with no state change must not re-trigger (distinctUntilChanged)
        isOnline.value = true

        assertEquals(1, submitCount, "distinctUntilChanged must prevent duplicate retry batch")
    }

    // ─── T3: retry success → entry marked SUBMITTED ───────────────────────────

    @Test
    fun `successful retry marks entry as SUBMITTED`() = runTest(UnconfinedTestDispatcher()) {
        val outbox = FakeSubmitOutbox<String>()
        outbox.save(formKey, "payload")

        val isOnline = MutableStateFlow(false)
        val syncer = backgroundScope.offlineSubmitSyncer<String, Unit>(
            outbox = outbox,
            isOnlineFlow = isOnline,
            submitBlock = { /* success */ },
        )
        syncer.start()
        isOnline.value = true

        val entry = outbox.entries.first()
        assertEquals(SubmitOutboxStatus.SUBMITTED, entry.status)
    }

    // ─── T4: individual failure → FAILED, others continue ────────────────────

    @Test
    fun `one failing entry is marked FAILED and does not abort remaining entries`() =
        runTest(UnconfinedTestDispatcher()) {
        val outbox = FakeSubmitOutbox<String>()
        outbox.save("fail_form", "bad_payload")
        outbox.save("ok_form", "good_payload")

        val isOnline = MutableStateFlow(false)
        val syncer = backgroundScope.offlineSubmitSyncer<String, Unit>(
            outbox = outbox,
            isOnlineFlow = isOnline,
            submitBlock = { payload ->
                if (payload == "bad_payload") throw RuntimeException("server error")
            },
        )
        syncer.start()
        isOnline.value = true

        val entries = outbox.entries
        val failEntry = entries.first { it.formKey == "fail_form" }
        val okEntry = entries.first { it.formKey == "ok_form" }
        assertEquals(SubmitOutboxStatus.FAILED, failEntry.status)
        assertEquals(SubmitOutboxStatus.SUBMITTED, okEntry.status)
    }

    // ─── T5: RETRYING entry is excluded from getAllPending → not double-synced ─

    @Test
    fun `entries already in RETRYING status are not picked up by syncer`() = runTest(UnconfinedTestDispatcher()) {
        val outbox = FakeSubmitOutbox<String>()
        val id = outbox.save(formKey, "payload")
        outbox.markRetrying(id)  // simulate UI already claiming this entry

        val isOnline = MutableStateFlow(false)
        var submitCount = 0
        val syncer = backgroundScope.offlineSubmitSyncer<String, Unit>(
            outbox = outbox,
            isOnlineFlow = isOnline,
            submitBlock = { submitCount++ },
        )
        syncer.start()
        isOnline.value = true

        assertEquals(0, submitCount, "RETRYING entries must be skipped by syncer")
    }
}
