package com.mifos.mifosxdroid.online.createnewcenter

import android.icu.util.Calendar
import android.text.format.DateFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifos.core.data.CenterPayload
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.White
import com.mifos.mifosxdroid.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateNewCenterScreen(
    viewModel: CreateNewCenterViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    var centerName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var isOfficeListDropDownExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var office by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var officeId by rememberSaveable {
        mutableIntStateOf(0)
    }
    var isActiveSelected by rememberSaveable {
        mutableStateOf(false)
    }
    val activationDatePickerState =
        rememberDatePickerState(initialSelectedDateMillis = Calendar.getInstance().timeInMillis)
    var openActivationDatePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var activationDate by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                DateFormat.format(
                    "dd MMM yyyy",
                    activationDatePickerState.selectedDateMillis!!
                ).toString()
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_center),
                        fontSize = 24.sp
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MifosOutlinedTextField(
                value = centerName,
                onValueChange = {
                    centerName = it
                },
                label = R.string.name,
                error = null,
                padding = 0.dp
            )
            ExposedDropdownMenuBox(
                expanded = isOfficeListDropDownExpanded,
                onExpandedChange = {
                    isOfficeListDropDownExpanded = !isOfficeListDropDownExpanded
                }
            ) {
                MifosOutlinedTextField(
                    value = office,
                    onValueChange = {},
                    label = R.string.office,
                    error = null,
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isOfficeListDropDownExpanded)
                    },
                    padding = 0.dp
                )
                DropdownMenu(
                    expanded = isOfficeListDropDownExpanded,
                    onDismissRequest = {
                        isOfficeListDropDownExpanded = false
                    },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    uiState.offices.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = it.name!!
                                )
                            },
                            onClick = {
                                office = TextFieldValue(it.name!!)
                                officeId = it.id!!
                                isOfficeListDropDownExpanded = false
                            }
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .combinedClickable {
                        isActiveSelected = !isActiveSelected
                    }
            ) {
                RadioButton(
                    selected = isActiveSelected,
                    onClick = {
                        isActiveSelected = !isActiveSelected
                    },
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(id = R.string.active)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (isActiveSelected) {
                MifosOutlinedTextField(
                    value = activationDate,
                    onValueChange = {
                        activationDate = it
                    },
                    label = R.string.activation_date,
                    error = null,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                openActivationDatePickerDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = null
                            )
                        }
                    },
                    padding = 0.dp
                )
                if (openActivationDatePickerDialog) {
                    DatePickerDialog(
                        onDismissRequest = {
                            openActivationDatePickerDialog = false
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openActivationDatePickerDialog = false
                                    activationDate = TextFieldValue(
                                        DateFormat.format(
                                            "dd MMM yyyy",
                                            activationDatePickerState.selectedDateMillis!!
                                        ).toString()
                                    )
                                }
                            ) {
                                Text(text = "OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openActivationDatePickerDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = activationDatePickerState)
                    }
                }
            }
            Button(
                onClick = {
                    viewModel.initiateCenterCreation(
                        CenterPayload(
                            name = centerName.text,
                            active = isActiveSelected,
                            activationDate = activationDate.text,
                            officeId = officeId,
                            dateFormat = "dd MMMM yyyy",
                            locale = "en"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.submit),
                    fontSize = 16.sp
                )
            }

            if (uiState.isLoading) {
                ProgressDialog(
                    onDismissRequest = {
                        viewModel.dismissDialog()
                    }
                )
            }
            if(!uiState.message.isNullOrEmpty()){
                LaunchedEffect(uiState.message) {
                    snackbarHostState.showSnackbar(uiState.message)
                    viewModel.resetErrorMessage()
                }
            }
        }
    }
}

@Composable
fun ProgressDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        CircularProgressIndicator(color = White)
    }
}