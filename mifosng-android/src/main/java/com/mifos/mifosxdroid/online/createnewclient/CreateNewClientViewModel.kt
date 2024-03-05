package com.mifos.mifosxdroid.online.createnewclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.templates.clients.ClientsTemplate
import com.mifos.core.objects.templates.clients.Options
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import rx.Observable
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
class CreateNewClientViewModel @Inject constructor(private val repository: CreateNewClientRepository) :
    ViewModel() {

    private val _createNewClientUiState = MutableLiveData<CreateNewClientUiState>()

    val createNewClientUiState: LiveData<CreateNewClientUiState>
        get() = _createNewClientUiState

    fun loadClientTemplate() {
        _createNewClientUiState.value = CreateNewClientUiState.ShowProgressbar
        repository.clientTemplate()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientsTemplate>() {
                override fun onCompleted() {
                    loadOffices()
                }

                override fun onError(e: Throwable) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowMessage(R.string.failed_to_fetch_client_template)
                }

                override fun onNext(clientsTemplate: ClientsTemplate?) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowClientTemplate(clientsTemplate)
                }
            })
    }

    fun loadOffices() {
        _createNewClientUiState.value = CreateNewClientUiState.ShowProgressbar
        repository.offices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowMessage(R.string.failed_to_fetch_offices)
                }

                override fun onNext(officeList: List<Office>) {
                    _createNewClientUiState.value = CreateNewClientUiState.ShowOffices(officeList)
                }
            })
    }

    fun loadStaffInOffices(officeId: Int) {
        repository.getStaffInOffice(officeId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Staff>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowMessage(R.string.failed_to_fetch_staffs)
                }

                override fun onNext(staffList: List<Staff>) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowStaffInOffices(staffList)
                }
            })
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
                                ?.string()
                            _createNewClientUiState.value = errorMessage?.let {
                                CreateNewClientUiState.ShowMessageString(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(client: Client?) {
                    if (client != null) {
                        if (client.clientId != null) {
                            _createNewClientUiState.value =
                                CreateNewClientUiState.ShowClientCreatedSuccessfully(R.string.client_created_successfully)
                            _createNewClientUiState.value = client.clientId?.let {
                                CreateNewClientUiState.SetClientId(
                                    it
                                )
                            }
                        } else {
                            _createNewClientUiState.value = client.clientId?.let {
                                CreateNewClientUiState.ShowWaitingForCheckerApproval(
                                    it
                                )
                            }
                        }
                    }
                }
            })
    }

    fun filterOptions(options: List<Options>?): List<String> {
        val filterValues: MutableList<String> = ArrayList()
        Observable.from(options)
            .subscribe { options -> filterValues.add(options.name) }
        return filterValues
    }

    fun filterOffices(offices: List<Office>?): List<String> {
        val officesList: MutableList<String> = ArrayList()
        Observable.from(offices)
            .subscribe { office -> office.name?.let { officesList.add(it) } }
        return officesList
    }

    fun filterStaff(staffs: List<Staff>?): List<String> {
        val staffList: MutableList<String> = ArrayList()
        Observable.from(staffs)
            .subscribe { staff -> staff.displayName?.let { staffList.add(it) } }
        return staffList
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
                        CreateNewClientUiState.ShowMessage(R.string.Image_Upload_Failed)
                }

                override fun onNext(t: ResponseBody) {
                    _createNewClientUiState.value =
                        CreateNewClientUiState.ShowMessage(R.string.Image_Upload_Successful)
                }
            })
    }
}