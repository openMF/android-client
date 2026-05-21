# `core/store`

Consumer customization seam for the offline-first Store5 pipeline shipped by
`core-base/store` + `core-base/ui`. App-specific branding, error mapping, and
Store registrations live here; `core-base/*` stays framework-shared and is
not edited locally.

## Files

| File | Role |
|---|---|
| `AppStoreRegistry.kt` | Named-qualifier registry. Add `val Foo = store("foo")` per `Store<K, V>` the app exposes. |
| `AppErrorMapper.kt` | `Throwable → user-message` mapper. Extend the `when` with domain exception branches above the `categorize` fallback. |
| `AppScreenStateDefaults.kt` | Branded `ScreenStateDefaults` (loading skeleton, empty/error/no-network copy). Wired into `MifosTheme` via `LocalScreenStateDefaults`. |
| `di/StoreModule.kt` | Koin `appStoreModule`. Register `single<Store<K, V>>(qualifier = AppStoreRegistry.X) { ... }` per Store. |

## Offline mutations (`SubmitOutbox` + `OfflineSubmitSyncer`)

`OfflineSubmitSyncer<P, R>` is parameterized by payload type — instantiate one per
form in the feature's DI module, bound to a long-lived `CoroutineScope`:

```kotlin
single<SubmitOutbox<LoanApplicationPayload>> {
    RoomSubmitOutbox(
        dao = get<MifosDatabase>().draftDao,
        serializer = LoanApplicationPayload.serializer(),
    )
}
single { (scope: CoroutineScope) ->
    scope.offlineSubmitSyncer(
        outbox       = get<SubmitOutbox<LoanApplicationPayload>>(),
        isOnlineFlow = get<NetworkMonitor>().isOnline,
        submitBlock  = { payload -> get<LoanRepository>().submit(payload) },
    ).also { it.start() }
}
```

Drafts are persisted in the `framework_submit_drafts` table
(`com.mifos.room.entities.framework.DraftEntity`). The syncer retries every
`PENDING` draft on each offline → online transition.
