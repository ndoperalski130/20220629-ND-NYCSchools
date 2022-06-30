package com.example.a20220629_noahdoperalski_nycschools.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.a20220629_noahdoperalski_nycschools.model.Schools
import com.example.a20220629_noahdoperalski_nycschools.model.Scores
import com.example.a20220629_noahdoperalski_nycschools.network.NetworkState
import com.example.a20220629_noahdoperalski_nycschools.network.RepositoryLayer
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SchoolsViewModelTest {

    @get: Rule val rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockRepository = mockk<RepositoryLayer>(relaxed = true)

    private lateinit var viewModelTest: SchoolsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModelTest = SchoolsViewModel(mockRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `get SAT scores when there is data available returning a success state`() {
        every { mockRepository.getSchoolSATScores("dbn") } returns flowOf(
            NetworkState.SUCCESS<List<Scores>>(listOf(mockk(), mockk()))
        )
        viewModelTest.schools = mockk {
            every { dbn } returns "dbn"
        }
        val lvStates = mutableListOf<NetworkState>()
        viewModelTest.scoresLV.observeForever {
            lvStates.add(it)
        }

        viewModelTest.getSATScoresBySchool()

        assertEquals(2, lvStates.size)
        assertEquals(2, (lvStates[1] as NetworkState.SUCCESS<List<Scores>>).response.size)
    }

    @Test
    fun `get SAT scores when there is data available returning a loading state`() {
        every { mockRepository.getSchoolSATScores("dbn") } returns flowOf(
            NetworkState.LOADING()
        )
        viewModelTest.schools = mockk {
            every { dbn } returns "dbn"
        }
        val lvStates = mutableListOf<NetworkState>()
        viewModelTest.scoresLV.observeForever {
            lvStates.add(it)
        }

        viewModelTest.getSATScoresBySchool()

        assertEquals(2, lvStates.size)
        assertTrue((lvStates[1] as NetworkState.LOADING).isLoading)
    }

    @Test
    fun `get SAT scores when there is no school object null returns an error state`() {
        viewModelTest.schools = null
        val lvStates = mutableListOf<NetworkState>()
        viewModelTest.scoresLV.observeForever {
            lvStates.add(it)
        }

        viewModelTest.getSATScoresBySchool()

        assertEquals(2, lvStates.size)
        assertEquals("No school set", (lvStates[1] as NetworkState.ERROR).error.localizedMessage)
    }

    @Test
    fun `get SAT scores when there is no dbn property null returns an error state`() {
        viewModelTest.schools = mockk {
            every { dbn } returns null
        }
        val lvStates = mutableListOf<NetworkState>()
        viewModelTest.scoresLV.observeForever {
            lvStates.add(it)
        }

        viewModelTest.getSATScoresBySchool()

        assertEquals(2, lvStates.size)
        assertEquals("No dbn found", (lvStates[1] as NetworkState.ERROR).error.localizedMessage)
    }

    @Test
    fun `get all schools when there is data available returns a success state`() {
        every { mockRepository.getAllNycSchools() } returns flowOf(
            NetworkState.SUCCESS<List<Schools>>(listOf(mockk(), mockk()))
        )
        val lvStates = mutableListOf<NetworkState>()

        val testObject = SchoolsViewModel(mockRepository, testDispatcher)

        testObject.schoolsLV.observeForever {
            lvStates.add(it)
        }

        assertEquals(1, lvStates.size)
    }

    @Test
    fun `get all schools when there is data available returns a loading state`() {
        every { mockRepository.getAllNycSchools() } returns flowOf(
            NetworkState.LOADING()
        )
        val lvStates = mutableListOf<NetworkState>()
        val testObject = SchoolsViewModel(mockRepository, testDispatcher)

        testObject.schoolsLV.observeForever {
            lvStates.add(it)
        }

        assertEquals(1, lvStates.size)
        assertTrue((lvStates[0] as NetworkState.LOADING).isLoading)
    }

    @Test
    fun `get all schools when there is no data available returns a error state`() {
        every { mockRepository.getAllNycSchools() } returns flowOf(
            NetworkState.ERROR(Exception("ERROR"))
        )
        val lvStates = mutableListOf<NetworkState>()
        val testObject = SchoolsViewModel(mockRepository, testDispatcher)

        testObject.schoolsLV.observeForever {
            lvStates.add(it)
        }

        assertEquals(1, lvStates.size)
    }
}