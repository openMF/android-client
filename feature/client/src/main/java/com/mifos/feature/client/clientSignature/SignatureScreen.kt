@file:OptIn(ExperimentalComposeUiApi::class)

package com.mifos.feature.client.clientSignature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.core.graphics.applyCanvas
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDrawingCanvas
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.utility.PathState
import com.mifos.feature.client.R
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

@Composable
fun SignatureScreen(
    onBackPressed: () -> Unit
) {

    val viewmodel: SignatureViewModel = hiltViewModel()
    val clientId by viewmodel.clientId.collectAsStateWithLifecycle()
    val state by viewmodel.signatureUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SignatureScreen(
        state = state,
        onBackPressed = onBackPressed,
        uploadSignature = {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val file = File(context.cacheDir, "signature.png")
            file.outputStream().use { it.write(byteArrayOutputStream.toByteArray()) }

            viewmodel.createDocument(
                Constants.ENTITY_TYPE_CLIENTS,
                clientId,
                file.name,
                "Signature",
                file
            )
        }
    )

}

@Composable
fun SignatureScreen(
    state: SignatureUiState,
    onBackPressed: () -> Unit,
    uploadSignature: (Bitmap) -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current

    var navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    var image by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val drawColor by remember { mutableStateOf(Color.Black) }
    val drawBrush by remember { mutableFloatStateOf(5f) }
    val usedColors by remember {
        mutableStateOf(
            mutableSetOf(
                Color.Black,
                Color.White,
                Color.Gray
            )
        )
    }
    var paths by remember { mutableStateOf(mutableListOf<PathState>()) }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                val bitmap = context.contentResolver.openInputStream(uri).use { stream ->
                    BitmapFactory.decodeStream(stream).asImageBitmap().asAndroidBitmap()
                }
                uploadSignature(bitmap)
            }
        }
    )


    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_client_signature_title),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                val bounds = capturingViewBounds ?: return@IconButton
                image = Bitmap.createBitmap(
                    bounds.width.roundToInt(), bounds.height.roundToInt(),
                    Bitmap.Config.ARGB_8888
                ).applyCanvas {
                    translate(-bounds.left, -bounds.top)
                    view.draw(this)
                }
                image?.let { uploadSignature(it) }
            }) {
                Icon(
                    imageVector = MifosIcons.upload,
                    contentDescription = null
                )
            }
        },
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems()
                    .forEachIndexed { index, navigationItem ->
                        NavigationBarItem(
                            selected = index == navigationSelectedItem,
                            label = {
                                Text(navigationItem.label)
                            },
                            icon = {
                                Icon(
                                    navigationItem.icon,
                                    contentDescription = navigationItem.label
                                )
                            },
                            onClick = {
                                navigationSelectedItem = index
                                when (index) {
                                    0 -> {
                                        paths = mutableListOf()
                                    }

                                    1 -> {
                                        galleryLauncher.launch("image/*")
                                    }
                                }
                            }
                        )
                    }
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .onGloballyPositioned {
                capturingViewBounds = it.boundsInRoot()
            }) {
            when (state) {
                is SignatureUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {

                }

                is SignatureUiState.Loading -> MifosCircularProgress()

                is SignatureUiState.SignatureUploadedSuccessfully -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.feature_client_signature_uploaded_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }

                is SignatureUiState.Initial -> {
                    paths.add(PathState(Path(), drawColor, drawBrush))

                    MifosDrawingCanvas(
                        drawColor = drawColor,
                        drawBrush = drawBrush,
                        usedColors = usedColors,
                        paths = paths
                    )

                }
            }
        }
    }
}

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = MifosIcons.close,
    val route: String = ""
) {

    @Composable
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = stringResource(id = R.string.feature_client_signature_reset),
                icon = MifosIcons.close
            ),
            BottomNavigationItem(
                label = stringResource(id = R.string.feature_client_signature_gallery),
                icon = MifosIcons.gallery
            )
        )
    }
}


class SignatureScreenUiStateProvider : PreviewParameterProvider<SignatureUiState> {

    override val values: Sequence<SignatureUiState>
        get() = sequenceOf(
            SignatureUiState.Initial,
            SignatureUiState.Error(message = R.string.feature_client_failed_to_add_signature),
            SignatureUiState.Loading,
            SignatureUiState.SignatureUploadedSuccessfully
        )

}

@Preview(showBackground = true)
@Composable
private fun SignatureScreenPreview(
    @PreviewParameter(SignatureScreenUiStateProvider::class) state: SignatureUiState
) {
    SignatureScreen(
        state = state,
        onBackPressed = {},
        uploadSignature = {}
    )
}