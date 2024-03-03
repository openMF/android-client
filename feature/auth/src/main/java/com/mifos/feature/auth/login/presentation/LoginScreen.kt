package com.mifos.feature.auth.login.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifos.core.designsystem.component.MifosAndroidClientIcon
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGrey
import com.mifos.core.designsystem.theme.White
import com.mifos.feature.auth.R

/**
 * Created by Aditya Gupta on 11/02/24.
 */

@Composable
fun LoginScreen(
    homeIntent: () -> Unit,
    passcodeIntent: () -> Unit
) {

    val loginViewModel: LoginViewModel = hiltViewModel()
    val state = loginViewModel.loginUiState.collectAsState().value
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val showDialog = rememberSaveable { mutableStateOf(false) }

    var userName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }

    val usernameError: MutableState<Int?> = remember { mutableStateOf(null) }
    val passwordError: MutableState<Int?> = remember { mutableStateOf(null) }

    when (state) {
        is LoginUiState.Empty -> {}

        is LoginUiState.ShowError -> {
            showDialog.value = false
            LaunchedEffect(key1 = state.message) {
                snackbarHostState.showSnackbar(message = context.getString(state.message))
            }
        }

        is LoginUiState.ShowProgress -> {
            showDialog.value = true
        }

        is LoginUiState.ShowValidationError -> {
            usernameError.value = state.usernameError
            passwordError.value = state.passwordError
        }

        LoginUiState.HomeActivityIntent -> {
            showDialog.value = false
            homeIntent()
        }

        LoginUiState.PassCodeActivityIntent -> {
            showDialog.value = false
            passcodeIntent()
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier = Modifier.height(80.dp))

            MifosAndroidClientIcon(R.drawable.mifos_logo)

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = stringResource(id = R.string.feature_auth_enter_credentials),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    color = DarkGrey
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            MifosOutlinedTextField(
                value = userName,
                onValueChange = { value ->
                    userName = value
                },
                icon = Icons.Filled.Person,
                label = R.string.feature_auth_username,
                error = usernameError.value,
                trailingIcon = {
                    if (usernameError.value != null) {
                        Icon(imageVector = Icons.Filled.Error, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            MifosOutlinedTextField(
                value = password,
                onValueChange = { value ->
                    password = value
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                icon = Icons.Filled.Lock,
                label = R.string.feature_auth_password,
                error = passwordError.value,
                trailingIcon = {
                    if (passwordError.value == null) {
                        val image = if (passwordVisibility)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(imageVector = image, null)
                        }
                    } else {
                        Icon(imageVector = Icons.Filled.Error, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { loginViewModel.validateUserInputs(userName.text, password.text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp)
                    .padding(start = 16.dp, end = 16.dp),
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                )
            ) {
                Text(text = "Login", fontSize = 16.sp)
            }
        }
        if (showDialog.value) {
            Dialog(
                onDismissRequest = { showDialog.value },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                CircularProgressIndicator(color = White)
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_7")
@Composable
fun LoginScreenPreview() {
    LoginScreen({}, {})
}