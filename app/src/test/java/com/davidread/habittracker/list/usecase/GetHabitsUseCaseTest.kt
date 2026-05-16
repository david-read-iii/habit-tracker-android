package com.davidread.habittracker.list.usecase

import androidx.paging.PagingData
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.repository.HabitListRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert
import org.junit.Test

class GetHabitsUseCaseTest {

    private val habitListRepository = mockk<HabitListRepository>()
    private val getHabitsUseCase = GetHabitsUseCase(habitListRepository)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke() {
        val pagingData = mockk<PagingData<HabitEntity>>()
        val expectedResult = flowOf(pagingData)
        every { habitListRepository.getHabits() } returns expectedResult

        val result = getHabitsUseCase()

        Assert.assertEquals(expectedResult, result)
    }
}
