/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mifos.feature.client.fixedDepositAccount

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier

import com.mifos.feature.client.fixedDepositAccount.details.DetailsStepRoute
import com.mifos.feature.client.fixedDepositAccount.details.detailsStepDestination
import com.mifos.feature.client.fixedDepositAccount.details.navigateToDetailsStep
import com.mifos.feature.client.fixedDepositAccount.currency.currencyStepDestination
import com.mifos.feature.client.fixedDepositAccount.currency.navigateToCurrencyStep
import com.mifos.feature.client.fixedDepositAccount.terms.termsStepDestination
import com.mifos.feature.client.fixedDepositAccount.terms.navigateToTermsStep
import com.mifos.feature.client.fixedDepositAccount.settings.settingsStepDestination
import com.mifos.feature.client.fixedDepositAccount.settings.navigateToSettingsStep
import com.mifos.feature.client.fixedDepositAccount.charges.chargesStepDestination
import com.mifos.feature.client.fixedDepositAccount.charges.navigateToChargesStep
import com.mifos.feature.client.fixedDepositAccount.preview.previewStepDestination
import com.mifos.feature.client.fixedDepositAccount.preview.navigateToPreviewStep

@Composable
fun FixedDepositAccountFlowScreen(
    clientId: Int,
    navigateBack: () -> Unit,
    onAccountCreated: (accountId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = DetailsStepRoute(clientId),
        modifier = modifier
    ) {
        
        detailsStepDestination(
            navController = navController,
            navigateBack = navigateBack,
            onNext = { navController.navigateToCurrencyStep(clientId) }
        )
        
        
        currencyStepDestination(
            navController = navController,
            navigateBack = { navController.popBackStack() },
            onNext = { navController.navigateToTermsStep(clientId) }
        )
        
        
        termsStepDestination(
            navController = navController,
            navigateBack = { navController.popBackStack() },
            onNext = { navController.navigateToSettingsStep(clientId) }
        )
        
        
        settingsStepDestination(
            navController = navController,
            navigateBack = { navController.popBackStack() },
            onNext = { navController.navigateToChargesStep(clientId) }
        )
        
        
        chargesStepDestination(
            navController = navController,
            navigateBack = { navController.popBackStack() },
            onNext = { navController.navigateToPreviewStep(clientId) }
        )
        
        
        previewStepDestination(
            navController = navController,
            navigateBack = { navController.popBackStack() },
            onSubmit = { 
                
                onAccountCreated("accountId")
            }
        )
    }

}
