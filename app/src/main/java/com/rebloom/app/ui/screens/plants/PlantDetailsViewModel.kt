package com.rebloom.app.ui.screens.plants

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.UserPlantRepository
import com.rebloom.app.domain.model.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantDetailsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UserPlantRepository(app.applicationContext)
    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: StateFlow<Plant?> = _plant.asStateFlow()

    fun load(plantId: String) {
        viewModelScope.launch {
            repo.observePlantById(plantId).collect { _plant.value = it }
        }
    }
}
