package com.example.plant_app_andrturtles

object MockDataRepository {
    fun getItems(): List<ItemModel> {
        return listOf(
            ItemModel(1, "Полив", "Валентин(сансиеверия)", R.drawable.ic_water_today),
            ItemModel(2, "Пересадка", "Валентин(сансиеверия)", R.drawable.ic_replant_today),
        )
    }
}
