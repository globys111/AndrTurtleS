package com.example.plant_app_andrturtles

object MockDataRepositoryRed {
    fun getItems(): List<ItemModel> {
        return listOf(
            ItemModel(1, "Полив","Тамара(фикус)",R.drawable.ic_light_overdue),
            ItemModel(2, "Освещение","София(аглаонема)",R.drawable.ic_water_overdue)
        )
    }
}
