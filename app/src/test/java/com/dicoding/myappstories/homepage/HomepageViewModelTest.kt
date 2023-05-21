package com.dicoding.myappstories.homepage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.myappstories.db.model.Stories
import com.dicoding.myappstories.db.repository.StoriesRepository
import com.dicoding.myappstories.ui.viewmodel.HomepageViewModel
import com.dicoding.myappstories.ui.adapter.StoriesAdapter
import com.dicoding.myappstories.utilities.CoroutinesTestRules
import com.dicoding.myappstories.utilities.DataDummy
import com.dicoding.myappstories.utilities.PagingTestingData
import com.dicoding.myappstories.utilities.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class HomepageViewModelTest {

    private lateinit var homepageViewModel: HomepageViewModel
    private val dummyToken = "authentication_token"

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRules()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoriesRepository

    @Before
    fun setUp() {
        homepageViewModel = HomepageViewModel(repository)
    }

    @Test
    fun `Get all empty list of stories`() = runBlockingTest {
        val dummyStories = emptyList<Stories>()
        val data = PagingTestingData.snap(dummyStories)
        val stories = flowOf(data)
        `when`(repository.getAllStories(dummyToken)).thenReturn(stories)

        val actualStories = homepageViewModel.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        verify(repository).getAllStories(dummyToken)

        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    @Test
    fun `Get all non-empty list of stories`() = runBlockingTest {
        val dummyStories = DataDummy.generateDummyListStoriesResponse()
        val data = PagingTestingData.snap(dummyStories)
        val stories = flowOf(data)
        `when`(repository.getAllStories(dummyToken)).thenReturn(stories)

        val actualStories = homepageViewModel.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        verify(repository).getAllStories(dummyToken)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories.first(), differ.snapshot().first())
    }
}