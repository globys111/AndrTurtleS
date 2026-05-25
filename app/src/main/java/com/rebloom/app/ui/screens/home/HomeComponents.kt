package com.rebloom.app.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebloom.app.R
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.domain.model.TaskOccurrence
import com.rebloom.app.domain.model.TaskStatus
import com.rebloom.app.domain.model.TaskType
import com.rebloom.app.ui.theme.GreenFrame
import com.rebloom.app.ui.theme.HomeCounterTasks
import com.rebloom.app.ui.theme.HomeFramePicture
import com.rebloom.app.ui.theme.HomeTextDay
import com.rebloom.app.ui.theme.HomeTextDow
import com.rebloom.app.ui.theme.TaskisEmpty
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun HomeTopBar(
    greetingText: String,
    hasNewNotifications: Boolean,
    avatarUrl: String?,                                // <-- добавить параметр
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(41.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable { onProfileClick() }
        ) {
            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_profile_main_screen),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = greetingText,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        val bellRes = if (hasNewNotifications) R.drawable.ic_bell_new else R.drawable.ic_bell_no_new
        Image(
            painter = painterResource(bellRes),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable { onNotificationsClick() }
        )
    }
}

@Composable
fun CalendarAndTasksCard(
    monthTitle: String,
    days: List<LocalDate>,
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    tasks: List<TaskOccurrence>,
    plantById: (String) -> Plant?,
    emptyText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GreenFrame)
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = monthTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(days) { d ->
                DayChip(
                    date = d,
                    isSelected = d == selectedDate,
                    onClick = { onSelectDate(d) }
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (tasks.isEmpty()) {
            Text(
                text = emptyText,
                fontSize = 14.sp,
                color = TaskisEmpty,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 16.dp)) {
                tasks.take(3).forEach { t ->
                    TaskRow(
                        task = t,
                        plant = plantById(t.plantId),
                        badgeText = if (!t.isCompleted && t.status == TaskStatus.TODAY) stringResource(R.string.today_badge) else null
                    )
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
    val day = date.dayOfMonth.toString().padStart(2, '0')

    if (isSelected) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 66.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF618262))
                .clickable { onClick() }
                .padding(horizontal = 8.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = dow, fontSize = 20.sp, color = Color.White)
                Text(text = day, fontSize = 20.sp, color = Color.White)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .size(width = 25.dp, height = 48.dp)
                .clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dow, fontSize = 20.sp, color = HomeTextDow)
            Text(text = day, fontSize = 20.sp, color = HomeTextDay)
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskOccurrence,
    plant: Plant?,
    badgeText: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .padding(start = 10.dp)
            ) {
                Image(
                    painter = painterResource(taskIconRes(task.type, task.status, task.isCompleted)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-2).dp)
                            .size(width = 42.dp, height = 13.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badgeText, fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }

        val title = stringResource(taskTypeTitleRes(task.type))
        val subtitle = if (plant != null) "${plant.name} (${plant.type})" else ""

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, fontSize = 14.sp)
        }

        val checkRes = if (task.isCompleted) {
            R.drawable.ic_check_box_checked
        } else {
            R.drawable.ic_check_box_not_checked
        }

        Image(
            painter = painterResource(checkRes),
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .padding(10.dp)
        )
    }
}

@Composable
fun DoneTodayAndMascot(
    done: Int,
    total: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .width(138.dp)
                .padding(bottom = 25.dp)
        ) {
            Text(
                text = "$done/$total",
                fontSize = 48.sp,
                color = HomeCounterTasks,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
            )
            Text(
                text = stringResource(R.string.done_tasks_subtitle),
                fontSize = 16.sp,
                color = HomeTextDow,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
            )
            Text(
                text = stringResource(R.string.today_word),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 6.dp, start = 10.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Image(
            painter = painterResource(R.drawable.pic_mascot),
            contentDescription = null,
            modifier = Modifier
                .size(width = 154.dp, height = 203.dp)
        )
    }
}

@Composable
fun PlantsSection(
    title: String,
    plants: List<Plant>,
    topTasksForPlant: (String) -> List<TaskOccurrence>,
    onPlantClick: (Plant) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 15.dp, bottom = 5.dp)
        )

        Spacer(Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 0.dp)
        ) {
            plants.forEach { plant ->
                PlantCard(
                    plant = plant,
                    tasks = topTasksForPlant(plant.id),
                    onClick = { onPlantClick(plant) }
                )
            }
        }
    }
}

@Composable
private fun PlantCard(
    plant: Plant,
    tasks: List<TaskOccurrence>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(111.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(HomeFramePicture)
            .clickable { onClick() }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (plant.imageUrl != null) {
                coil.compose.AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.ic_plants_selected),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = colorResource(R.color.text_primary)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = plant.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = plant.type, fontSize = 13.sp, color = Color(0x99242823))

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                tasks.take(3).forEach { t ->
                    Box(modifier = Modifier.size(55.dp)) {
                        Image(
                            painter = painterResource(taskIconRes(t.type, t.status, t.isCompleted)),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (!t.isCompleted && t.status == TaskStatus.TODAY) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (-2).dp)
                                    .size(width = 42.dp, height = 13.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(HomeCounterTasks),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(R.string.today_badge), fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.block_gap)))
            Text(text = stringResource(R.string.loading_home))
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Text(text = stringResource(R.string.error_title), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(text = message)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors()) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@StringRes
private fun taskTypeTitleRes(type: TaskType): Int =
    when (type) {
        TaskType.WATER -> R.string.task_water
        TaskType.REPOT -> R.string.task_repot
        TaskType.LIGHT -> R.string.task_light
    }

@DrawableRes
private fun taskIconRes(type: TaskType, status: TaskStatus, isCompleted: Boolean): Int {
    return when (type) {
        TaskType.WATER -> when {
            isCompleted -> R.drawable.ic_water_done
            status == TaskStatus.OVERDUE -> R.drawable.ic_water_overdue
            status == TaskStatus.TODAY -> R.drawable.ic_water_today
            else -> R.drawable.ic_water_future
        }

        TaskType.REPOT -> when {
            isCompleted -> R.drawable.ic_replant_done
            status == TaskStatus.OVERDUE -> R.drawable.ic_replant_overdue
            status == TaskStatus.TODAY -> R.drawable.ic_replant_today
            else -> R.drawable.ic_replant_future
        }

        TaskType.LIGHT -> when {
            isCompleted -> R.drawable.ic_light_done
            status == TaskStatus.OVERDUE -> R.drawable.ic_light_overdue
            status == TaskStatus.TODAY -> R.drawable.ic_light_today
            else -> R.drawable.ic_light_future
        }
    }
}

/**
 * Если у тебя есть drawable по имени из JSON (например "plant_1"),
 * вернёт id. Если нет — вернёт null.
 */
private fun drawableIdByName(context: android.content.Context, name: String): Int? {
    val id = context.resources.getIdentifier(name, "drawable", context.packageName)
    return id.takeIf { it != 0 }
}
