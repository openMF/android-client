package com.mifos.feature.client.createNewClient

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.telephony.PhoneNumberUtils
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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.noncore.DataTable
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.templates.clients.ClientsTemplate
import com.mifos.feature.client.R
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Objects

/**
 * Created by Pronay Sarker on 07/07/2024 (3:45 AM)
 */

@Composable
fun CreateNewClientScreen(
    navigateBack: () -> Unit,
    hasDatatables: (datatables: List<DataTable>, clientPayload: ClientPayload) -> Unit
) {
    val viewmodel: CreateNewClientViewModel = hiltViewModel()
    val uiState by viewmodel.createNewClientUiState.collectAsStateWithLifecycle()
    val officeList by viewmodel.showOffices.collectAsStateWithLifecycle()
    val staffInOffice by viewmodel.staffInOffices.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.loadOfficeAndClientTemplate()
    }

    CreateNewClientScreen(
        uiState = uiState,
        officeList = officeList,
        staffInOffices = staffInOffice,
        onRetry = { viewmodel.loadOfficeAndClientTemplate() },
        navigateBack = navigateBack,
        loadStaffInOffice = { viewmodel.loadStaffInOffices(it) },
        createClient = { viewmodel.createClient(clientPayload = it) },
        uploadImage = { id, uri ->
            viewmodel.uploadImage(id, uri.toFile())
        },
        hasDatatables = hasDatatables
    )
}

