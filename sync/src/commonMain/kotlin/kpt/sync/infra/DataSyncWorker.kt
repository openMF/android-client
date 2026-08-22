/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(io.github.mobilebytelabs.worker.ExperimentalWorkerApi::class)

package kpt.sync.infra

import io.github.mobilebytelabs.worker.CoroutineWorker
import io.github.mobilebytelabs.worker.WorkResult
import io.github.mobilebytelabs.worker.WorkerContext
import kpt.core.base.data.infra.Synchronizer
import kpt.core.base.datastore.infra.ChangeListVersions
import kpt.core.base.datastore.infra.SyncStatePersister

/**
 * Single data-sync worker. Implements [Synchronizer] so any [kpt.core.base.data.infra.Syncable]
 * collaborator can read + write [ChangeListVersions] through `this` without an extra abstraction.
 *
 * The template's showcase demo `Syncable` repositories (currency / macro-indicators) were removed
 * during template adoption — the field-officer fork does not sync those domains. This fork's real
 * offline sync (client / center / group / loan / savings payloads) is driven by the feature-layer
 * `Sync*DialogViewModel`s pushing queued payloads on demand, not by a headless background
 * `Synchronizer` adopter, so no repository is enrolled here yet.
 *
 * **No `getAll<Syncable>()`** — collaborators are constructor-injected as named interfaces. Enrolling
 * a repository (once one adopts [kpt.core.base.data.infra.Syncable]) requires editing this class
 * signature + its worker-registry autowiring — not a runtime discovery.
 *
 * The worker still reads the persisted [ChangeListVersions] at start and writes them back at end, so
 * the [SyncStatePersister] round-trip and the [Synchronizer] seam stay live for the first adopter.
 */
public class DataSyncWorker(
    context: WorkerContext,
    private val persister: SyncStatePersister,
) : CoroutineWorker(context), Synchronizer {

    // Synchronizer's in-memory state for THIS worker invocation. Read at start
    // from the persister; written back at end.
    private var workingVersions: ChangeListVersions = ChangeListVersions()

    override suspend fun getChangeListVersions(): ChangeListVersions = workingVersions

    override suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions) {
        workingVersions = workingVersions.update()
    }

    override suspend fun doWork(): WorkResult {
        workingVersions = persister.read()
        // No Syncable repositories are enrolled in this fork yet (see class KDoc). The persister
        // round-trip below keeps the sync-state seam live for the first adopter; once a repository
        // adopts Syncable, run its `syncWith(this@DataSyncWorker)` here and gate success on the result.
        persister.write(workingVersions)
        return WorkResult.success()
    }
}
