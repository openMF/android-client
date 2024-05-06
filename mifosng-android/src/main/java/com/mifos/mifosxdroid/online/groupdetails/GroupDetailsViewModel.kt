package com.mifos.mifosxdroid.online.groupdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.zipmodels.GroupAndGroupAccounts
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class GroupDetailsViewModel @Inject constructor(private val repository: GroupDetailsRepository) :
    ViewModel() {

    private val _groupDetailsUiState = MutableLiveData<GroupDetailsUiState>()

    val groupDetailsUiState: LiveData<GroupDetailsUiState>
        get() = _groupDetailsUiState

    fun loadGroupDetailsAndAccounts(groupId: Int) {
        _groupDetailsUiState.value = GroupDetailsUiState.ShowProgressbar(true)
        Observable.combineLatest(
            repository.getGroup(groupId),
            repository.getGroupAccounts(groupId)
        ) { group, groupAccounts -> GroupAndGroupAccounts(group, groupAccounts) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupAndGroupAccounts>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _groupDetailsUiState.value =
                        GroupDetailsUiState.ShowFetchingError(R.string.failed_to_fetch_group_and_account)
                }

                override fun onNext(groupAndGroupAccounts: GroupAndGroupAccounts) {
                    _groupDetailsUiState.value =
                        GroupDetailsUiState.ShowGroup(groupAndGroupAccounts.group)
                    _groupDetailsUiState.value =
                        GroupDetailsUiState.ShowGroupAccounts(groupAndGroupAccounts.groupAccounts)
                }
            })
    }

    fun loadGroupAssociateClients(groupId: Int) {
        _groupDetailsUiState.value = GroupDetailsUiState.ShowProgressbar(true)
        repository.getGroupWithAssociations(groupId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _groupDetailsUiState.value =
                        GroupDetailsUiState.ShowFetchingError(R.string.failed_to_load_client)
                }

                override fun onNext(groupWithAssociations: GroupWithAssociations) {
                    _groupDetailsUiState.value =
                        GroupDetailsUiState.ShowGroupClients(groupWithAssociations.clientMembers)
                }
            })

    }
}