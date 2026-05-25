package com.rebloom.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.UserPlantRepository
import com.rebloom.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.rebloom.app.domain.usecase.TaskScheduler

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val plantRepo = UserPlantRepository(app.applicationContext)

    private val _state = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val state: StateFlow<UiState<HomeData>> = _state

    init {
        viewModelScope.launch { load() }
        viewModelScope.launch {
            try {
                plantRepo.syncFromRemote()
                val current = _state.value
                if (current is UiState.Success) {
                    val plants = plantRepo.observePlants().first()
                    _state.value = UiState.Success(current.data.copy(plants = plants))
                }
            } catch (_: Exception) {}
        }
    }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val plants = plantRepo.observePlants().first()
                val today = LocalDate.now()

                val wateringDefs = TaskScheduler.generateWateringDefinitions(plants, today)
                val allOccurrences = TaskScheduler.generateOccurrences(
                    defs = wateringDefs,
                    today = today
                )

                _state.value = UiState.Success(
                    HomeData(
                        plants = plants,
                        allOccurrences = allOccurrences,
                        selectedDate = today,
                        today = today
                    )
                )
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectDate(date: LocalDate) {
        val current = _state.value
        if (current is UiState.Success) {
            _state.value = UiState.Success(current.data.copy(selectedDate = date))
        }
    }
}
