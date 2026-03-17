package com.example.plant_app_andrturtles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiStateRed {
    object Loading : UiStateRed()
    data class Success(val items: List<ItemModel>) : UiStateRed()
    data class Error(val message: String) : UiStateRed()
}

class ScrollScreenViewModelRed : ViewModel() {

    private val _uiStateRed = MutableStateFlow<UiStateRed>(UiStateRed.Loading)
    val uiStateRed: StateFlow<UiStateRed> = _uiStateRed

    init {
        loadItemsRed()
    }

    private fun loadItemsRed() {
        viewModelScope.launch {
            try {
                delay(1500)
                val items = MockDataRepositoryRed.getItems()
                _uiStateRed.value = UiStateRed.Success(items)
            } catch (e: Exception) {
                _uiStateRed.value = UiStateRed.Error(e.message ?: "Ошибка")
            }
        }
    }
}