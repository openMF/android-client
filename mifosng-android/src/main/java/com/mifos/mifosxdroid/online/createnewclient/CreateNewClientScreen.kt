package com.mifos.mifosxdroid.online.createnewclient

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.application.App.Companion.context
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.mifosxdroid.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 07/07/2024 (3:45 AM)
 */

@Composable
fun CreateNewClientScreen(
    navigateBack: () -> Unit
) {
    val viewmodel: CreateNewClientViewModel = hiltViewModel()
    val uiState by viewmodel.createNewClientUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.loadClientTemplate()
    }

    CreateNewClientScreen(
        uiState = uiState,
        onRetry = { viewmodel.loadClientTemplate() },
        navigateBack = navigateBack,
        sortStaffInOffice = {
            viewmodel.filterStaff(it)
        },
        sortOffices = {
            viewmodel.filterOffices(it)
        }
    )
}

@Composable
fun CreateNewClientScreen(
    uiState: CreateNewClientUiState,
    onRetry: () -> Unit,
    navigateBack: () -> Unit,
    sortStaffInOffice: (List<Staff>) -> List<String>,
    sortOffices: (List<Office>) -> List<String>

) {
    var officeList: List<String>? by rememberSaveable {
        mutableStateOf(null)
    }
    var staffInOffices: List<String>? by rememberSaveable {
        mutableStateOf(null)
    }
    var clientStaff: List<Office>? = null

    val context = LocalContext.current


    Box(modifier = Modifier.fillMaxSize()) {
        CreateNewClientContent()

        when (uiState) {
            is CreateNewClientUiState.SetClientId -> {
                Log.d("uiStateUwU ", uiState.toString())

            }

            is CreateNewClientUiState.ShowClientCreatedSuccessfully -> {
                Log.d("uiStateUwU ", uiState.toString())

                Toast.makeText(
                    context,
                    uiState.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is CreateNewClientUiState.ShowClientTemplate -> {
                Log.d("uiStateUwU ", uiState.toString())

                CreateNewClientContent()
            }

            is CreateNewClientUiState.ShowMessage -> {
                Log.d("uiStateUwU ", uiState.toString())

//check for Int or String msg
                MifosSweetError(
                    message = stringResource(id = uiState.message),
                    onclick = onRetry
                )
            }

            is CreateNewClientUiState.ShowMessageString -> {
                Log.d("uiStateUwU ", uiState.toString())

                Toast.makeText(
                    context,
                    uiState.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is CreateNewClientUiState.ShowOffices -> {
                Log.d("uiStateUwU ", uiState.toString())

                officeList = sortOffices.invoke(uiState.officeList)
            }

            is CreateNewClientUiState.ShowProgress -> {
                Log.d("uiStateUwU ", uiState.toString())

                MifosCircularProgress(uiState.message)
            }

            CreateNewClientUiState.ShowProgressbar -> {
                Log.d("uiStateUwU ", uiState.toString())

                MifosCircularProgress()
            }

            is CreateNewClientUiState.ShowStaffInOffices -> {
                Log.d("uiStateUwU ", uiState.toString())

                if (uiState.staffList.isEmpty()) {
                    Toast.makeText(
                        context,
                        stringResource(id = R.string.no_staff_associated_with_office),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                staffInOffices = sortStaffInOffice.invoke(uiState.staffList)
            }

            is CreateNewClientUiState.ShowWaitingForCheckerApproval -> {
                Log.d("uiStateUwU ", uiState.toString())

                Toast.makeText(
                    context,
                    stringResource(id = uiState.message),
                    Toast.LENGTH_SHORT
                ).show()

                navigateBack.invoke()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showSystemUi = true)
fun CreateNewClientContent(modifier: Modifier = Modifier) {
    var firstName by rememberSaveable {
        mutableStateOf("")
    }
    var middleName by rememberSaveable {
        mutableStateOf("")
    }
    var lastName by rememberSaveable {
        mutableStateOf("")
    }
    var mobileNumber by rememberSaveable {
        mutableStateOf("")
    }
    var externalId by rememberSaveable {
        mutableStateOf("")
    }
    var gender by rememberSaveable {
        mutableStateOf("")
    }
    var client by rememberSaveable {
        mutableStateOf("")
    }
    var clientClassification by rememberSaveable {
        mutableStateOf("")
    }
    var office by rememberSaveable {
        mutableStateOf("")
    }
    var staff by rememberSaveable {
        mutableStateOf("")
    }
    var isActive by rememberSaveable {
        mutableStateOf(false)
    }

    var dateOfBirth by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var activationDate by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var showDateOfBirthDatepicker by rememberSaveable {
        mutableStateOf(false)
    }
    var showActivateDatepicker by rememberSaveable {
        mutableStateOf(false)
    }
    var imageClicked by rememberSaveable {
        mutableStateOf(false)
    }
    var pickImage by rememberSaveable {
        mutableStateOf(false)
    }
    var takePhoto by rememberSaveable {
        mutableStateOf(false)
    }


    val activateDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activationDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    val dateOfBirthDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateOfBirth,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    var imageUri: Uri? by rememberSaveable {
        mutableStateOf(null)
    }
    var bitmap: Bitmap? by rememberSaveable { mutableStateOf(null) }
    var imageSelected by rememberSaveable {
        mutableStateOf(false)
    }


    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val context = LocalContext.current

//    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_dp_placeholder)


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri

        imageUri?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                bitmap = ImageDecoder.decodeBitmap(source)
                /**
                 * check for hardware bitmaps f a
                 * s
                 * fd sf
                 *  dfsfd
                 */
            }
        }

        if (imageUri == null) {
            Toast.makeText(context, R.string.image_not_selected, Toast.LENGTH_SHORT).show()
        } else {
            imageSelected = true

        }
    }


    if (showActivateDatepicker || showDateOfBirthDatepicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDateOfBirthDatepicker = false
                showActivateDatepicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (showActivateDatepicker) {
                            activateDatePickerState.selectedDateMillis?.let {
                                activationDate = it
                            }
                        } else {
                            dateOfBirthDatePickerState.selectedDateMillis?.let {
                                dateOfBirth = it
                            }
                        }
                        showActivateDatepicker = false
                        showDateOfBirthDatepicker = false
                    }
                ) { Text(stringResource(id = R.string.select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showActivateDatepicker = false
                        showDateOfBirthDatepicker = false
                    }
                ) { Text(stringResource(id = R.string.cancel)) }
            }
        )
        {
            DatePicker(state = if (showActivateDatepicker) activateDatePickerState else dateOfBirthDatePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = MaterialTheme.typography.headlineSmall,
            text = stringResource(id = R.string.create_new_client),
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        ) {
            (if (imageSelected) bitmap?.asImageBitmap() else BitmapFactory.decodeResource(
                context.resources,
                R.drawable.ic_dp_placeholder
            ).asImageBitmap())?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickable {
                            imageClicked = !imageClicked
                        }
                        .border(color = DarkGray, width = 2.dp, shape = CircleShape)
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }
            DropdownMenu(
                expanded = imageClicked,
                onDismissRequest = { imageClicked = false },
            ) {
                MifosMenuDropDownItem(
                    option = stringResource(id = R.string.take_a_photo),
                    onClick = {
                        imageClicked = false
                        takePhoto = true
                    }
                )
                MifosMenuDropDownItem(
                    option = stringResource(id = R.string.upload_photo),
                    onClick = {
                        imageClicked = false
                        imagePickerLauncher.launch("image/*")
                    }
                )
                MifosMenuDropDownItem(
                    option = stringResource(id = R.string.remove_existing_photo),
                    onClick = {
                        imageUri = null
                        bitmap = null
                        imageClicked = false

                    }
                )
            }
        }




        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = stringResource(id = R.string.first_name_mandatory),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = stringResource(id = R.string.middle_name),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = stringResource(id = R.string.last_name_mandatory),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            label = stringResource(id = R.string.mobile_no),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = { externalId = it },
            label = stringResource(id = R.string.external_id),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = gender,
            onValueChanged = { gender = it },
            onOptionSelected = { _, _ ->

            },
            label = R.string.gender,
            options = listOf(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                dateOfBirth
            ),
            label = R.string.dob,
            openDatePicker = { showDateOfBirthDatepicker = !showDateOfBirthDatepicker }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = client,
            onValueChanged = { client = it },
            onOptionSelected = { _, _ -> },
            label = R.string.client,
            options = listOf(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = clientClassification,
            onValueChanged = { clientClassification = it },
            onOptionSelected = { _, _ -> },
            label = R.string.client_classification,
            options = listOf(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = office,
            onValueChanged = { office = it },
            onOptionSelected = { _, _ -> },
            label = R.string.office,
            options = listOf(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = office,
            onValueChanged = { office = it },
            onOptionSelected = { _, _ -> },
            label = R.string.office_name_mandatory,
            options = listOf(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = staff,
            onValueChanged = { staff = it },
            onOptionSelected = { _, _ -> },
            label = R.string.staff,
            options = listOf(),
            readOnly = true

        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isActive,
                onCheckedChange = { isActive = !isActive },
                colors = CheckboxDefaults.colors(
                    if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                )
            )
            Text(text = stringResource(id = R.string.client_active))
        }


        AnimatedVisibility(
            visible = isActive,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MifosDatePickerTextField(
                value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                    activationDate
                ),
                label = R.string.center_submission_date,
                openDatePicker = { showActivateDatepicker = !showActivateDatepicker }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(46.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            ),
            onClick = {
                if (isAllFieldsValid(
                        context = context,
                        firstName = firstName,
                        middleName = middleName,
                        lastName = lastName
                    )
                ) {
                    if (com.mifos.core.common.utils.Network.isOnline(context)) {

                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.error_not_connected_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }) {
            Text(text = stringResource(id = R.string.submit))
        }

    }
}


fun isAllFieldsValid(
    context: Context,
    firstName: String,
    middleName: String,
    lastName: String,

    ): Boolean {
    return when {
        !isFirstNameValid(firstName, context) -> {
            false
        }

        !isMiddleNameValid(middleName, context) -> {
            false
        }

        !isLastNameValid(lastName, context) -> {
            false
        }

        else -> true
    }
}

fun isFirstNameValid(name: String, context: Context): Boolean {
    return when {
        name.isEmpty() -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.error_first_name_can_not_be_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.error_first_name_should_contain_only_alphabets),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        else -> true
    }
}

fun isLastNameValid(name: String, context: Context): Boolean {
    return when {
        name.isEmpty() -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.error_last_name_can_not_be_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.error_last_name_should_contain_only_alphabets),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        else -> true
    }
}

fun isMiddleNameValid(name: String, context: Context): Boolean {
    return when {
        name.isEmpty() -> {
            true
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.error_middle_name_should_contain_only_alphabets),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        else -> true
    }
}

fun convertBitmapToPng(bitmap: Bitmap): ByteArray? {
    val outputStream = ByteArrayOutputStream()
    val compressed =
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // 100 for lossless PNG
    if (compressed) {
        return outputStream.toByteArray()
    }
    return null
}