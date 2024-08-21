package com.mifos.feature.passcode.navigation

sealed class PasscodeScreens(val route: String) {

    data object PasscodeScreen : PasscodeScreens("passcode_screen")

}