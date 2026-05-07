package com.rebloom.app.ui.screens.plants.add_plants

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.UserPlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPlantViewModel(app: Application) : AndroidViewModel(app) {

    data class UiState(
        val isLoading: Boolean = false,
        val selectedPhotoUri: Uri? = null,
        val errorMessage: String? = null,
        val saved: Boolean = false
    )

    private val repo = UserPlantRepository(app.applicationContext)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setPhoto(uri: Uri) {
        _uiState.update { it.copy(selectedPhotoUri = uri) }
    }

    fun clearPhoto() {
        _uiState.update { it.copy(selectedPhotoUri = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun save(name: String, plantType: String, description: String, date: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val entity = repo.addPlant(
                    nickname      = name,
                    notes         = description,
                    acquiredDate  = date,
                    plantId       = null,
                    plantTypeName = plantType,
                    photoUri      = _uiState.value.selectedPhotoUri
                )
                launch { repo.syncToRemote(entity) }
                _uiState.update { it.copy(isLoading = false, saved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
