# feature/center — migration ledger

**Source:** `6b66e8a43:feature/center` — 17 `.kt` (5 screens, 5 ViewModels)
**Landed:** out of slice order, ahead of S6 — the dashboard's Centers TAB needed a destination
**Status:** read paths complete. Three write surfaces deferred (§7).

---

## 1. Original inventory

| original package | files | migrated to | note |
|---|---|---|---|
| `centerList/ui/` | 3 | `list/` | `CenterListUiState` dissolves into `ScreenState` |
| `centerDetails/` | 3 | `detail/` | merged with `centerGroupList` — see §3 |
| `centerGroupList/` | 3 | `detail/` | merged, NOT dropped |
| `createCenter/` | 3 | — | **DEFER** (§7) |
| `syncCentersDialog/` | 3 | — | **DEFER** (§7) |
| `navigation/CenterNavigation.kt` | 1 | `navigation/CenterNavigation.kt` + `CenterTab.kt` | tab declaration split out as a `@FeatureTab` |
| `di/CenterModule.kt` | 1 | `di/CenterModule.kt` | |

## 2. Use-case classification

| use case | kind | substrate |
|---|---|---|
| list centers | read, cache-first | `CenterRepository.centersStream` |
| one center's details | read | selected from the same stream (§3) |
| groups of a center | read | `GroupRepository.groupsStream`, filtered |
| create a center | write | deferred (§7) |
| bulk sync centers | write | deferred (§7) |

## 3. Substrate mapping

**Two original screens became one.** `CenterDetailsScreen` showed a center's fields and a "Groups"
button; tapping it pushed `GroupListScreen`, which showed the groups of that same center and
nothing else. Two destinations, two ViewModels, two loading states for one center's worth of
information — and the group list could load for a center whose own detail had failed.

They are now one screen with two INDEPENDENT `ScreenState`s. Independence is the property that
makes the merge honest, and it is asserted in both directions
(`CenterDetailViewModelTest.aFailedGroupsReadDoesNotBlankTheCenterDetails` and its inverse); a
shared `combine` passes the first and fails the second.

**No per-id read.** Neither repository exposes one. `centersStream` is a list over the officer's
caseload, and a center reached from the list is by construction in it — so detail SELECTS from the
same stream, keeping one cache warm instead of a second per-id store to invalidate in step.

**The connectivity branch is gone.** The original opened each load with a `userStatus` flag
choosing between a DB read and an API read, each with its own `DataState` fold. `centersStream`
serves cache-first with a background refresh, so both paths collapse into one subscription.

**Manual paging removed.** `loadCentersPaged(limit, offset)` + an `isLastPage` flag held as list
bookkeeping in the ViewModel. If server-side paging is wanted back it belongs in the Store as a
`PagingScreenStream` — holding it in the ViewModel is what let `isLastPage` disagree with the list
after a refresh.

## 4. Write policy

None shipped. `CenterCommandRepository` exposes `activateCenter` / `activate2` and the collection-
sheet calls; none is surfaced yet. Center activation is the obvious next one — `feature/groups`
already ports the equivalent.

## 5. New-state decisions

| state | decision |
|---|---|
| `Empty` (list) | a filter matching nothing renders Empty, not a Content of zero rows — otherwise "no match" is indistinguishable from "this officer services no centers" |
| `Empty` (detail) | a center outside the caseload is Empty ("not yours"), never a Content of blank fields |
| `Empty` (groups) | its own state, independent of the center's |
| `NoNetwork` | last known rows stay, with the offline signal |

## 6. Nav registration

`@FeatureTab CenterTab` + `@FeatureDestination centerListDestination`, both collected by codegen.
The tab points at the GRAPH so it owns its own back stack; detail pushes on the OUTER controller so
it covers the bottom bar. The placeholder `AuthenticatedNavBarTabItem.CentersTab` was DELETED in
the same change — two Centers tabs, one of them dead, is worse than the gap it covered.

## 7. Deviations from the original

| deviation | reason |
|---|---|
| `centerGroupList` merged into detail rather than ported as a screen | §3 |
| `createCenter` not ported | `createCenter` exists on the command repository, but the form needs office/staff pickers whose templates are not ported. A create form that cannot populate its pickers is worse than none. Tracked in `CAPABILITY_GAPS.yaml`. |
| `syncCentersDialog` not ported | belongs with the Offline Sync drawer entry, which is itself unported (S8). Porting it alone would give two entry points to a capability that has none. |
| `SelectionModeTopAppBar` + long-press selection not ported | selection exists ONLY to drive the bulk Sync action above. With no sync path, long-press would select rows for an action that cannot run. |
| create FAB absent, not disabled | an affordance that does nothing is the dead-clickable defect; absent is honest |
| `PullToRefreshBox` not yet wired | the streams expose `refresh()`, so this is an omission rather than a decision. Noted so it is not mistaken for one. |
