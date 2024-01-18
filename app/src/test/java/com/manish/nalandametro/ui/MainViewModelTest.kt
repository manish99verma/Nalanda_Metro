package com.manish.nalandametro.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.manish.nalandametro.MainCoroutineRule
import com.manish.nalandametro.data.repository.FakeRepository
import com.manish.nalandametro.utils.Resource
import com.manish.retrofittest.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(FakeRepository())
    }

    @After
    fun tearDown() {

    }

    @Test
    fun `add station with empty name returns error`() {
        viewModel.addStation("", "MS", LatLng(0.0, 0.0))

        val result = viewModel.addStationResultLiveData().getOrAwaitValue()
        val content = result.getContentIfNotHandled()

        assertThat(content?.status).isEqualTo(Resource.Status.FAILED)
    }

    fun `add station with already used name returns success`() {
        viewModel.addStation("","GR", LatLng(25.027308, 85.528208))

        val result = viewModel.addStationResultLiveData().getOrAwaitValue()
        val content = result.getContentIfNotHandled()

        assertThat(content?.status).isEqualTo(Resource.Status.SUCCESS)
    }

    fun `add station with empty stationId returns returns error`() {

    }

    fun `add station with used stationId returns error`() {

    }

    fun `add station with correct stationId returns true`() {

    }
}