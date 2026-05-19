package com.rebloom.app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.TaskDefinition
import com.rebloom.app.domain.model.TaskType
import com.rebloom.app.ui.common.UiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.rebloom.app.ui.theme.HomeFramePicture
import com.rebloom.app.ui.theme.Lighting
import com.rebloom.app.ui.theme.Tasks
import com.rebloom.app.ui.theme.TasksChooseBotton
import com.rebloom.app.ui.theme.Transplanting
import com.rebloom.app.ui.theme.Watering

@Composable
fun TasksScreen(
    onOverdueClick: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val completedIds by viewModel.completedIds.collectAsStateWithLifecycle()

    when (val state = uiState) {
        UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.block_gap)))
                    Text(text = stringResource(R.string.loading_tasks))
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

        is UiState.Success -> {
            val tasks = state.data

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                                selectedContainerColor = TasksChooseBotton
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
                        text = Tasks,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 8.dp, bottom = 4.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(tasks) { task ->
                    val taskId = task.id ?: task.hashCode().toString()
                    val isCompleted = completedIds.contains(taskId)

                    TaskCard(task = task, isCompleted = isCompleted, onToggle = { viewModel.toggleTaskCompletion(task) })
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskDefinition, isCompleted: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HomeFramePicture)
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
                    //text = "Каждые ${task.intervalDays} дня",
                    text = "Каждые 3 дня",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Checkbox(
                checked = isCompleted,
                onCheckedChange = {onToggle()}
            )
        }
    }
}

fun TaskType.toTitle(): String = when (this) {
    TaskType.WATER -> Watering
    TaskType.REPOT -> Transplanting
    TaskType.LIGHT -> Lighting
}

fun TaskType.toIconRes(): Int = when (this) {
    TaskType.WATER -> R.drawable.ic_water_today
    TaskType.REPOT -> R.drawable.ic_replant_today
    TaskType.LIGHT -> R.drawable.ic_light_overdue
}