package com.rebloom.app.ui.screens.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import coil.compose.AsyncImage
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.ui.common.UiState

@Composable
fun PlantsScreen(
    onPlantClick: (Plant) -> Unit,
    onAddPlant: () -> Unit
) {
    val viewModel: PlantViewModel = viewModel()
    val plantsState = viewModel.plantState.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value
    val isEditMode = viewModel.isEditMode.collectAsState().value

    var showConfirmExit by remember { mutableStateOf(false) }
    var plantToDelete by remember { mutableStateOf<Plant?>(null) }

    if (showConfirmExit) {
        Dialog(onDismissRequest = { showConfirmExit = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = colorResource(R.color.light_green)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Закончить редактирование?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.text_primary)
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = { showConfirmExit = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.dark_green)
                        )
                    ) { Text("Остаться", color = Color.White) }
                    Button(
                        onClick = { viewModel.exitEditMode(); showConfirmExit = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_gray_transparent),
                            contentColor = colorResource(R.color.text_primary)
                        )
                    ) { Text("Выйти") }
                }
            }
        }
    }

    plantToDelete?.let { plant ->
        Dialog(onDismissRequest = { plantToDelete = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = colorResource(R.color.light_green)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            if (plant.imageUrl != null) {
                                AsyncImage(
                                    model = plant.imageUrl,
                                    contentDescription = plant.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_plants_selected),
                                    contentDescription = plant.name,
                                    modifier = Modifier.size(48.dp),
                                    tint = colorResource(R.color.text_primary)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = plant.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.text_primary)
                            )
                            Text(
                                text = plant.type,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorResource(R.color.text_secondary)
                            )
                        }
                    }
                    Text(
                        text = "Удалить растение?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.text_primary)
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = { plantToDelete = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.dark_green)
                        )
                    ) { Text("Оставить", color = Color.White) }
                    Button(
                        onClick = { viewModel.deletePlant(plant); plantToDelete = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_gray_transparent),
                            contentColor = colorResource(R.color.text_primary)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Удалить")
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.header_height))
                    .background(colorResource(R.color.bg)),
                contentAlignment = Alignment.Center
            ) {
                if (isEditMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(colorResource(R.color.light_gray_transparent))
                                .clickable { showConfirmExit = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Выйти из редактирования",
                                tint = colorResource(R.color.text_primary)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(dimensionResource(R.dimen.oval_width))
                                .height(dimensionResource(R.dimen.oval_height))
                                .clip(RoundedCornerShape(dimensionResource(R.dimen.oval_radius)))
                                .background(colorResource(R.color.light_green)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Редактирование",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.text_primary)
                            )
                        }

                        Spacer(modifier = Modifier.size(34.dp))
                    }
                } else {
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
            }

            // Search row
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
                Box(
                    modifier = if (isEditMode) {
                        Modifier
                            .weight(1f)
                            .height(dimensionResource(R.dimen.search_height))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.search_radius)))
                            .background(colorResource(R.color.light_gray_transparent))
                    } else {
                        Modifier
                            .width(dimensionResource(R.dimen.search_width))
                            .height(dimensionResource(R.dimen.search_height))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.search_radius)))
                            .background(colorResource(R.color.light_gray_transparent))
                    },
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

                if (isEditMode) {
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.button_gap)))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.button_gap)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.filter_button_size))
                            .clip(CircleShape)
                            .background(colorResource(R.color.light_gray_transparent)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = stringResource(R.string.filter_button),
                            modifier = Modifier.size(dimensionResource(R.dimen.filter_icon_size)),
                            tint = colorResource(R.color.text_primary)
                        )
                    }

                    if (!isEditMode) {
                        Box(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.edit_button_size))
                                .clip(CircleShape)
                                .background(colorResource(R.color.light_gray_transparent))
                                .clickable { viewModel.enterEditMode() },
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
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_top_pad)))

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
                            Text(text = stringResource(R.string.loading_plants))
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
                            Button(onClick = { viewModel.retry() }) {
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
                                        isEditMode = isEditMode,
                                        onClick = { onPlantClick(plant) },
                                        onDeleteClick = { plantToDelete = plant }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!isEditMode) {
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
}

@Composable
fun PlantCard(
    plant: Plant,
    isEditMode: Boolean = false,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    Box {
        Card(
            onClick = { if (!isEditMode) onClick() },
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
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.plant_image_container))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.card_radius)))
                        .background(colorResource(R.color.white)),
                    contentAlignment = Alignment.Center
                ) {
                    if (plant.imageUrl != null) {
                        AsyncImage(
                            model = plant.imageUrl,
                            contentDescription = plant.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
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

        if (isEditMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.light_gray_transparent))
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить растение",
                    tint = colorResource(R.color.text_primary),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
