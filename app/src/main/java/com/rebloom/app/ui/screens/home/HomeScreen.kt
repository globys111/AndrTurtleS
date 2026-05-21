package com.rebloom.app.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.domain.usecase.TaskScheduler
import com.rebloom.app.ui.common.UiState
import com.rebloom.app.ui.screens.profile.ProfileViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onOpenPlant: (Plant) -> Unit,
    vm: HomeViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.load() }
    val profileState by profileViewModel.uiState.collectAsState()
    val state by vm.state.collectAsState()
    val side = dimensionResource(R.dimen.screen_hpad)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(horizontal = side)
    ) {
        when (val s = state) {
            UiState.Loading -> {
                LoadingState()
            }

            is UiState.Error -> {
                ErrorState(message = s.message, onRetry = { vm.load() })
            }

            is UiState.Success -> {
                val data = s.data

                val plantsById = remember(data.plants) { data.plants.associateBy { it.id } }

                val tasksForSelected = remember(data.allOccurrences, data.selectedDate) {
                    TaskScheduler.tasksForDate(data.allOccurrences, data.selectedDate)
                }

                val tasksToday = remember(data.allOccurrences, data.today) {
                    TaskScheduler.tasksForDate(data.allOccurrences, data.today)
                }

                val doneToday = tasksToday.count { it.isCompleted }
                val totalToday = tasksToday.size.coerceAtLeast(1)

                val monthName = data.selectedDate.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
                    .replaceFirstChar { it.titlecase(Locale("ru")) }
                val monthTitle = "$monthName ${data.selectedDate.year}"

                val greeting = stringResource(greetingRes(LocalTime.now()))

                val days = remember(data.selectedDate) {
                    // 7 дней вокруг выбранной даты (как в макете — 7 элементов)
                    (-3..3).map { data.selectedDate.plusDays(it.toLong()) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    item {
                        HomeTopBar(
                            greetingText = greeting,
                            hasNewNotifications = true,
                            avatarUrl = profileState.avatarUrl,
                            onProfileClick = onOpenProfile,
                            onNotificationsClick = { /* позже */ }
                        )
                    }

                    item {
                        CalendarAndTasksCard(
                            monthTitle = monthTitle,
                            days = days,
                            selectedDate = data.selectedDate,
                            onSelectDate = vm::selectDate,
                            tasks = tasksForSelected,
                            plantById = { id -> plantsById[id] },
                            emptyText = stringResource(R.string.no_tasks)
                        )
                    }

                    item {
                        DoneTodayAndMascot(
                            done = doneToday,
                            total = totalToday
                        )
                    }

                    item {
                        PlantsSection(
                            title = stringResource(R.string.plants_title),
                            plants = data.plants.take(3),
                            topTasksForPlant = { plantId ->
                                TaskScheduler.top3ForPlant(data.allOccurrences, plantId)
                            },
                            onPlantClick = onOpenPlant
                        )
                    }
                }
            }
        }
    }
}

@StringRes
private fun greetingRes(now: LocalTime): Int {
    return when (now.hour) {
        in 5..11 -> R.string.greeting_morning
        in 12..16 -> R.string.greeting_day
        in 17..22 -> R.string.greeting_evening
        else -> R.string.greeting_night
    }
}
