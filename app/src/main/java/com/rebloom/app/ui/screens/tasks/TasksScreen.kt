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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.TaskOccurrence

import com.rebloom.app.ui.components.taskIconRes
import com.rebloom.app.ui.theme.HomeFramePicture
import com.rebloom.app.ui.theme.Tasks
import com.rebloom.app.ui.theme.TasksChooseBotton

@Composable
fun TasksScreen(
    onOverdueClick: () -> Unit,   // можно убрать, если не нужен
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val completedIds by viewModel.completedIds.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Переключатели фильтров
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            FilterChip(
                selected = filter == TaskViewModel.TaskFilter.LIST,
                onClick = { viewModel.setFilter(TaskViewModel.TaskFilter.LIST) },
                label = { Text("Список") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TasksChooseBotton
                )
            )
            FilterChip(
                selected = filter == TaskViewModel.TaskFilter.OVERDUE,
                onClick = { viewModel.setFilter(TaskViewModel.TaskFilter.OVERDUE) },
                label = { Text("Просрочено") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = filter == TaskViewModel.TaskFilter.ALL,
                onClick = { viewModel.setFilter(TaskViewModel.TaskFilter.ALL) },
                label = { Text("Все") },
                modifier = Modifier.weight(1f)
            )
        }

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет задач", color = Color.Gray)
            }
        } else {
            Text(
                text = Tasks,   // строка "Задачи"
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { "${it.definitionId}_${it.date}" }) { task ->
                    val isCompleted = completedIds.contains("${task.definitionId}_${task.date}")
                    TaskCard(
                        task = task,
                        isCompleted = isCompleted,
                        onToggle = { viewModel.toggleTaskCompletion(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskOccurrence, isCompleted: Boolean, onToggle: () -> Unit) {
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
            // Иконка задачи через общую функцию
            Icon(
                painter = painterResource(taskIconRes(task.type, task.status, isCompleted)),
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
                    text = "Полив",   // или R.string.task_water
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${task.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() }
            )
        }
    }
}