package com.example.plant_app_andrturtles.ui.screens.plants

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plant_app_andrturtles.R
import com.example.plant_app_andrturtles.domain.model.Plant
import com.example.plant_app_andrturtles.ui.state.UiState
import com.example.plant_app_andrturtles.ui.viewmodel.PlantViewModel

@Composable
fun PlaceholderScreen(@StringRes titleRes: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(titleRes))
    }
}

@Composable
fun PlantsScreen(
    onPlantClick: (Plant) -> Unit,
    onAddPlant: () -> Unit
) {
    // ViewModel
    val viewModel: PlantViewModel = viewModel()
    val plantsState = viewModel.plantState.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value

    // Загрузка при старте
    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    // Основной layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Шапка с зеленым овалом
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.header_height))
                    .background(colorResource(R.color.bg)),
                contentAlignment = Alignment.Center
            ) {
                // Зеленый овал
                Box(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.oval_width))
                        .height(dimensionResource(R.dimen.oval_height))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.oval_radius)))
                        .background(colorResource(R.color.light_green)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.plants_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.text_primary)
                    )
                }
            }

            // Панель с поиском и кнопками
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.search_bar_hpad),
                        vertical = dimensionResource(R.dimen.search_bar_vpad)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Поиск - ШИРИНА 246dp, высота 34dp
                Box(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.search_width))
                        .height(dimensionResource(R.dimen.search_height))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.search_radius)))
                        .background(colorResource(R.color.light_gray_transparent)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.search_inner_pad)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colorResource(R.color.text_secondary).copy(alpha = 0.6f),
                            modifier = Modifier.size(dimensionResource(R.dimen.search_icon_size))
                        )

                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.search_icon_gap)))

                        Text(
                            text = stringResource(R.string.search_hint),
                            color = colorResource(R.color.text_secondary).copy(alpha = 0.6f),
                            fontSize = dimensionResource(R.dimen.search_text_size).value.sp
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.button_gap)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка фильтрации - КРУГ 34dp
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.filter_button_size))
                            .clip(CircleShape)
                            .background(colorResource(R.color.light_gray_transparent)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Используем кастомную иконку фильтра или Material
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter), // Создай файл
                            contentDescription = stringResource(R.string.filter_button),
                            modifier = Modifier.size(dimensionResource(R.dimen.filter_icon_size)),
                            tint = colorResource(R.color.text_primary)
                        )
                    }

                    // Кнопка редактирования - КРУГ 34dp
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.edit_button_size))
                            .clip(CircleShape)
                            .background(colorResource(R.color.light_gray_transparent)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_button),
                            modifier = Modifier.size(dimensionResource(R.dimen.edit_icon_size)),
                            tint = colorResource(R.color.text_primary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_top_pad)))

            // Контент по состояниям
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.screen_hpad))
            ) {
                when (plantsState) {
                    UiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.block_gap)))
                            Text(
                                text = stringResource(R.string.loading_plants),
                                color = colorResource(R.color.text_primary)
                            )
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.error_title),
                                color = colorResource(R.color.accent_red)
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.block_gap)))
                            Button(
                                onClick = { viewModel.loadPlants() }
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }

                    is UiState.Success -> {
                        val plants = (plantsState as UiState.Success<List<Plant>>).data
                        val filteredPlants = if (searchQuery.isEmpty()) {
                            plants
                        } else {
                            plants.filter { plant ->
                                plant.name.contains(searchQuery, ignoreCase = true) ||
                                        plant.type.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        if (filteredPlants.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty()) {
                                        stringResource(R.string.no_plants)
                                    } else {
                                        stringResource(R.string.no_plants_found)
                                    },
                                    color = colorResource(R.color.text_secondary)
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.block_gap)),
                                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.fab_bottom_pad))
                            ) {
                                items(filteredPlants) { plant ->
                                    PlantCard(
                                        plant = plant,
                                        onClick = { onPlantClick(plant) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB кнопка добавления
        FloatingActionButton(
            onClick = onAddPlant,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = dimensionResource(R.dimen.fab_end_pad),
                    bottom = dimensionResource(R.dimen.fab_above_nav_pad)
                ),
            containerColor = colorResource(R.color.dark_green),
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Add,
                stringResource(R.string.add_plant),
                tint = colorResource(R.color.white)
            )
        }
    }
}

@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.plant_card_height)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.card_bg))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.card_pad))
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Используем изображение из ресурсов по imageName
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.plant_image_container))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.card_radius)))
                    .background(colorResource(R.color.white)),
                contentAlignment = Alignment.Center
            ) {
                // Загружаем изображение по имени из модели
                // plant.imageName должно содержать имя файла, например: "pic_plant_1"
                val imageResId = remember(plant.imageName) {
                    getDrawableResourceId(plant.imageName)
                }

                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = plant.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Фолбэк если изображение не найдено
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plants_selected),
                        contentDescription = plant.name,
                        modifier = Modifier.size(dimensionResource(R.dimen.plant_image_size) * 0.7f),
                        tint = colorResource(R.color.text_primary)
                    )
                }
            }

            Spacer(Modifier.width(dimensionResource(R.dimen.card_pad)))

            Column {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.text_primary),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.text_pad)))
                Text(
                    text = plant.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.text_secondary)
                )
            }
        }
    }
}

private fun getDrawableResourceId(imageName: String): Int {
    return try {
        val nameWithoutExtension = imageName.substringBeforeLast(".")
        val field = R.drawable::class.java.getDeclaredField(nameWithoutExtension)
        field.getInt(null)
    } catch (e: Exception) {
        0
    }
}