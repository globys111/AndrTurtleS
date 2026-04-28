package com.rebloom.app.ui.screens.plants

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.PlantRepository
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlantViewModel(app: Application): AndroidViewModel(app) {

    private val repo = PlantRepository(app.applicationContext)

    private val _plantsState = MutableStateFlow<UiState<List<Plant>>>(UiState.Loading)
    val plantState: StateFlow<UiState<List<Plant>>> = _plantsState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Отфильтрованные растения для поиска
    private val _filteredPlants = MutableStateFlow<List<Plant>>(emptyList())
    val filteredPlants: StateFlow<List<Plant>> = _filteredPlants

    fun loadPlants(){
        _plantsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val plants = repo.loadPlants()
                _plantsState.value = UiState.Success(plants)
                _filteredPlants.value = plants // Изначально показываем все
            }catch (e: Exception){
                _plantsState.value = UiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
        filterPlants(query)
    }

    private fun filterPlants(query: String) {
        val currentState = _plantsState.value
        if (currentState is UiState.Success) {
            val allPlants = currentState.data
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
}