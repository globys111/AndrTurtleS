package com.rebloom.app.ui.screens.plants

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.UserPlantRepository
import com.rebloom.app.domain.model.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import com.rebloom.app.data.repository.PlantCareRepository

private const val TAG = "AddEditVM"
private val careRepo = PlantCareRepository()

class AddEditPlantViewModel(app: Application) : AndroidViewModel(app) {

    data class UiState(
        val isLoading: Boolean = false,
        val name: String = "",
        val plantType: String = "",
        val description: String = "",
        val dateIso: String = "",
        val dateDisplay: String = "",
        val existingImageUrl: String? = null,
        val newPhotoUri: Uri? = null,
        val errorMessage: String? = null,
        val saved: Boolean = false
    )

    private val repo = UserPlantRepository(app.applicationContext)
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun initFromPlant(plant: Plant) {
        _uiState.update {
            it.copy(
                name             = plant.name,
                plantType        = plant.type,
                description      = plant.description,
                dateDisplay      = plant.plantingDate,
                dateIso          = displayToIso(plant.plantingDate),
                existingImageUrl = plant.imageUrl
            )
        }
    }

    fun setPhoto(uri: Uri)   { _uiState.update { it.copy(newPhotoUri = uri) } }
    fun clearError()         { _uiState.update { it.copy(errorMessage = null) } }
    fun updateName(v: String)      { _uiState.update { it.copy(name = v) } }
    fun updatePlantType(v: String) { _uiState.update { it.copy(plantType = v) } }
    fun updateDescription(v: String) { _uiState.update { it.copy(description = v) } }
    fun updateDate(iso: String, disp: String) {
        _uiState.update { it.copy(dateIso = iso, dateDisplay = disp) }
    }

    fun create() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val s = _uiState.value
                val wateringLevel = careRepo.getWateringLevel(s.plantType.trim())
                val entity = repo.addPlant(
                    nickname      = s.name,
                    notes         = s.description,
                    acquiredDate  = s.dateIso,
                    plantId       = null,
                    plantTypeName = s.plantType,
                    wateringLevel = wateringLevel
                )
                Log.d(TAG, "create: addPlant done id=${entity.id}, starting uploadPhotoAndSync")
                val result = withTimeoutOrNull(15_000L) { repo.uploadPhotoAndSync(entity, s.newPhotoUri) }
                Log.d(TAG, "create: uploadPhotoAndSync finished result=$result")
                _uiState.update { it.copy(isLoading = false, saved = true) }
            } catch (e: Exception) {
                Log.e(TAG, "create: FAILED", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun update(plantId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val s = _uiState.value
                val wateringLevel = careRepo.getWateringLevel(s.plantType.trim())
                val entity = repo.updatePlant(
                    plantId       = plantId,
                    nickname      = s.name,
                    notes         = s.description,
                    acquiredDate  = s.dateIso,
                    plantTypeName = s.plantType,
                    wateringLevel = wateringLevel
                )
                Log.d(TAG, "update: updatePlant done, starting uploadPhotoAndSync")
                val result = withTimeoutOrNull(15_000L) { repo.uploadPhotoAndSync(entity, s.newPhotoUri) }
                Log.d(TAG, "update: uploadPhotoAndSync finished result=$result")
                _uiState.update { it.copy(isLoading = false, saved = true) }
            } catch (e: Exception) {
                Log.e(TAG, "update: FAILED", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun displayToIso(display: String): String {
        if (display.isEmpty()) return ""
        return try {
            val p = display.split(".")
            if (p.size == 3) "${p[2]}-${p[1]}-${p[0]}" else display
        } catch (_: Exception) { "" }
    }
}
