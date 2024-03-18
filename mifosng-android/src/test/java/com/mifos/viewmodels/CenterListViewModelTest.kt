package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.centerlist.CenterListRepository
import com.mifos.mifosxdroid.online.centerlist.CenterListUiState
import com.mifos.mifosxdroid.online.centerlist.CenterListViewModel
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Aditya Gupta on 06/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class CenterListViewModelTest {

    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var centerListRepository: CenterListRepository

    private lateinit var centerListViewModel: CenterListViewModel

    @Mock
    private lateinit var centerListUiObserver: Observer<CenterListUiState>

    @Mock
    private lateinit var mockCenterList: Page<Center>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        centerListViewModel = CenterListViewModel(centerListRepository)
        centerListViewModel.centerListUiState.observeForever(centerListUiObserver)
    }

    @Test
    fun testCenterList_SuccessfulCenterListReceivedFromRepository_ReturnsCenterList() {
        val list1 = Mockito.mock(Center::class.java)
        val list2 = Mockito.mock(Center::class.java)
        val list = listOf(list1, list2)
        val mockPage = Page(2, list)

        Mockito.`when`(
            centerListRepository.getCenters(
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            Observable.just(mockPage)
        )
        centerListViewModel.loadCenters(false, 1)
        Mockito.verify(centerListUiObserver).onChanged(CenterListUiState.ShowProgressbar(true))
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowFetchingError)
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowMessage(R.string.failed_to_fetch_centers))
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowEmptyCenters(R.string.center))
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.UnregisterSwipeAndScrollListener)
        Mockito.verify(centerListUiObserver)
            .onChanged(CenterListUiState.ShowCenters(mockPage.pageItems))
        Mockito.verifyNoMoreInteractions(centerListUiObserver)
    }


    @Test
    fun testCenterList_SuccessfulCenterListReceivedFromRepository_ReturnsEmptyCenterList() {
        Mockito.`when`(
            centerListRepository.getCenters(
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            Observable.just(mockCenterList)
        )
        centerListViewModel.loadCenters(false, 1)
        Mockito.verify(centerListUiObserver).onChanged(CenterListUiState.ShowProgressbar(true))
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowFetchingError)
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowMessage(R.string.failed_to_fetch_centers))
        Mockito.verify(centerListUiObserver)
            .onChanged(CenterListUiState.ShowEmptyCenters(R.string.center))
        Mockito.verify(centerListUiObserver)
            .onChanged(CenterListUiState.UnregisterSwipeAndScrollListener)
        Mockito.verifyNoMoreInteractions(centerListUiObserver)
    }

    @Test
    fun testCenterList_UnsuccessfulCenterListReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            centerListRepository.getCenters(
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            Observable.error(RuntimeException("some error message"))
        )
        centerListViewModel.loadCenters(false, 1)
        Mockito.verify(centerListUiObserver).onChanged(CenterListUiState.ShowProgressbar(true))
        Mockito.verify(centerListUiObserver).onChanged(CenterListUiState.ShowFetchingError)
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowEmptyCenters(R.string.center))
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowMessage(R.string.failed_to_fetch_centers))
        Mockito.verifyNoMoreInteractions(centerListUiObserver)
    }

    @Test
    fun testCenterList_UnsuccessfulDbCenterListReceivedFromRepository_ReturnsCenterList() {
        Mockito.`when`(
            centerListRepository.allDatabaseCenters()
        ).thenReturn(
            Observable.just(mockCenterList)
        )
        centerListViewModel.loadDatabaseCenters()
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowMessage(R.string.failed_to_load_db_centers))
        Mockito.verifyNoMoreInteractions(centerListUiObserver)
    }

    @Test
    fun testCenterList_UnsuccessfulDbCenterListReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            centerListRepository.allDatabaseCenters()
        ).thenReturn(
            Observable.error(RuntimeException("some error message"))
        )
        centerListViewModel.loadDatabaseCenters()
        Mockito.verify(centerListUiObserver)
            .onChanged(CenterListUiState.ShowMessage(R.string.failed_to_load_db_centers))
        Mockito.verify(centerListUiObserver, Mockito.never())
            .onChanged(CenterListUiState.ShowCenters(mockCenterList.pageItems))
        Mockito.verifyNoMoreInteractions(centerListUiObserver)
    }

    @After
    fun tearDown() {
        centerListViewModel.centerListUiState.removeObserver(centerListUiObserver)
    }
}