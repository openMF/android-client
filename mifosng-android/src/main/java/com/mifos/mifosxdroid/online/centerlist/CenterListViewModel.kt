package com.mifos.mifosxdroid.online.centerlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class CenterListViewModel @Inject constructor(private val repository: CenterListRepository) :
    ViewModel() {

    private val _centerListUiState = MutableLiveData<CenterListUiState>()
    val centerListUiState: LiveData<CenterListUiState>
        get() = _centerListUiState

    private var mDbCenterList: List<Center> = ArrayList()
    private var mSyncCenterList: List<Center> = ArrayList()
    private val limit = 100
    private var loadmore = false
    private var mRestApiCenterSyncStatus = false
    private var mDatabaseCenterSyncStatus = false

    /**
     * This Method for loading the first 100 centers.
     *
     * @param loadmore
     * @param offset
     */
    fun loadCenters(loadmore: Boolean, offset: Int) {
        this.loadmore = loadmore
        loadCenters(true, offset, limit)
    }

    /**
     * This Method For showing Center List in UI.
     *
     * @param centers
     */
    fun showCenters(centers: List<Center>) {
        if (loadmore) {
            _centerListUiState.value = CenterListUiState.ShowMoreCenters(centers)
        } else {
            _centerListUiState.value = CenterListUiState.ShowCenters(centers)
        }
    }

    /**
     * Setting CenterSync Status True when mRestApiCenterSyncStatus && mDatabaseCenterSyncStatus
     * are true.
     */
    fun setAlreadyCenterSyncStatus() {
        if (mRestApiCenterSyncStatus && mDatabaseCenterSyncStatus) {
            showCenters(checkCenterAlreadySyncedOrNot(mSyncCenterList))
        }
    }

    /**
     * @param paged  True Enabling the Pagination of the API
     * @param offset Value given from which position Center List will be fetched.
     * @param limit  Number of Centers to fetch.
     */
    private fun loadCenters(paged: Boolean, offset: Int, limit: Int) {
        _centerListUiState.value = CenterListUiState.ShowProgressbar(true)
        repository.getCenters(paged, offset, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Center>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    if (loadmore) {
                        _centerListUiState.value =
                            CenterListUiState.ShowMessage(R.string.failed_to_fetch_centers)
                    } else {
                        _centerListUiState.value = CenterListUiState.ShowFetchingError
                    }
                }

                override fun onNext(centerPage: Page<Center>) {
                    mSyncCenterList = centerPage.pageItems
                    if (mSyncCenterList.isEmpty() && !loadmore) {
                        _centerListUiState.value =
                            CenterListUiState.ShowEmptyCenters(R.string.center)
                        _centerListUiState.value =
                            CenterListUiState.UnregisterSwipeAndScrollListener
                    } else if (mSyncCenterList.isEmpty() && loadmore) {
                        _centerListUiState.value =
                            CenterListUiState.ShowMessage(R.string.no_more_centers_available)
                    } else {
                        showCenters(mSyncCenterList)
                        mRestApiCenterSyncStatus = true
                        setAlreadyCenterSyncStatus()
                    }
                }
            })
    }

    fun loadCentersGroupAndMeeting(id: Int) {
        _centerListUiState.value = CenterListUiState.ShowProgressbar(true)
        repository.getCentersGroupAndMeeting(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CenterWithAssociations?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _centerListUiState.value =
                        CenterListUiState.ShowMessage(R.string.failed_to_fetch_Group_and_meeting)
                }

                override fun onNext(centerWithAssociations: CenterWithAssociations?) {
                    _centerListUiState.value =
                        CenterListUiState.ShowCentersGroupAndMeeting(centerWithAssociations, id)
                }
            })
    }

    /**
     * This Method Loading the Center From Database. It request Observable to DataManagerCenter
     * and DataManagerCenter Request to DatabaseHelperCenter to load the Center List Page from the
     * Center_Table and As the Center List Page is loaded DataManagerCenter gives the Center List
     * Page after getting response from DatabaseHelperCenter
     */
    fun loadDatabaseCenters() {
        repository.allDatabaseCenters()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Center>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _centerListUiState.value =
                        CenterListUiState.ShowMessage(R.string.failed_to_load_db_centers)
                }

                override fun onNext(centerPage: Page<Center>) {
                    mDatabaseCenterSyncStatus = true
                    mDbCenterList = centerPage.pageItems
                    setAlreadyCenterSyncStatus()
                }
            })
    }

    /**
     * This Method Filtering the Centers Loaded from Server is already sync or not. If yes the
     * put the center.setSync(true) and view will show those centers as sync already to user
     *
     * @param
     * @return Page<Center>
    </Center> */
    private fun checkCenterAlreadySyncedOrNot(centers: List<Center>): List<Center> {
        if (mDbCenterList.isNotEmpty()) {
            for (dbCenter in mDbCenterList) {
                for (syncCenter in centers) {
                    if (dbCenter.id == syncCenter.id) {
                        syncCenter.sync = true
                        break
                    }
                }
            }
        }
        return centers
    }

}