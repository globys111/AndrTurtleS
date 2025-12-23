package com.example.plant_app_andrturtles.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_app_andrturtles.data.repository.MockRepository
import com.example.plant_app_andrturtles.domain.model.Plant
import com.example.plant_app_andrturtles.domain.model.TaskOccurrence
import com.example.plant_app_andrturtles.domain.usecase.TaskEngine
import com.example.plant_app_andrturtles.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeData(
    val plants: List<Plant>,
    val allOccurrences: List<TaskOccurrence>,
    val selectedDate: LocalDate,
    val today: LocalDate
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MockRepository(app.applicationContext)

    private val _state = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val state: StateFlow<UiState<HomeData>> = _state

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val plants = repo.loadPlants()
                val defs = repo.loadTaskDefinitions()
                val today = LocalDate.now()
                val occ = TaskEngine.generateOccurrences(defs, today = today)
                _state.value = UiState.Success(
                    HomeData(
                        plants = plants,
                        allOccurrences = occ,
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
