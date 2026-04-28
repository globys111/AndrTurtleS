package com.rebloom.app.ui.screens.plants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.ui.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsScreen(
    onPlantClick: (Plant) -> Unit,
    onAddPlant: () -> Unit
) {
    val viewModel: PlantViewModel = viewModel()
    val plantsState = viewModel.plantState.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value
    val filteredPlants = viewModel.filteredPlants.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

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
                // Поле поиска
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.search_radius)),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.search_height))
                            .padding(horizontal = dimensionResource(R.dimen.search_inner_pad)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colorResource(R.color.text_secondary),
                            modifier = Modifier.size(dimensionResource(R.dimen.search_icon_size))
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.search_icon_gap)))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.search_hint),
                                        color = colorResource(R.color.text_secondary),
                                        fontSize = dimensionResource(R.dimen.search_text_size).value.sp
                                    )
                                }
                                innerTextField()
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.updateSearchQuery("") },
                                modifier = Modifier.size(dimensionResource(R.dimen.filter_button_size))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Очистить",
                                    tint = colorResource(R.color.text_secondary),
                                    modifier = Modifier.size(dimensionResource(R.dimen.filter_icon_size))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.button_gap)))

                // Кнопка фильтра
                Surface(
                    modifier = Modifier.size(dimensionResource(R.dimen.filter_button_size)),
                    shape = CircleShape,
                    color = colorResource(R.color.card_bg),
                    shadowElevation = 2.dp,
                    onClick = { /* TODO: фильтры */ }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = stringResource(R.string.filter_button),
                            tint = colorResource(R.color.text_primary),
                            modifier = Modifier.size(dimensionResource(R.dimen.filter_icon_size))
                        )
                    }
                }

                // Кнопка добавления
                Surface(
                    modifier = Modifier.size(dimensionResource(R.dimen.edit_button_size)),
                    shape = CircleShape,
                    color = colorResource(R.color.dark_green),
                    shadowElevation = 2.dp,
                    onClick = onAddPlant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_plant),
                            tint = Color.White,
                            modifier = Modifier.size(dimensionResource(R.dimen.edit_icon_size))
                        )
                    }
                }
            }

            // Контент
            when (plantsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = plantsState.message, color = colorResource(R.color.accent_red))
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadPlants() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    if (filteredPlants.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty())
                                    stringResource(R.string.no_plants_found)
                                else
                                    stringResource(R.string.no_plants),
                                color = colorResource(R.color.text_secondary)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = dimensionResource(R.dimen.screen_hpad),
                                vertical = dimensionResource(R.dimen.content_top_pad)
                            ),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.block_gap))
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
}

@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.card_bg))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.card_pad)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Изображение растения
            Surface(
                modifier = Modifier.size(dimensionResource(R.dimen.plant_image_container)),
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plants_selected),
                        contentDescription = plant.name,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        tint = colorResource(R.color.text_secondary)
                    )
                }
            }

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.block_gap)))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.text_primary)
                )
                Text(
                    text = plant.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.text_secondary)
                )
            }

            IconButton(
                onClick = { /* TODO: редактирование */ },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_button),
                    tint = colorResource(R.color.text_secondary),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun getDrawableResourceId(imageName: String): Int {
    return try {
        val nameWithoutExtension = if (imageName.contains(".")) {
            imageName.substringBeforeLast(".")
        } else {
            imageName
        }
        val field = R.drawable::class.java.getDeclaredField(nameWithoutExtension)
        field.getInt(null)
    } catch (e: Exception) {
        0
    }
}