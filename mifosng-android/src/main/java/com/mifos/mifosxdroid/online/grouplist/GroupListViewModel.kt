package com.mifos.mifosxdroid.online.grouplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.GroupWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class GroupListViewModel @Inject constructor(private val repository: GroupListRepository) :
    ViewModel() {

    private val _groupListUiState = MutableLiveData<GroupListUiState>()

    val groupListUiState: LiveData<GroupListUiState>
        get() = _groupListUiState


    fun loadGroups(groupId: Int) {
        _groupListUiState.value = GroupListUiState.ShowProgress(true)
        repository.getGroups(groupId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupWithAssociations?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _groupListUiState.value =
                        GroupListUiState.ShowFetchingError("Failed to load Groups")
                }

                override fun onNext(groupWithAssociations: GroupWithAssociations?) {
                    _groupListUiState.value = GroupListUiState.ShowGroups(groupWithAssociations)
                }
            })
    }

    fun loadGroupByCenter(id: Int) {
        _groupListUiState.value = GroupListUiState.ShowProgress(true)
        repository.getGroupsByCenter(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CenterWithAssociations?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _groupListUiState.value =
                        GroupListUiState.ShowFetchingError("Failed to load GroupList")
                }

                override fun onNext(centerWithAssociations: CenterWithAssociations?) {
                    _groupListUiState.value = GroupListUiState.ShowGroupList(centerWithAssociations)
                }
            })
    }

}