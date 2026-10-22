/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.loan.chargeform

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class LoanChargeFormScreenUiTest {

    private val dueDateCharge = LoanChargeType(
        id = 11,
        name = "Processing Fee",
        amount = 150.0,
        time = ChargeTimeKind.SPECIFIED_DUE_DATE,
        calculation = ChargeCalculationKind.FLAT,
    )

    private val disbursementCharge = LoanChargeType(
        id = 22,
        name = "Disbursement Fee",
        amount = 50.0,
        time = ChargeTimeKind.DISBURSEMENT,
        calculation = ChargeCalculationKind.FLAT,
    )

    private fun loaded(selected: LoanChargeType? = null) = LoanChargeFormState(
        loanId = 7,
        chargeTypes = listOf(dueDateCharge, disbursementCharge),
        selectedCharge = selected,
        amount = selected?.amount?.toString().orEmpty(),
    )

    @Test
    fun theChargeDropdownAndAmountRenderBeforeAnythingIsChosen() = runComposeUiTest {
        setContent {
            KptTheme { LoanChargeFormContent(state = loaded(), onAction = {}) }
        }
        onNodeWithTag(LoanChargeFormTestTags.CHARGE_TYPE).assertIsDisplayed()
        onNodeWithTag(LoanChargeFormTestTags.AMOUNT).assertIsDisplayed()
        // The two DETERMINED fields only appear once a charge is chosen — there is nothing to
        // show about a charge nobody has picked.
        onNodeWithTag(LoanChargeFormTestTags.CHARGE_TIME).assertDoesNotExist()
        onNodeWithTag(LoanChargeFormTestTags.CHARGE_CALCULATION).assertDoesNotExist()
    }

    @Test
    fun choosingAChargeRevealsWhatItWillDo() = runComposeUiTest {
        setContent {
            KptTheme { LoanChargeFormContent(state = loaded(dueDateCharge), onAction = {}) }
        }
        onNodeWithTag(LoanChargeFormTestTags.CHARGE_TIME).assertIsDisplayed()
        onNodeWithTag(LoanChargeFormTestTags.CHARGE_CALCULATION).assertIsDisplayed()
        onNodeWithText("Specified Due Date").assertIsDisplayed()
        onNodeWithText("Flat").assertIsDisplayed()
    }

    @Test
    fun theDueDateFieldAppearsONLYForASpecifiedDueDateCharge() = runComposeUiTest {
        setContent {
            KptTheme { LoanChargeFormContent(state = loaded(dueDateCharge), onAction = {}) }
        }
        onNodeWithTag(LoanChargeFormTestTags.DUE_DATE).assertIsDisplayed()
    }

    @Test
    fun theDueDateFieldIsABSENTForADisbursementCharge() = runComposeUiTest {
        setContent {
            KptTheme { LoanChargeFormContent(state = loaded(disbursementCharge), onAction = {}) }
        }
        // The original drew it regardless and then discarded the value in the payload, so an
        // officer could pick a date that went nowhere. The field follows the payload now.
        onNodeWithTag(LoanChargeFormTestTags.DUE_DATE).assertDoesNotExist()
    }

    @Test
    fun anInvalidAmountShowsItsError() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargeFormContent(
                    state = loaded(dueDateCharge).copy(amount = "abc", amountError = true),
                    onAction = {},
                )
            }
        }
        onNodeWithText("Enter a valid amount").assertIsDisplayed()
    }

    @Test
    fun submittingWithNoChargeChosenShowsThatError() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargeFormContent(state = loaded().copy(chargeError = true), onAction = {})
            }
        }
        onNodeWithText("Select a charge").assertIsDisplayed()
    }

    @Test
    fun aTenantWithNoUsableChargeTypeSAYSSoRatherThanDrawingAnEmptyForm() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargeFormContent(
                    state = LoanChargeFormState(loanId = 7, chargeTypes = emptyList()),
                    onAction = {},
                )
            }
        }
        // An empty dropdown over a live submit button says nothing; this states the situation.
        onNodeWithTag(LoanChargeFormTestTags.NO_TYPES).assertIsDisplayed()
        onNodeWithTag(LoanChargeFormTestTags.SUBMIT).assertDoesNotExist()
    }

    @Test
    fun aFailedTemplateReadReplacesTheFormWithARetry() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargeFormContent(
                    state = LoanChargeFormState(loanId = 7, typesError = true),
                    onAction = {},
                )
            }
        }
        onNodeWithTag(LoanChargeFormTestTags.TYPES_ERROR).assertIsDisplayed()
        onNodeWithTag(LoanChargeFormTestTags.CHARGE_TYPE).assertDoesNotExist()
    }

    @Test
    fun submitIsDisabledWhileTheChargeIsInFlight() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargeFormContent(
                    state = loaded(dueDateCharge).copy(isSubmitting = true),
                    onAction = {},
                )
            }
        }
        onNodeWithTag(LoanChargeFormTestTags.SUBMIT).assertIsNotEnabled()
    }

    @Test
    fun theOutcomeIsADialog() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargeFormContent(
                    state = loaded(dueDateCharge).copy(
                        dialogState = LoanChargeFormState.DialogState.Failed(
                            "charge is not applicable to this loan product",
                        ),
                    ),
                    onAction = {},
                )
            }
        }
        // The original fired snackbars from the enclosing LIST screen's callbacks, two lambdas up.
        onNodeWithTag(LoanChargeFormTestTags.STATUS_DIALOG).assertIsDisplayed()
        onNodeWithText("charge is not applicable to this loan product").assertIsDisplayed()
    }
}
