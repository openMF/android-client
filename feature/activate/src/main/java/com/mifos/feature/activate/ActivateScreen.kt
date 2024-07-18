@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.activate

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.client.ActivatePayload
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ActivateScreen(
    id: Int,
    activateType: String,
    onBackPressed: () -> Unit
) {

    val viewModel: ActivateViewModel = hiltViewModel()
    val state by viewModel.activateUiState.collectAsStateWithLifecycle()

    ActivateScreen(
        state = state,
        onActivate = {
            when (activateType) {
                Constants.ACTIVATE_CLIENT -> viewModel.activateClient(
                    clientId = id,
                    clientPayload = it
                )

                Constants.ACTIVATE_CENTER -> viewModel.activateCenter(
                    centerId = id,
                    centerPayload = it
                )

                Constants.ACTIVATE_GROUP -> viewModel.activateGroup(
                    groupId = id,
                    groupPayload = it
                )

                else -> {}
            }
        },
        onBackPressed = onBackPressed
    )
}

@Composable
fun ActivateScreen(
    state: ActivateUiState,
    onActivate: (ActivatePayload) -> Unit,
    onBackPressed: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_activate),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is ActivateUiState.ActivatedSuccessfully -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = state.message),
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }

                is ActivateUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {}

                is ActivateUiState.Loading -> MifosCircularProgress()

                is ActivateUiState.Initial -> ActivateContent(onActivate = onActivate)
            }
        }
    }
}

@Composable
fun ActivateContent(
    onActivate: (ActivatePayload) -> Unit
) {

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var activateDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activateDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let {
                            activateDate = it
                        }
                    }
                ) { Text(stringResource(id = R.string.feature_activate_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_activate_cancel)) }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }

    MifosDatePickerTextField(
        value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
            activateDate
        ),
        label = R.string.feature_activate_activation_date,
        openDatePicker = {
            showDatePicker = true
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            onActivate(
                ActivatePayload(
                    activationDate = activateDate.toString()
                )
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(44.dp)
            .padding(start = 16.dp, end = 16.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
        )
    ) {
        Text(text = stringResource(id = R.string.feature_activate), fontSize = 16.sp)
    }
}


class ActivateUiStateProvider : PreviewParameterProvider<ActivateUiState> {

    override val values: Sequence<ActivateUiState>
        get() = sequenceOf(
            ActivateUiState.Loading,
            ActivateUiState.Error(R.string.feature_activate_failed_to_activate_client),
            ActivateUiState.ActivatedSuccessfully(R.string.feature_activate_client),
            ActivateUiState.Initial
        )
}

@Preview(showBackground = true)
@Composable
private fun ActivateScreenPreview(
    @PreviewParameter(ActivateUiStateProvider::class) state: ActivateUiState
) {
    ActivateScreen(
        state = state,
        onActivate = {},
        onBackPressed = {}
    )
}