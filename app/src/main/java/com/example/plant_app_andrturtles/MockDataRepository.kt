package com.example.plant_app_andrturtles
import com.example.plant_app_andrturtles.R

object MockDataRepository {
    fun getItems(): List<ItemModel> {
        return listOf(
            ItemModel(1,  R.drawable.ic_water_today),
            ItemModel(2,  R.drawable.ic_replant_today),
            ItemModel(3,  R.drawable.ic_light_overdue),
            ItemModel(4,  R.drawable.ic_water_overdue)
        )
    }
}