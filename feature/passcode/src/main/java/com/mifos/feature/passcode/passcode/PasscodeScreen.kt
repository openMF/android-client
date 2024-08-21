package com.mifos.feature.passcode.passcode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold

@Composable
fun PasscodeScreen(
    viewmodel: PasscodeViewmodel = hiltViewModel()
) {

    val passcodeStatus by viewmodel.passcodeStatus.collectAsStateWithLifecycle()


}

@Composable
fun PasscodeScreen(

) {

    MifosScaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            com.mifos.compose.component.PasscodeScreen(
                onForgotButton = { },
                onSkipButton = { },
                onPasscodeConfirm = { },
                onPasscodeRejected = { }
            )
        }
    }
}

@Preview
@Composable
private fun PasscodeScreenPreview() {
    PasscodeScreen()
}