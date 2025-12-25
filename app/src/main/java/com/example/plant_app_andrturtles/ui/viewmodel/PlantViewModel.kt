package com.example.plant_app_andrturtles.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_app_andrturtles.data.repository.MockRepository
import com.example.plant_app_andrturtles.domain.model.Plant
import com.example.plant_app_andrturtles.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlantViewModel(app: Application): AndroidViewModel (app){

    private val repo = MockRepository(app.applicationContext)

    private val _plantsState = MutableStateFlow<UiState<List<Plant>>>(UiState.Loading)
    val plantState: StateFlow<UiState<List<Plant>>> = _plantsState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun loadPlants(){
        _plantsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val plants = repo.loadPlants()
                _plantsState.value = UiState.Success(plants)
            }catch (e: Exception){
                _plantsState.value = UiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }
}