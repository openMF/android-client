package com.mifos.feature.groupsList

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.mifos.core.objects.group.Group
import com.mifos.core.testing.util.MainDispatcherRule
import com.mifos.feature.groupsList.data.repositoryImp.FakeGroupsListRepository
import com.mifos.feature.groupsList.data.repositoryImp.errorMessage
import com.mifos.feature.groupsList.data.repositoryImp.getPagedData
import com.mifos.feature.groupsList.data.repositoryImp.sampleGroups
import com.mifos.feature.groupsList.domain.use_case.GroupsListPagingDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GroupListPagingSourceTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeGroupsListRepository()

    private lateinit var pagingDataSource: GroupsListPagingDataSource

    private lateinit var pager: TestPager<Int, Group>

    private val pageSize = 10

    @Before
    fun setup() {
        pagingDataSource = GroupsListPagingDataSource(fakeRepository, pageSize)

        pager = TestPager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
            ),
            pagingSource = pagingDataSource
        )
    }

    @Test
    fun check_for_initial_load_result() = runTest {
        val result = pager.refresh() as PagingSource.LoadResult.Page

        // Write assertions against the loaded data
        assertEquals(result.data, sampleGroups.take(pageSize))

        val appendResult = pager.append() as PagingSource.LoadResult.Page
        assertEquals(appendResult.data, sampleGroups.getPagedData(result.nextKey ?: 0, pageSize))
    }

    @Test
    fun check_for_append_load_result() = runTest {
        val page = with(pager) {
            refresh()
            append()
            append()
        } as PagingSource.LoadResult.Page

        assert(page.data.isNotEmpty())

        assertEquals(
            sampleGroups.getPagedData(pageSize * 2, pageSize),
            page.data
        )
    }

    @Test
    fun check_for_append_and_prepend_load_result() = runTest {
        val initialData = PagingSource
            .LoadParams
            .Refresh(
                key = 1,
                loadSize = pageSize,
                placeholdersEnabled = false
            )

        val initialSource = pagingDataSource.load(params = initialData)

        val initialExpected = PagingSource
            .LoadResult
            .Page(
                data = sampleGroups.take(pageSize),
                prevKey = initialData.key?.minus(pageSize),
                nextKey = initialData.key?.plus(pageSize)
            )

        assertEquals(initialExpected, initialSource)

        //Appending data first time
        val appendData = PagingSource.LoadParams.Append(
            key = initialExpected.nextKey ?: 0,
            loadSize = pageSize,
            placeholdersEnabled = false
        )

        val appendSource = pagingDataSource.load(params = appendData)

        val appendExpected = PagingSource
            .LoadResult
            .Page(
                data = sampleGroups.getPagedData(appendData.key, pageSize),
                prevKey = appendData.key.minus(pageSize),
                nextKey = pageSize.plus(appendData.key)
            )

        // then
        assertEquals(appendExpected, appendSource)

        assertNotEquals(appendExpected, initialExpected)

        assertEquals(pageSize * 2, initialExpected.data.size.plus(appendExpected.data.size))

        assertEquals(
            sampleGroups.take(pageSize * 2),
            initialExpected.data.plus(appendExpected.data)
        )

        // Prepending first time then it should same as initial data
        val prependData = PagingSource.LoadParams.Prepend(
            key = appendExpected.prevKey ?: 0,
            loadSize = pageSize,
            placeholdersEnabled = false
        )

        // when
        val prependSource = pagingDataSource.load(params = prependData)

        val prependExpected = PagingSource
            .LoadResult
            .Page(
                data = sampleGroups.getPagedData(prependData.key, pageSize),
                prevKey = initialExpected.prevKey,
                nextKey = initialExpected.nextKey
            )

        assertEquals(prependExpected, prependSource)

        // Now check with initial data which must be same.
        assertEquals(prependExpected, initialExpected)

        // check with append data which must not be same.
        assertNotEquals(prependExpected, appendExpected)

    }

    @Test
    fun check_for_load_error() {
        fakeRepository.setGroupsData(null)

        runTest {
            val result = pager.refresh()
            assertTrue(result is PagingSource.LoadResult.Error)

            assertEquals(errorMessage, result.throwable.message)

            val page = pager.getLastLoadedPage()
            assertNull(page)
        }
    }

}