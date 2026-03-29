package com.example.plant_app_andrturtles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp


@Composable
fun ScrollScreen(
    onOverdueClick: () -> Unit,
    viewModel: ScrollScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Табы Календарь/Список
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color(0xFFE9EED9), RoundedCornerShape(20.dp))
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "Календарь",
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (selectedTab == 0) Color(0xFF416946) else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedTab = 0 }
                                .padding(12.dp),
                            color = if (selectedTab == 0) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Список",
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (selectedTab == 1) Color(0xFF416946) else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedTab = 1 }
                                .padding(12.dp),
                            color = if (selectedTab == 1) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Фильтры (Список/Просрочено/Все)
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "Список",
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFCBD2A4), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Просрочено",
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                                .clickable { onOverdueClick() },
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Все",
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Заголовок "Задачи:"
                item {
                    Text(
                        text = "Задачи:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Секция Валентина
                item {
                    Row(
                        Modifier
                            .background(Color(0xFFE9EED9))
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Валентин",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Задачи (один список для всех)
                items(state.items) { item ->
                    ItemCard(item)
                }

                // Секция Тамары
                item {
                    Row(
                        Modifier
                            .background(Color(0xFFE9EED9))
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Тамара",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Те же самые задачи (дублируются, как в оригинале)
                items(state.items) { item ->
                    ItemCard(item)
                }
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ошибка: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ItemCard(item: ItemModel) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = item.move,  // ← Проверь: item.title, а не item.move
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Checkbox(
                checked = false,
                onCheckedChange = { }
            )
        }
    }
}