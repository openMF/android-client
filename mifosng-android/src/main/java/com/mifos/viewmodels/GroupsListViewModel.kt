package com.mifos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.mifosxdroid.R
import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import com.mifos.repositories.GroupsListRepository
import com.mifos.states.GroupsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class GroupsListViewModel @Inject constructor(private val repository: GroupsListRepository) :
    ViewModel() {

    private val _groupsListUiState = MutableLiveData<GroupsListUiState>()

    val groupsListUiState: LiveData<GroupsListUiState>
        get() = _groupsListUiState

    private var mDbGroupList: List<Group> = ArrayList()
    private var mSyncGroupList: List<Group> = ArrayList()
    private val limit = 100
    private var loadmore = false
    private var mRestApiGroupSyncStatus = false
    private var mDatabaseGroupSyncStatus = false
    fun loadGroups(loadmore: Boolean, offset: Int) {
        this.loadmore = loadmore
        loadGroups(true, offset, limit)
    }

    /**
     * Showing Groups List in View, If loadmore is true call showLoadMoreGroups(...) and else
     * call showGroupsList(...).
     */
    private fun showClientList(clients: List<Group>) {
        if (loadmore) {
            _groupsListUiState.value = GroupsListUiState.ShowLoadMoreGroups(clients)
        } else {
            _groupsListUiState.value = GroupsListUiState.ShowGroups(clients)
        }
    }

    /**
     * This Method will called, when Parent (Fragment or Activity) will be true.
     * If Parent Fragment is true then there is no need to fetch ClientList, Just show the Parent
     * (Fragment or Activity)'s Groups in View
     *
     * @param groups List<Group></Group>>
     */
    fun showParentClients(groups: List<Group>) {
        _groupsListUiState.value = GroupsListUiState.UnregisterSwipeAndScrollListener
        if (groups.isEmpty()) {
            _groupsListUiState.value = GroupsListUiState.ShowEmptyGroups(R.string.group)
        } else {
            mRestApiGroupSyncStatus = true
            mSyncGroupList = groups
            setAlreadyClientSyncStatus()
        }
    }

    /**
     * Setting GroupSync Status True when mRestApiGroupSyncStatus && mDatabaseGroupSyncStatus
     * are true.
     */
    fun setAlreadyClientSyncStatus() {
        if (mRestApiGroupSyncStatus && mDatabaseGroupSyncStatus) {
            showClientList(checkGroupAlreadySyncedOrNot(mSyncGroupList))
        }
    }

    private fun loadGroups(paged: Boolean, offset: Int, limit: Int) {
        _groupsListUiState.value = GroupsListUiState.ShowProgressbar
        repository.getGroups(paged, offset, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Group>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    mvpView?.showProgressbar(false)
                    if (loadmore) {
//                        mvpView?.showMessage(R.string.failed_to_fetch_groups)
                        _groupsListUiState.value =
                            GroupsListUiState.ShowMessage(R.string.failed_to_fetch_groups)
                    } else {
                        _groupsListUiState.value = GroupsListUiState.ShowFetchingError
//                        mvpView?.showFetchingError()
                    }
                }

                override fun onNext(groupPage: Page<Group>) {
                    mSyncGroupList = groupPage.pageItems
                    if (mSyncGroupList.isEmpty() && !loadmore) {
//                        mvpView?.showEmptyGroups(R.string.group)
                        _groupsListUiState.value = GroupsListUiState.ShowEmptyGroups(R.string.group)
//                        mvpView?.unregisterSwipeAndScrollListener()
                        _groupsListUiState.value =
                            GroupsListUiState.UnregisterSwipeAndScrollListener
                    } else if (mSyncGroupList.isEmpty() && loadmore) {
//                        mvpView?.showMessage(R.string.no_more_groups_available)
                        _groupsListUiState.value =
                            GroupsListUiState.ShowMessage(R.string.no_more_groups_available)
                    } else {
                        mRestApiGroupSyncStatus = true
                        setAlreadyClientSyncStatus()
                    }
//                    mvpView?.showProgressbar(false)
                }
            })
    }

    fun loadDatabaseGroups() {
        _groupsListUiState.value = GroupsListUiState.ShowProgressbar
        repository.databaseGroups()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Group>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    mvpView?.showMessage(R.string.failed_to_load_db_groups)
                    _groupsListUiState.value =
                        GroupsListUiState.ShowMessage(R.string.failed_to_load_db_groups)
                }

                override fun onNext(groupPage: Page<Group>) {
                    mDatabaseGroupSyncStatus = true
                    mDbGroupList = groupPage.pageItems
                    setAlreadyClientSyncStatus()
                }
            })

    }

    /**
     * This Method Filtering the Groups Loaded from Server, is already sync or not. If yes the
     * put the client.setSync(true) and view will show to user that group already synced
     *
     * @param groups
     * @return List<Client>
    </Client> */
    private fun checkGroupAlreadySyncedOrNot(groups: List<Group>): List<Group> {
        if (mDbGroupList.isNotEmpty()) {
            for (dbGroup in mDbGroupList) {
                for (syncGroup in groups) {
                    if (dbGroup.id?.toInt() == syncGroup.id?.toInt()) {
                        syncGroup.sync = true
                        break
                    }
                }
            }
        }
        return groups
    }

}