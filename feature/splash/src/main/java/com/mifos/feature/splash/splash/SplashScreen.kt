package com.mifos.feature.splash.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.SummerSky
import com.mifos.feature.splash.R

@Composable
fun SplashScreen(
    viewmodel: SplashScreenViewmodel = hiltViewModel(),
    navigatePasscode: () -> Unit,
    navigateLogin: () -> Unit
) {
    val state by viewmodel.isAuthenticated.collectAsStateWithLifecycle()

    SplashScreen(
        state = state,
        navigatePasscode = navigatePasscode,
        navigateLogin = navigateLogin
    )
}

@Composable
fun SplashScreen(
    state: Boolean?,
    navigatePasscode: () -> Unit,
    navigateLogin: () -> Unit
) {

    when (state) {
        false -> navigateLogin()
        true -> navigatePasscode()
        else -> {}
    }

    MifosScaffold(
        containerColor = SummerSky
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.feature_splash_icon),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        state = false,
        navigatePasscode = {},
        navigateLogin = {}
    )
}