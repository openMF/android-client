@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.mifos.feature.client.clientDetails.ui

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.MobileFriendly
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mifos.core.common.utils.Utils
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.feature.client.R
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects

/**
 * Created by Aditya Gupta on 18/03/24.
 */

@Composable
fun ClientDetailsScreen(
    clientDetailsViewModel: ClientDetailsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    addLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    charges: (Int) -> Unit,
    documents: (Int) -> Unit,
    identifiers: (Int) -> Unit,
    moreClientInfo: (Int) -> Unit,
    notes: (Int) -> Unit,
    pinpointLocation: (Int) -> Unit,
    survey: (Int) -> Unit,
    uploadSignature: (Int) -> Unit,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, DepositType) -> Unit,
    activateClient: (Int) -> Unit
) {
    val clientId by clientDetailsViewModel.clientId.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = clientDetailsViewModel.clientDetailsUiState.collectAsStateWithLifecycle().value
    val client = clientDetailsViewModel.client.collectAsStateWithLifecycle().value
    val loanAccounts = clientDetailsViewModel.loanAccount.collectAsStateWithLifecycle().value
    val savingsAccounts = clientDetailsViewModel.savingsAccounts.collectAsStateWithLifecycle().value
    val showLoading = clientDetailsViewModel.showLoading.collectAsStateWithLifecycle().value


    var clientNotFoundError by rememberSaveable { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSelectImageDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }


    val file = context.createImageFile()
    val cameraImageUri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.mifos.mifosxdroid" + ".provider",
        file
    )
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA
        )
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                val bitmap = context.contentResolver.openInputStream(uri).use { stream ->
                    BitmapFactory.decodeStream(stream).asImageBitmap().asAndroidBitmap()
                }
                showSelectImageDialog = false
                clientDetailsViewModel.saveClientImage(clientId, bitmap)
            }
        }
    )


    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { status ->
            if (status) {
                imageUri = cameraImageUri
                val bitmap = context.contentResolver.openInputStream(cameraImageUri).use { stream ->
                    BitmapFactory.decodeStream(stream).asImageBitmap().asAndroidBitmap()
                }
                showSelectImageDialog = false
                clientDetailsViewModel.saveClientImage(clientId, bitmap)
            }
        }

    LaunchedEffect(key1 = true) {
        clientDetailsViewModel.loadClientDetailsAndClientAccounts(clientId)
    }

    when (state) {

        is ClientDetailsUiState.ShowClientImageDeletedSuccessfully -> {
            val message = stringResource(id = R.string.feature_client_client_image_deleted)
            LaunchedEffect(key1 = state) {
                snackbarHostState.showSnackbar(message = message)
            }
        }

        is ClientDetailsUiState.ShowUploadImageSuccessfully -> {
            val message = stringResource(id = R.string.feature_client_client_image_updated)
            LaunchedEffect(key1 = state.response) {
                snackbarHostState.showSnackbar(message = message)
            }
        }

        is ClientDetailsUiState.ShowError -> {
            when (state.message == "null") {
                true -> {
                    clientNotFoundError = true
                }

                false -> {
                    LaunchedEffect(key1 = state.message) {
                        snackbarHostState.showSnackbar(message = state.message)
                    }
                }
            }
        }

        ClientDetailsUiState.Empty -> {}
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_client),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = { showMenu = showMenu.not() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                modifier = Modifier.background(White),
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_add_loan_account)) {
                    addLoanAccount(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_add_savings_account)) {
                    addSavingsAccount(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_charges)) {
                    charges(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_documents)) {
                    documents(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_identifiers)) {
                    identifiers(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_more_client_info)) {
                    moreClientInfo(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_notes)) {
                    notes(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_pinpoint_location)) {
                    pinpointLocation(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_survey)) {
                    survey(clientId)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_client_upload_signature)) {
                    uploadSignature(clientId)
                    showMenu = false
                }
            }
        },
        snackbarHostState = snackbarHostState,
        bottomBar = {
            if (client?.active == false) {
                Button(
                    onClick = { activateClient(clientId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(44.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_client_activate_client),
                        fontSize = 16.sp
                    )
                }

            }
        }) { padding ->
        if (showSelectImageDialog) {
            MifosSelectImageDialog(onDismissRequest = {
                run {
                    showSelectImageDialog = !showSelectImageDialog
                }
            },
                takeImage = {
                    permissionState.permissions.forEach { per ->
                        when (per.permission) {
                            Manifest.permission.CAMERA -> {
                                when {
                                    per.status.isGranted -> {
                                        cameraLauncher.launch(cameraImageUri)
                                    }

                                    else -> {
                                        permissionState.launchMultiplePermissionRequest()
                                    }
                                }
                            }
                        }
                    }
                },
                uploadImage = {
                    galleryLauncher.launch("image/*")
                }, deleteImage = {
                    clientDetailsViewModel.deleteClientImage(clientId)
                    showSelectImageDialog = false
                })
        }
        if (clientNotFoundError) {
            MifosSweetError(message = stringResource(id = R.string.feature_client_client_not_found)) {
                clientDetailsViewModel.loadClientDetailsAndClientAccounts(clientId)
            }
        } else {
            if (showLoading) {
                MifosCircularProgress()
            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(100))
                                .clickable(onClick = {
                                    showSelectImageDialog = true
                                }),
                            model = if (client?.imagePresent == true) {
                                client.clientId?.let {
                                    scope.launch {
                                        clientDetailsViewModel.getClientImageUrl(
                                            it
                                        )
                                    }
                                }
                            } else R.drawable.feature_client_ic_launcher,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    client?.displayName?.let {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = it,
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Normal
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    client?.accountNo?.let {
                        MifosClientDetailsText(
                            icon = Icons.Outlined.Numbers,
                            field = stringResource(id = R.string.feature_client_account_number),
                            value = it
                        )
                    }
                    client?.externalId?.let {
                        MifosClientDetailsText(
                            icon = Icons.Outlined.Numbers,
                            field = stringResource(id = R.string.feature_client_external_id),
                            value = it
                        )
                    }
                    client?.let { Utils.getStringOfDate(it.activationDate) }?.let {
                        MifosClientDetailsText(
                            icon = Icons.Outlined.DateRange,
                            field = stringResource(id = R.string.feature_client_activation_date),
                            value = it
                        )
                    }
                    client?.officeName?.let {
                        MifosClientDetailsText(
                            icon = Icons.Outlined.HomeWork,
                            field = stringResource(id = R.string.feature_client_office),
                            value = it
                        )
                    }
                    client?.mobileNo?.let {
                        MifosClientDetailsText(
                            icon = Icons.Outlined.MobileFriendly,
                            field = stringResource(id = R.string.feature_client_mobile_no),
                            value = it
                        )
                    }
                    client?.groupNames?.let {
                        MifosClientDetailsText(
                            icon = Icons.Outlined.Groups,
                            field = stringResource(id = R.string.feature_client_group),
                            value = it
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    if (loanAccounts != null && savingsAccounts != null) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp),
                            text = stringResource(id = R.string.feature_client_accounts),
                            style = TextStyle(
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Normal
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                    loanAccounts?.let {
                        MifosLoanAccountExpendableCard(
                            stringResource(id = R.string.feature_client_loan_account),
                            it,
                            loanAccountSelected
                        )
                    }
                    savingsAccounts?.let {
                        MifosSavingsAccountExpendableCard(
                            stringResource(id = R.string.feature_client_savings_account),
                            it, savingsAccountSelected
                        )
                    }
                }
            }
        }
    }
}

