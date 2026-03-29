package com.rebloom.app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.TaskDefinition
import com.rebloom.app.domain.model.TaskType
import com.rebloom.app.ui.common.UiState

@Composable
fun TasksScreen(
    onOverdueClick: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        UiState.Loading -> {
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
                Text(
                    text = "Ошибка: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is UiState.Success -> {
            val tasks = state.data

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Фильтры
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(4.dp)
                    ) {
                        FilterChip(
                            selected = true,
                            onClick = {},
                            label = { Text("Список") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFCBD2A4)
                            )
                        )
                        FilterChip(
                            selected = false,
                            onClick = onOverdueClick,
                            label = { Text("Просрочено") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = false,
                            onClick = {},
                            label = { Text("Все") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Задачи:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 8.dp, bottom = 4.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(tasks) { task ->
                    TaskCard(task = task)
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskDefinition) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EED9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = task.type.toIconRes()),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = task.type.toTitle(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Растение ID: ${task.plantId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (task.isRepeating) {
                    Text(
                        text = "Каждые ${task.intervalDays} дней",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Checkbox(
                checked = false,
                onCheckedChange = {}
            )
        }
    }
}

fun TaskType.toTitle(): String = when (this) {
    TaskType.WATER -> "Полив"
    TaskType.REPOT -> "Пересадка"
    TaskType.LIGHT -> "Освещение"
}

fun TaskType.toIconRes(): Int = when (this) {
    TaskType.WATER -> R.drawable.ic_water_today
    TaskType.REPOT -> R.drawable.ic_replant_today
    TaskType.LIGHT -> R.drawable.ic_light_overdue
}