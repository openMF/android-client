package com.mifos.feature.search

/**
 * Created by Aditya Gupta on 02/09/23.
 */
//@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {
    // TODO:: fix the test cases

//    @Mock
//    lateinit var searchRepository: SearchRepository
//
//    private lateinit var searchViewModel: SearchViewModel
//
//    private val dispatcher: TestDispatcher = StandardTestDispatcher()
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Before
//    fun setUp() {
//        MockitoAnnotations.openMocks(this)
//        searchViewModel = SearchViewModel(searchRepository)
//        Dispatchers.setMain(dispatcher)
//    }

//    @Test
//    fun testSearchAll_SuccessfulSearchAllReceivedFromUseCase_ReturnsSuccess() {
//        runTest {
//            Mockito.`when`(
//                searchRepository.searchResources(
//                    Mockito.anyString(),
//                    Mockito.anyString(),
//                    Mockito.anyBoolean()
//                )
//            ).thenReturn(
//                flowOf(listOf(SearchedEntity()))
//            )
//        }
//
//        runTest {
//            searchViewModel.searchResources("query", "resources", false)
//        }
//
//        assertNotEquals(0, searchViewModel.searchUiState.value.searchedEntities.size)
//        assertEquals(searchViewModel.searchUiState.value.isLoading, false)
//        assertNull(searchViewModel.searchUiState.value.error)
//    }
//
//    @Test
//    fun testSearchAll_UnsuccessfulSearchAllReceivedFromUseCase_ReturnsError() {
//        Mockito.`when`(
//            searchRepository.searchResources(
//                Mockito.anyString(),
//                Mockito.anyString(),
//                Mockito.anyBoolean()
//            )
//        ).thenReturn(
//            flowOf(Resource.Error("some error message"))
//        )
//
//        runTest {
//            searchViewModel.searchResources("query", "resources", false)
//        }
//
//        assertEquals(0, searchViewModel.searchUiState.value.searchedEntities.size)
//        assertEquals(searchViewModel.searchUiState.value.isLoading, false)
//        assertNotNull(searchViewModel.searchUiState.value.error)
//    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
}