@Composable
fun CreateNewClientScreen(
    uiState: CreateNewClientUiState,
    onRetry: () -> Unit,
    officeList: List<Office>,
    staffInOffices: List<Staff>,
    loadStaffInOffice: (officeId: Int) -> Unit,
    navigateBack: () -> Unit,
    createClient: (clientPayload: ClientPayload) -> Unit,
    uploadImage: (id: Int, imageUri: Uri) -> Unit,
    hasDatatables: (datatables: List<DataTable>, clientPayload: ClientPayload) -> Unit
) {
    val context = LocalContext.current
    var createClientWithImage by rememberSaveable { mutableStateOf(false) }
    var clientImageUri: Uri? by rememberSaveable { mutableStateOf(null) }

    Box {
        when (uiState) {
            CreateNewClientUiState.ShowProgressbar -> {
                MifosCircularProgress()
            }

            is CreateNewClientUiState.ShowProgress -> {
                MifosCircularProgress(text = uiState.message)
            }

            is CreateNewClientUiState.ShowClientTemplate -> {
                CreateNewClientContent(
                    officeList = officeList,
                    staffInOffices = staffInOffices,
                    clientTemplate = uiState.clientsTemplate,
                    loadStaffInOffice = loadStaffInOffice,
                    createClient = createClient,
                    onHasDatatables = hasDatatables,
                    setUriForUpload = { uri ->
                        if (uri.path?.isNotEmpty() == true) {
                            clientImageUri = uri
                            createClientWithImage = true
                        }
                    }
                )
            }

            is CreateNewClientUiState.SetClientId -> {
                if (createClientWithImage) {
                    clientImageUri?.let { uploadImage(uiState.id, it) }
                } else navigateBack.invoke()
            }

            is CreateNewClientUiState.ShowClientCreatedSuccessfully -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_LONG).show()
            }

            is CreateNewClientUiState.OnImageUploadSuccess -> {
                Toast.makeText(context, stringResource(id = uiState.message), Toast.LENGTH_SHORT)
                    .show()
                navigateBack.invoke()
            }

            is CreateNewClientUiState.ShowWaitingForCheckerApproval -> {
                Toast.makeText(context, stringResource(id = uiState.message), Toast.LENGTH_SHORT)
                    .show()
                navigateBack.invoke()
            }

            is CreateNewClientUiState.ShowError -> {
                MifosSweetError(
                    message = stringResource(id = uiState.message),
                    onclick = { onRetry() }
                )
            }

            is CreateNewClientUiState.ShowStringError -> {
                MifosSweetError(
                    message = uiState.message,
                    onclick = { onRetry() },
                    buttonText = stringResource(id = R.string.feature_client_go_back)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateNewClientContent(
    officeList: List<Office>,
    staffInOffices: List<Staff>,
    clientTemplate: ClientsTemplate,
    loadStaffInOffice: (officeId: Int) -> Unit,
    createClient: (clientPayload: ClientPayload) -> Unit,
    onHasDatatables: (datatables: List<DataTable>, clientPayload: ClientPayload) -> Unit,
    setUriForUpload: (uri: Uri) -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var middleName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var externalId by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var genderId by rememberSaveable { mutableIntStateOf(0) }

    var client by rememberSaveable { mutableStateOf("") }
    var selectedClientId by rememberSaveable { mutableIntStateOf(0) }
    var clientClassification by rememberSaveable { mutableStateOf("") }
    var selectedClientClassificationId by rememberSaveable { mutableIntStateOf(0) }
    var selectedOffice by rememberSaveable { mutableStateOf("") }
    var selectedOfficeId: Int? by rememberSaveable { mutableStateOf(0) }
    var staff by rememberSaveable { mutableStateOf("") }
    var selectedStaffId: Int? by rememberSaveable { mutableStateOf(0) }

    var isActive by rememberSaveable { mutableStateOf(false) }
    var dateOfBirth by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var activationDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var showDateOfBirthDatepicker by rememberSaveable { mutableStateOf(false) }
    var showActivateDatepicker by rememberSaveable { mutableStateOf(false) }
    var showImagePickerDialog by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var handleImageSelection by remember { mutableStateOf(false) }
    var permissionList: List<String> by rememberSaveable { mutableStateOf(listOf()) }
    var imagePickerActionType by rememberSaveable { mutableStateOf(ImagePickerType.GALLERY) }
    var selectedImageUri: Uri by rememberSaveable { mutableStateOf(Uri.EMPTY) }

    val file = context.createTempImageFile()
    val imgUri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.mifos.mifosxdroid" + ".provider",
        file
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                selectedImageUri = imgUri
            }
            handleImageSelection = false
        }
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
        handleImageSelection = false
    }

    val hasDatatables by rememberSaveable {
        mutableStateOf(
            clientTemplate.dataTables.isNotEmpty()
        )
    }

    LaunchedEffect(key1 = Unit) {
        if (officeList.isNotEmpty()) {
            officeList[0].id?.let { loadStaffInOffice.invoke(it) }
        }
    }
    LaunchedEffect(key1 = staffInOffices) {
        if(staffInOffices.isEmpty()){
            Toast.makeText(context, context.resources.getString(R.string.feature_client_no_staff_associated_with_office), Toast.LENGTH_SHORT).show()
            staff = ""
            selectedStaffId = 0
        }
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

    if (handleImageSelection) {
        PermissionBox(
            requiredPermissions = permissionList,
            title = R.string.feature_client_permissions_required,
            description = R.string.feature_client_please_grant_us_the_following_permission,
            confirmButtonText = R.string.feature_client_proceed,
            dismissButtonText = R.string.feature_client_skip,
            onGranted = {
                LaunchedEffect(key1 = Unit) {
                    if (imagePickerActionType == ImagePickerType.GALLERY) {
                        imagePickerLauncher.launch("image/png")
                    } else {
                        cameraLauncher.launch(imgUri)
                    }
                }
            }
        )
    }

    if (showImagePickerDialog) {
        MifosSelectImageDialog(
            onDismissRequest = { showImagePickerDialog = false },
            takeImage = {
                showImagePickerDialog = false
                val requiredPermissions = listOf(Manifest.permission.CAMERA)

                imagePickerActionType = ImagePickerType.CAMERA
                permissionList = requiredPermissions
                handleImageSelection = true
            },
            uploadImage = {
                showImagePickerDialog = false

                val requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
                    listOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                } else {
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }

                imagePickerActionType = ImagePickerType.GALLERY
                permissionList = requiredPermissions
                handleImageSelection = true
            },
            removeImage = {
                showImagePickerDialog = false
                selectedImageUri = Uri.EMPTY
            }
        )
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
                ) { Text(stringResource(id = R.string.feature_client_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showActivateDatepicker = false
                        showDateOfBirthDatepicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_client_cancel)) }
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
            text = stringResource(id = R.string.feature_client_create_new_client),
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        ) {
            Image(
                painter = if (selectedImageUri.path?.isNotEmpty() == true) rememberAsyncImagePainter(
                    selectedImageUri
                ) else painterResource(
                    id = R.drawable.feature_client_ic_dp_placeholder
                ),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable {
                        showImagePickerDialog = true
                    }
                    .border(color = DarkGray, width = 2.dp, shape = CircleShape)
                    .size(80.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = stringResource(id = R.string.feature_client_first_name_mandatory),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = stringResource(id = R.string.feature_client_middle_name),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = stringResource(id = R.string.feature_client_last_name_mandatory),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            label = stringResource(id = R.string.feature_client_mobile_no),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = { externalId = it },
            label = stringResource(id = R.string.feature_client_external_id),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = gender,
            onValueChanged = { gender = it },
            onOptionSelected = { index, value ->
                gender = value
                genderId = clientTemplate.genderOptions[index].id
            },
            label = R.string.feature_client_gender,
            options = clientTemplate.genderOptions.map { it.name },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                dateOfBirth
            ),
            label = R.string.feature_client_dob,
            openDatePicker = { showDateOfBirthDatepicker = !showDateOfBirthDatepicker }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = client,
            onValueChanged = { client = it },
            onOptionSelected = { index, value ->
                client = value
                selectedClientId = clientTemplate.clientTypeOptions[index].id
            },
            label = R.string.feature_client_client,
            options = clientTemplate.clientTypeOptions.sortedBy { it.name }.map { it.name },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = clientClassification,
            onValueChanged = { clientClassification = it },
            onOptionSelected = { index, value ->
                clientClassification = value
                selectedClientClassificationId =
                    clientTemplate.clientClassificationOptions[index].id
            },
            label = R.string.feature_client_client_classification,
            options = clientTemplate.clientClassificationOptions.sortedBy { it.name }
                .map { it.name },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedOffice,
            onValueChanged = { selectedOffice = it },
            onOptionSelected = { index, value ->
                selectedOffice = value
                selectedOfficeId = officeList[index].id

                if (selectedOfficeId != null) {
                    scope.launch {
                        loadStaffInOffice.invoke(selectedOfficeId!!)
                    }
                }
            },
            label = R.string.feature_client_office_name_mandatory,
            options = officeList.sortedBy { it.name }.map { it.name.toString() },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = staff,
            onValueChanged = { staff = it },
            onOptionSelected = { index, value ->
                staff = value
                selectedStaffId = staffInOffices[index].id
            },
            label = R.string.feature_client_staff,
            options = staffInOffices.sortedBy { it.displayName }.map { it.displayName.toString() },
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
            Text(text = stringResource(id = R.string.feature_client_client_active))
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
                label = R.string.feature_client_center_submission_date,
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
                        val clientPayload = ClientPayload()

                        //Mandatory fields
                        clientPayload.firstname = firstName
                        clientPayload.lastname = lastName
                        clientPayload.officeId = selectedOfficeId

                        // Optional Fields, we do not need to add any check because these fields carry some
                        //  default values
                        clientPayload.active = isActive
                        clientPayload.activationDate =
                            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                                activationDate
                            )
                        clientPayload.dateOfBirth =
                            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                                dateOfBirth
                            )

                        //Optional Fields
                        if (middleName.isNotEmpty()) {
                            clientPayload.middlename = middleName
                        }
                        if (PhoneNumberUtils.isGlobalPhoneNumber(mobileNumber)) {
                            clientPayload.mobileNo = mobileNumber
                        }
                        if (externalId.isNotEmpty()) {
                            clientPayload.externalId = externalId
                        }
                        if (clientTemplate.genderOptions.isNotEmpty()) {
                            clientPayload.genderId = genderId
                        }
                        if (staffInOffices.isNotEmpty()) {
                            clientPayload.staffId = selectedStaffId
                        }
                        if (clientTemplate.clientTypeOptions.isNotEmpty()) {
                            clientPayload.clientTypeId = selectedClientId
                        }
                        if (clientTemplate.clientClassificationOptions.isNotEmpty()) {
                            clientPayload.clientClassificationId = selectedClientClassificationId
                        }
                        if (hasDatatables) {
                            onHasDatatables.invoke(clientTemplate.dataTables, clientPayload)
                        } else {
                            setUriForUpload.invoke(selectedImageUri)
                            clientPayload.datatables = null
                            createClient.invoke(clientPayload)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.feature_client_error_not_connected_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.feature_client_submit))
        }
    }
}