fun Context.createImageFile(): File {
    val imageFileName = "client_image"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}

@Composable
fun MifosLoanAccountExpendableCard(
    accountType: String,
    loanAccounts: List<LoanAccount>,
    loanAccountSelected: (Int) -> Unit
) {

    var expendableState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(
        targetValue = if (expendableState) 180f else 0f,
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(BlueSecondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    text = accountType,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Black,
                    textAlign = TextAlign.Start
                )
                IconButton(
                    modifier = Modifier
                        .size(24.dp),
                    onClick = { expendableState = !expendableState }) {
                    Icon(
                        modifier = Modifier.rotate(rotateState),
                        imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null
                    )
                }
            }

            if (expendableState) {

                Spacer(modifier = Modifier.height(10.dp))
                MifosLoanAccountsLazyColumn(loanAccounts, loanAccountSelected)
            }
        }
    }
}


@Composable
fun MifosLoanAccountsLazyColumn(
    loanAccounts: List<LoanAccount>,
    loanAccountSelected: (Int) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(White)
    ) {
        LazyColumn(
            modifier = Modifier
                .height((loanAccounts.size * 52).dp)
                .padding(6.dp)
        ) {
            items(loanAccounts) { loanAccount ->
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable(onClick = {
                            loanAccount.id?.let {
                                loanAccountSelected(
                                    it
                                )
                            }
                        }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier
                        .size(20.dp)
                        .padding(4.dp), onDraw = {
                        drawCircle(
                            color = when {
                                loanAccount.status?.active == true -> {
                                    Color.Green
                                }

                                loanAccount.status?.waitingForDisbursal == true -> {
                                    Color.Blue
                                }

                                loanAccount.status?.pendingApproval == true -> {
                                    Color.Yellow
                                }

                                loanAccount.status?.active == true && loanAccount.inArrears == true -> {
                                    Color.Red
                                }

                                else -> {
                                    Color.DarkGray
                                }
                            }
                        )
                    })
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        loanAccount.productName?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal
                                ),
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                        Text(
                            text = loanAccount.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal
                            ),
                            color = DarkGray,
                            textAlign = TextAlign.Start
                        )
                    }
                    loanAccount.productId?.let {
                        Text(
                            text = it.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MifosSavingsAccountExpendableCard(
    accountType: String,
    savingsAccount: List<SavingsAccount>,
    savingsAccountSelected: (Int, DepositType) -> Unit
) {

    var expendableState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(
        targetValue = if (expendableState) 180f else 0f,
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(BlueSecondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    text = accountType,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Black,
                    textAlign = TextAlign.Start
                )
                IconButton(
                    modifier = Modifier
                        .size(24.dp),
                    onClick = { expendableState = !expendableState }) {
                    Icon(
                        modifier = Modifier.rotate(rotateState),
                        imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null
                    )
                }
            }

            if (expendableState) {

                Spacer(modifier = Modifier.height(10.dp))
                MifosSavingsAccountsLazyColumn(savingsAccount, savingsAccountSelected)
            }
        }
    }
}


