package com.rebloom.app.ui.screens.plants

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.UserPlantRepository
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UserPlantRepository(app.applicationContext)

    val plantState: StateFlow<UiState<List<Plant>>> = repo.observePlants()
        .map { UiState.Success(it) as UiState<List<Plant>> }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    // Отфильтрованные растения для поиска
    private val _filteredPlants = MutableStateFlow<List<Plant>>(emptyList())
    val filteredPlants: StateFlow<List<Plant>> = _filteredPlants

    init {
        viewModelScope.launch {
            try { repo.syncFromRemote() } catch (_: Exception) {}
        }
    }

    fun retry() {
        viewModelScope.launch {
            try { repo.syncFromRemote() } catch (_: Exception) {}
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterPlants(query)
    }

    private fun filterPlants(query: String) {
        val currentPlants = plantState.value
        if (currentPlants is UiState.Success) {
            val allPlants = currentPlants.data
            if (query.isBlank()) {
                _filteredPlants.value = allPlants
            } else {
                val lowerQuery = query.lowercase().trim()
                val filtered = allPlants.filter { plant ->
                    plant.name.lowercase().contains(lowerQuery) ||
                            plant.type.lowercase().contains(lowerQuery)
                }
                _filteredPlants.value = filtered
            }
        }
    }

    fun enterEditMode() { _isEditMode.value = true }
    fun exitEditMode()  { _isEditMode.value = false }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            try { repo.deletePlant(plant) } catch (_: Exception) {}
        }
    }
}