@Composable
fun MifosSelectImageDialog(
    onDismissRequest: () -> Unit,
    takeImage: () -> Unit,
    uploadImage: () -> Unit,
    removeImage: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.feature_client_please_select_action),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { takeImage() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_client_take_a_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { uploadImage() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_client_upload_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { removeImage() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_client_remove_existing_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

enum class ImagePickerType {
    CAMERA,
    GALLERY
}

fun Context.createTempImageFile(): File {
    val imageFileName = "clients_image"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".png", /* suffix */
        externalCacheDir /* directory */
    )
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
                context.resources.getString(R.string.feature_client_error_first_name_can_not_be_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_client_error_first_name_should_contain_only_alphabets),
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
                context.resources.getString(R.string.feature_client_error_last_name_can_not_be_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_client_error_last_name_should_contain_only_alphabets),
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
                context.resources.getString(R.string.feature_client_error_middle_name_should_contain_only_alphabets),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        else -> true
    }
}

class CreateNewClientScreenPreviewProvider : PreviewParameterProvider<CreateNewClientUiState> {
    override val values: Sequence<CreateNewClientUiState>
        get() = sequenceOf(
            CreateNewClientUiState.ShowClientTemplate(
                ClientsTemplate(
                    officeOptions = listOf(),
                    staffOptions = listOf(),
                    genderOptions = listOf(),
                    clientTypeOptions = listOf(),
                    clientClassificationOptions = listOf(),
                    clientLegalFormOptions = listOf(),
                    savingProductOptions = listOf(),
                    dataTables = listOf()
                )
            ),
            CreateNewClientUiState.ShowProgressbar,
            CreateNewClientUiState.ShowClientCreatedSuccessfully(R.string.feature_client_client_created_successfully),
            CreateNewClientUiState.OnImageUploadSuccess(R.string.feature_client_Image_Upload_Successful),
            CreateNewClientUiState.ShowWaitingForCheckerApproval(R.string.feature_client_waiting_for_checker_approval)
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewCreateNewClientScreen(
    @PreviewParameter(CreateNewClientScreenPreviewProvider::class) createNewClientUiState: CreateNewClientUiState
) {
    // ToDo : FIX Preview

    CreateNewClientScreen(
        uiState = createNewClientUiState,
        onRetry = { },
        officeList = listOf(),
        staffInOffices = listOf(),
        loadStaffInOffice = { },
        navigateBack = { },
        createClient = { },
        uploadImage = { _, _ -> }
    ) { _, _ ->

    }
}