@Composable
fun MifosSavingsAccountsLazyColumn(
    savingsAccounts: List<SavingsAccount>,
    savingsAccountSelected: (Int, DepositType) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(White)
    ) {
        LazyColumn(
            modifier = Modifier
                .height((savingsAccounts.size * 50).dp)
                .padding(6.dp)
        ) {
            items(savingsAccounts) { savingsAccount ->
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable(onClick = {
                            savingsAccount.id?.let {
                                savingsAccount.depositType?.let { it1 ->
                                    savingsAccountSelected(
                                        it, it1
                                    )
                                }
                            }
                        }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier
                        .size(20.dp)
                        .padding(4.dp), onDraw = {
                        drawCircle(
                            color = when {
                                savingsAccount.status?.active == true -> {
                                    Color.Green
                                }

                                savingsAccount.status?.approved == true -> {
                                    Color.Blue
                                }

                                savingsAccount.status?.submittedAndPendingApproval == true -> {
                                    Color.Yellow
                                }

                                else -> {
                                    Color.DarkGray
                                }
                            }
                        )
                    })
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        savingsAccount.productName?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal
                                ),
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                        Text(
                            text = savingsAccount.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal
                            ),
                            color = DarkGray,
                            textAlign = TextAlign.Start
                        )
                    }
                    savingsAccount.productId?.let {
                        Text(
                            text = it.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MifosSelectImageDialog(
    onDismissRequest: () -> Unit,
    takeImage: () -> Unit,
    uploadImage: () -> Unit,
    deleteImage: () -> Unit,
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
                    text = stringResource(id = R.string.feature_client_please_select),
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
                        text = stringResource(id = R.string.feature_client_take_new_image),
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
                        text = stringResource(id = R.string.feature_client_upload_new_image),
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
                    onClick = { deleteImage() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_client_delete_image),
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

@Composable
fun MifosClientDetailsText(icon: ImageVector, field: String, value: String) {

    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = icon,
            contentDescription = null,
            tint = DarkGray
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = Black,
            textAlign = TextAlign.Start
        )
        Text(

            text = value,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = DarkGray,
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
private fun ClientDetailsScreenPreview() {
    ClientDetailsScreen(
        onBackPressed = {},
        addLoanAccount = {},
        addSavingsAccount = {},
        charges = {},
        documents = {},
        identifiers = {},
        moreClientInfo = {},
        notes = {},
        pinpointLocation = {},
        survey = {},
        uploadSignature = {},
        loanAccountSelected = {},
        savingsAccountSelected = { _, _ -> },
        activateClient = {}
    )
}