/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.loan.charges

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import kpt.core.base.store.screen.ScreenState
import kpt.core.designsystem.theme.KptTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class LoanChargesScreenUiTest {

    private fun row(id: Int) = LoanChargeRow(
        id = id,
        chargeId = "11",
        name = "Processing Fee",
        amount = "₹1,500.00",
        dueDate = "14 Mar 2026",
    )

    @Test
    fun aChargeCardRendersAllFourLabelledRows() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargesContent(
                    charges = ScreenState.Content(listOf(row(1))),
                    onAction = {},
                )
            }
        }
        onNodeWithTag(LoanChargesTestTags.LIST).assertIsDisplayed()
        onNodeWithTag(LoanChargesTestTags.row(1)).assertIsDisplayed()
        onNodeWithText("Charge ID").assertIsDisplayed()
        onNodeWithText("Charge Name").assertIsDisplayed()
        onNodeWithText("Charge Amount").assertIsDisplayed()
        onNodeWithText("Charge Due Date").assertIsDisplayed()
        onNodeWithText("₹1,500.00").assertIsDisplayed()
    }

    @Test
    fun twoChargesOfTheSameTypeBothRender() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargesContent(
                    charges = ScreenState.Content(listOf(row(1), row(2))),
                    onAction = {},
                )
            }
        }
        // Keyed on the LEVIED charge's id, so a loan charged the same fee twice does not collapse
        // to one row — `chargeId` is the definition id and repeats.
        onNodeWithTag(LoanChargesTestTags.row(1)).assertIsDisplayed()
        onNodeWithTag(LoanChargesTestTags.row(2)).assertIsDisplayed()
    }

    @Test
    fun aLoanWithNoChargesRendersTheEmptyState() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargesContent(charges = ScreenState.Empty, onAction = {})
            }
        }
        onNodeWithTag(LoanChargesTestTags.EMPTY).assertIsDisplayed()
        // The original's copy read "No Loan Charges Available for this client" — on the LOAN
        // charges screen, reached from a loan.
        onNodeWithText("No charges on this loan").assertIsDisplayed()
        onNodeWithTag(LoanChargesTestTags.LIST).assertDoesNotExist()
    }

    @Test
    fun theAddActionIsABSENTWhenNoCallerSuppliesIt() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargesContent(
                    charges = ScreenState.Content(listOf(row(1))),
                    onAction = {},
                    canAddCharge = false,
                )
            }
        }
        // The gating itself, independent of whether the form happens to be ported. It WAS the
        // reason the "+" was hidden until S4h; the form is live now and `LoanNavigation` passes a
        // non-null callback, so this test pins the mechanism rather than that fact.
        onNodeWithTag(LoanChargesTestTags.ADD).assertDoesNotExist()
    }

    @Test
    fun theAddActionIsDrawnWhenTheFormExists() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargesContent(
                    charges = ScreenState.Content(listOf(row(1))),
                    onAction = {},
                    canAddCharge = true,
                )
            }
        }
        onNodeWithTag(LoanChargesTestTags.ADD).assertIsDisplayed()
    }

    @Test
    fun theScreenDrawsItsChromeWhileLoading() = runComposeUiTest {
        setContent {
            KptTheme {
                LoanChargesContent(charges = ScreenState.Loading, onAction = {})
            }
        }
        onNodeWithTag(LoanChargesTestTags.SCREEN).assertIsDisplayed()
        onNodeWithText("Loan Charges").assertIsDisplayed()
    }
}
