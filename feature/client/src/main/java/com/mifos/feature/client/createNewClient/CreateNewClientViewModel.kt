package com.mifos.feature.client.createNewClient

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.domain.use_cases.ClientTemplateUseCase
import com.mifos.core.domain.use_cases.GetStaffInOfficeForCreateNewClientUseCase
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.templates.clients.ClientsTemplate
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class CreateNewClientViewModel @Inject constructor(
    private val repository: CreateNewClientRepository,
    private val clientTemplateUseCase: ClientTemplateUseCase,
    private val getStaffInOffice : GetStaffInOfficeForCreateNewClientUseCase
) : ViewModel() {

    private val _createNewClientUiState = MutableStateFlow<CreateNewClientUiState>(CreateNewClientUiState.ShowProgressbar)
    val createNewClientUiState: StateFlow<CreateNewClientUiState> get() = _createNewClientUiState

    private val _staffInOffices = MutableStateFlow<List<Staff>>(emptyList())
    val staffInOffices: StateFlow<List<Staff>> get() = _staffInOffices

    private val _showOffices = MutableStateFlow<List<Office>>(emptyList())
    val showOffices: StateFlow<List<Office>> get() = _showOffices

    fun loadOfficeAndClientTemplate() {
        _createNewClientUiState.value = CreateNewClientUiState.ShowProgressbar
        loadOffices()
    }

    private fun loadClientTemplate() = viewModelScope.launch(Dispatchers.IO) {
        clientTemplateUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _createNewClientUiState.value =
                    CreateNewClientUiState.ShowError(R.string.feature_client_failed_to_fetch_client_template)

                is Resource.Loading -> Unit

                is Resource.Success -> _createNewClientUiState.value =
                    CreateNewClientUiState.ShowClientTemplate(result.data ?: ClientsTemplate())
            }
        }
    }

    private fun loadOffices() {
        repository.offices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {
                    loadClientTemplate()
                }

                override fun onError(e: Throwable) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowError(R.string.feature_client_failed_to_fetch_offices)
                }

                override fun onNext(officeList: List<Office>) {
                    _showOffices.value = officeList
                }
            })
    }

    fun loadStaffInOffices(officeId: Int) =
        viewModelScope.launch (Dispatchers.IO) {
            getStaffInOffice(officeId).collect{ result ->
                when(result){
                    is Resource.Error -> _createNewClientUiState.value =
                        CreateNewClientUiState.ShowError(R.string.feature_client_failed_to_fetch_staffs)

                    is Resource.Loading -> Unit

                    is Resource.Success ->_staffInOffices.value = result.data ?: emptyList()
                }
            }
    }

    fun createClient(clientPayload: ClientPayload) {
        _createNewClientUiState.value = CreateNewClientUiState.ShowProgressbar
        repository.createClient(clientPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string() ?: ""
                            Log.d("error", errorMessage)
                            _createNewClientUiState.value =
                                CreateNewClientUiState.ShowStringError(errorMessage)

                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(client: Client?) {
                    if (client != null) {
                        if (client.clientId != null) {
                            _createNewClientUiState.value =
                                CreateNewClientUiState.ShowClientCreatedSuccessfully(R.string.feature_client_client_created_successfully)

                            _createNewClientUiState.value = client.clientId?.let {
                                CreateNewClientUiState.SetClientId(
                                    it
                                )
                            }!!
                        } else {
                            _createNewClientUiState.value = client.clientId?.let {
                                CreateNewClientUiState.ShowWaitingForCheckerApproval(
                                    it
                                )
                            }!!
                        }
                    }
                }
            })
    }

    fun uploadImage(id: Int, pngFile: File) {
        _createNewClientUiState.value =
            CreateNewClientUiState.ShowProgress("Uploading Client's Picture...")
        val imagePath = pngFile.absolutePath

        // create RequestBody instance from file
        val requestFile = pngFile.asRequestBody("image/png".toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("file", pngFile.name, requestFile)
        repository.uploadClientImage(id, body)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowError(R.string.feature_client_Image_Upload_Failed)
                }

                override fun onNext(t: ResponseBody) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.OnImageUploadSuccess(R.string.feature_client_Image_Upload_Successful)
                }
            })
    }
}