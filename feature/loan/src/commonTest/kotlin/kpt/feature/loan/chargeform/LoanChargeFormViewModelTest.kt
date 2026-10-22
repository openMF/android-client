/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(kpt.core.data.testing.ExperimentalDataTestingApi::class)

package kpt.feature.loan.chargeform

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.common.utils.DateHelper
import kpt.core.data.testing.FakeChargeCommandRepository
import kpt.core.data.testing.FakeLoanRepository
import kpt.core.model.objects.template.client.ChargeCalculationType
import kpt.core.model.objects.template.client.ChargeOptions
import kpt.core.model.objects.template.client.ChargeTemplate
import kpt.core.model.objects.template.client.ChargeTimeType
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoanChargeFormViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private fun option(
        id: Int? = 11,
        name: String? = "Processing Fee",
        amount: Double? = 150.0,
        time: String? = "Specified due date",
        calculation: String? = "Flat",
    ) = ChargeOptions(
        id = id,
        name = name,
        amount = amount,
        chargeTimeType = time?.let { ChargeTimeType(id = 1, code = "c", value = it) },
        chargeCalculationType = calculation?.let {
            ChargeCalculationType(id = 1, code = "c", value = it)
        },
    )

    private fun template(vararg options: ChargeOptions) =
        ChargeTemplate(penalty = false, chargeOptions = options.toList())

    private fun viewModel(
        commands: FakeChargeCommandRepository = FakeChargeCommandRepository(),
        chargeTemplate: () -> ChargeTemplate? = { template(option()) },
        loanId: Int = 7,
    ) = LoanChargeFormViewModel(
        FakeLoanRepository(chargeTemplate = chargeTemplate),
        commands,
        SavedStateHandle(mapOf("loanId" to loanId)),
    )

    private val disbursementCharge = LoanChargeType(
        id = 22,
        name = "Disbursement Fee",
        amount = 50.0,
        time = ChargeTimeKind.DISBURSEMENT,
        calculation = ChargeCalculationKind.FLAT,
    )

    private val dueDateCharge = LoanChargeType(
        id = 11,
        name = "Processing Fee",
        amount = 150.0,
        time = ChargeTimeKind.SPECIFIED_DUE_DATE,
        calculation = ChargeCalculationKind.FLAT,
    )

    // ---- the template ----

    @Test
    fun theRouteArgumentReachesBothTheTemplateReadAndTheWrite() = runTest(dispatcher) {
        val repository = FakeLoanRepository(chargeTemplate = { template(option()) })
        val commands = FakeChargeCommandRepository()
        val vm = LoanChargeFormViewModel(
            repository,
            commands,
            SavedStateHandle(mapOf("loanId" to 42)),
        )
        testScheduler.advanceUntilIdle()

        assertEquals(listOf(42), repository.chargeTemplateQueries.toList())

        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()
        assertEquals(42, commands.created.single().second)
    }

    @Test
    fun anOptionMissingAnythingTheFormNeedsIsDROPPED() = runTest(dispatcher) {
        // The original's `mapNotNull`, kept: a charge with no id, no name, no amount, or an
        // unrecognised time/calculation type cannot be submitted, so it must not be offered.
        val vm = viewModel(
            chargeTemplate = {
                template(
                    option(),
                    option(id = null),
                    option(name = null),
                    option(amount = null),
                    // Fineract has many more charge-time kinds than the three this form handles.
                    option(time = "Overdue Installment"),
                    // And percentage charges need a base the form does not collect.
                    option(calculation = "% Approved Amount"),
                )
            },
        )
        testScheduler.advanceUntilIdle()

        assertEquals(listOf(11), vm.stateFlow.value.chargeTypes.map { it.id })
    }

    @Test
    fun theThreeChargeTimeKindsMapCaseInsensitively() {
        assertEquals(ChargeTimeKind.DISBURSEMENT, ChargeTimeKind.fromValue("disbursement"))
        assertEquals(ChargeTimeKind.INSTALLMENT_FEE, ChargeTimeKind.fromValue("Installment Fee"))
        assertEquals(
            ChargeTimeKind.SPECIFIED_DUE_DATE,
            ChargeTimeKind.fromValue("SPECIFIED DUE DATE"),
        )
        assertNull(ChargeTimeKind.fromValue("Overdue Installment"))
        assertNull(ChargeTimeKind.fromValue(null))
    }

    @Test
    fun aFailedTemplateReadIsReportedAndRetryable() = runTest(dispatcher) {
        val vm = viewModel(chargeTemplate = { null })
        testScheduler.advanceUntilIdle()

        assertTrue(vm.stateFlow.value.typesError)
        assertTrue(!vm.stateFlow.value.isLoadingTypes)

        vm.actionChannel.trySend(LoanChargeFormAction.OnRetry)
        testScheduler.advanceUntilIdle()
        assertTrue(vm.stateFlow.value.typesError)
    }

    @Test
    fun aTenantWithNoUsableChargeTypeYieldsAnEmptyListNotAnError() = runTest(dispatcher) {
        val vm = viewModel(chargeTemplate = { template(option(time = "Overdue Installment")) })
        testScheduler.advanceUntilIdle()

        assertTrue(vm.stateFlow.value.chargeTypes.isEmpty())
        // Not an error — the read SUCCEEDED, there is simply nothing this form can add.
        assertTrue(!vm.stateFlow.value.typesError)
    }

    // ---- selection ----

    @Test
    fun choosingAChargePrefillsItsDefaultAmount() = runTest(dispatcher) {
        val vm = viewModel()
        testScheduler.advanceUntilIdle()

        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        testScheduler.advanceUntilIdle()

        assertEquals("150.0", vm.stateFlow.value.amount)
        assertEquals(11, vm.stateFlow.value.selectedCharge?.id)
    }

    @Test
    fun onlyASpecifiedDueDateChargeNeedsADueDate() = runTest(dispatcher) {
        val vm = viewModel()
        testScheduler.advanceUntilIdle()

        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(disbursementCharge))
        testScheduler.advanceUntilIdle()
        assertTrue(!vm.stateFlow.value.needsDueDate)

        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        testScheduler.advanceUntilIdle()
        assertTrue(vm.stateFlow.value.needsDueDate)
    }

    // ---- the payload ----

    @Test
    fun theDueDateIsSentONLYForASpecifiedDueDateCharge() = runTest(dispatcher) {
        // Sending one on a disbursement charge makes Fineract reject the whole request — the
        // original's conditional, kept. It drew the field regardless, though, so an officer could
        // pick a date that was then silently discarded.
        val commands = FakeChargeCommandRepository()
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()

        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(disbursementCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()
        assertNull(commands.created.single().third.dueDate)

        vm.actionChannel.trySend(LoanChargeFormAction.CloseDialog)
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnDueDateChanged(DUE_MILLIS))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        assertEquals("14-03-2026", commands.created.last().third.dueDate)
    }

    @Test
    fun theDatePairsWithItsOwnDeclaredFormat() = runTest(dispatcher) {
        // One of the few payloads in the original where the date and its `dateFormat` agreed:
        // `getDateAsStringFromLong` emits "dd-MM-yyyy" and the payload declared the same literal.
        // Kept, with the literal replaced by the constant the helper actually formats with.
        val commands = FakeChargeCommandRepository()
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnDueDateChanged(DUE_MILLIS))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        val payload = commands.created.single().third
        assertEquals(DateHelper.SHORT_MONTH, payload.dateFormat)
        assertEquals(DateHelper.getDateAsStringFromLong(DUE_MILLIS), payload.dueDate)
    }

    @Test
    fun theRequestCarriesTheChargeIdTheLoanAndTheAmount() = runTest(dispatcher) {
        val commands = FakeChargeCommandRepository()
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnAmountChanged("275.50"))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        val (resourceType, resourceId, payload) = commands.created.single()
        assertEquals("loans", resourceType)
        assertEquals(7, resourceId)
        assertEquals(11, payload.chargeId)
        assertEquals(7, payload.loanId)
        assertEquals("275.50", payload.amount)
        assertEquals("en", payload.locale)
    }

    // ---- validation ----

    @Test
    fun submitWithNoChargeChosenIsRefused() = runTest(dispatcher) {
        val commands = FakeChargeCommandRepository()
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()

        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        assertTrue(vm.stateFlow.value.chargeError)
        assertTrue(commands.created.isEmpty())
    }

    @Test
    fun theAmountIsReValidatedAtSUBMIT() = runTest(dispatcher) {
        // The original validated the amount only as a side effect of TYPING
        // (`amountError = amount.toDoubleOrNull() == null` inside `updateAmount`) and never
        // re-checked it at submit — so a prefilled-but-unparseable amount, or one never touched,
        // went unvalidated.
        val commands = FakeChargeCommandRepository()
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnAmountChanged("not a number"))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        assertTrue(vm.stateFlow.value.amountError)
        assertTrue(commands.created.isEmpty())
    }

    @Test
    fun aZeroOrNegativeChargeIsRefused() = runTest(dispatcher) {
        val commands = FakeChargeCommandRepository()
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))

        vm.actionChannel.trySend(LoanChargeFormAction.OnAmountChanged("0"))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()
        assertTrue(vm.stateFlow.value.amountError)

        vm.actionChannel.trySend(LoanChargeFormAction.OnAmountChanged("-5"))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()
        assertTrue(vm.stateFlow.value.amountError)

        assertTrue(commands.created.isEmpty())
    }

    @Test
    fun aSecondSubmitWhileOneIsInFlightIsIgnored() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        val commands = FakeChargeCommandRepository(gate = gate)
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        assertEquals(1, commands.created.size, "a double tap must not levy the charge twice")
        gate.complete(Unit)
        testScheduler.advanceUntilIdle()
        assertTrue(!vm.stateFlow.value.isSubmitting)
    }

    // ---- the outcome ----

    @Test
    fun successShowsTheDialog() = runTest(dispatcher) {
        val vm = viewModel()
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        assertIs<LoanChargeFormState.DialogState.Submitted>(vm.stateFlow.value.dialogState)
    }

    @Test
    fun offlineSaysTheChargeWasNotAdded() = runTest(dispatcher) {
        val commands = FakeChargeCommandRepository(
            result = { MutationResult.Blocked(BlockReason.OFFLINE) },
        )
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        val dialog = assertIs<LoanChargeFormState.DialogState.Failed>(vm.stateFlow.value.dialogState)
        assertTrue(dialog.msg.contains("No connection"), "message was ${dialog.msg}")
    }

    @Test
    fun aRejectionSurfacesFineractsReasonAndKeepsTheForm() = runTest(dispatcher) {
        val commands = FakeChargeCommandRepository(
            result = {
                MutationResult.Failed(
                    IllegalStateException("charge is not applicable to this loan product"),
                    rolledBack = false,
                )
            },
        )
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(LoanChargeFormAction.OnChargeSelected(dueDateCharge))
        vm.actionChannel.trySend(LoanChargeFormAction.OnAmountChanged("200"))
        vm.actionChannel.trySend(LoanChargeFormAction.OnSubmit)
        testScheduler.advanceUntilIdle()

        val dialog = assertIs<LoanChargeFormState.DialogState.Failed>(vm.stateFlow.value.dialogState)
        assertEquals("charge is not applicable to this loan product", dialog.msg)
        assertEquals("200", vm.stateFlow.value.amount)
        assertEquals(11, vm.stateFlow.value.selectedCharge?.id)
    }

    @Test
    fun backNavigationIsAnEvent() = runTest(dispatcher) {
        val vm = viewModel()
        vm.eventFlow.test {
            vm.actionChannel.trySend(LoanChargeFormAction.NavigateBack)
            assertEquals(LoanChargeFormEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        /** 2026-03-14 12:00 UTC — noon, so the device zone cannot shift the DATE. */
        const val DUE_MILLIS = 1_773_489_600_000L
    }
}
