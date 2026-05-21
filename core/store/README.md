# `core/store` — Consumer Customization Seam

This module is the **single discoverable customization point** for `mifos-x-field-officer-app`'s
adoption of `kmp-project-template`'s Store5 offline-first pipeline. It scaffolds the
per-app integration layer for the framework's `core-base/store` (state model) and
`core-base/ui` (ScreenState rendering).

## What you get for free (no per-screen code)

By calling `ScreenContent(state, onRetry) { data -> ... }` (or `PagingScreenContent`),
your screen automatically gets:

- ✅ Loading → Content / NoNetwork / Error / Empty transitions, animated
- ✅ Captive-portal detection
- ✅ Auto-refresh when network reconnects (debounced 300ms)
- ✅ `lastContent` preservation during refresh (no flicker)
- ✅ Pagination with cache-first reads + load-more trigger + footer + retry
- ✅ Branded visuals from `appScreenStateDefaults()`
- ✅ A11y semantics (TalkBack/VoiceOver state announcements + liveRegion)
- ✅ Default `messageFor` routes through Fineract-aware `mapErrorToUserMessage`

You write **zero state-handling code** in screens. Decision logic lives in
`core-base/store`'s `DecisionEngine`.

## The four customization files

| File | Purpose |
|---|---|
| `AppStoreRegistry.kt` | Named-qualifier registry for every `Store5` instance. Phase C feature waves add `val Foo = store("foo")` here as they migrate. |
| `AppErrorMapper.kt` | `Throwable` → user-message mapper. Extend the `when` with Fineract exception branches as features land. |
| `AppScreenStateDefaults.kt` | Branded empty / error / no-network / loading visuals (Lottie + telemetry hooks reserved). |
| `di/StoreModule.kt` | Koin `appStoreModule` — `single<Store<…>>(qualifier = AppStoreRegistry.X) { … }` registrations. |

## Do NOT modify

- `core-base/store` — framework-shared, syncs from `kmp-project-template` via the sync-dirs workflow. Any per-app behavior should land here in `core/store` instead.
- `core-base/ui` — same.

## Wiring (Phase B follow-up)

The Phase B scaffold creates the files but does **not** yet wire them into the running
app. The remaining wiring tasks are:

1. **Theme** — thread `LocalScreenStateDefaults provides appScreenStateDefaults()` into
   `core/designsystem/.../MifosTheme.kt` so every screen inherits the branded defaults.
2. **Koin** — add `appStoreModule` to the `startKoin { modules(...) }` call in both
   `cmp-android` and `cmp-ios` app modules.
3. **AppDatabase v4 → v5 AutoMigration** — add `framework_submit_drafts` entity to the
   `MifosDatabase` `entities = [...]`, bump version, register the AutoMigration.
4. **OfflineSubmitSyncer boot** — call `syncer.start()` in `MainActivity.onCreate` (Android)
   and the iOS app entry point.

See the Phase B sub-plan in `plan-layer/project-plans/mifos-x/mifos-x-field-officer-app/active/store5-adoption/B-core.md`
for the full task list.

## See also

- Store implementation guide — `kmp-project-template/docs/store-implementation.md` (template repo)
- Phase B sub-plan — `plan-layer/project-plans/mifos-x/mifos-x-field-officer-app/active/store5-adoption/B-core.md`
- Phase C sub-plan (feature-wave migrations) — `plan-layer/project-plans/mifos-x/mifos-x-field-officer-app/active/store5-adoption/C-features.md`
