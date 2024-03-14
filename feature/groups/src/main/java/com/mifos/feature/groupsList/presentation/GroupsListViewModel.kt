package com.mifos.feature.groupsList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.network.Dispatcher
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class GroupsListViewModel @Inject constructor(
    private val repository: GroupsListRepository,
    @Dispatcher(MifosDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val prefManager: PrefManager,
): ViewModel() {

    private val _uiState = MutableStateFlow<GroupsListState>(GroupsListState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadDataFromApiOrDB()
    }

    fun refreshData() {
        _isLoading.value = true
        loadDataFromApiOrDB()
        _isLoading.value = false
    }

    private fun loadDataFromApiOrDB() {
        if (prefManager.userStatus) getGroupsFromDb() else getGroupsFromApi()
    }

    private fun getGroupsFromApi(limit: Int = 10) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = GroupsListState.GroupsFromAPI(repository.getAllGroups(limit))
        }
    }

    private fun getGroupsFromDb() {
        viewModelScope.launch(ioDispatcher) {
            repository.getAllDatabaseGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Group>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(error: Throwable) {
                        _uiState.value = GroupsListState.Error("Failed to Fetch Groups")
                    }

                    override fun onNext(clients: Page<Group>) {
                        _uiState.value = GroupsListState.GroupsFromLocalDB(clients.pageItems)
                    }
                })
        }
    }
}