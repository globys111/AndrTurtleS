package com.rebloom.app.ui.screens.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rebloom.app.R
import com.rebloom.app.domain.model.Plant
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun PlantDetailsScreen(
    plant: Plant?,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    // ПРОСТО ПРОВЕРКА НА NULL И ВСЕ
    if (plant == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.bg)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(R.color.text_primary)
            )
        }
        return
    }

    // ОСНОВНОЙ КОНТЕНТ
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(colorResource(R.color.bg).copy(alpha = 0.4f)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = colorResource(R.color.text_primary)
                )
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.text_secondary))
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Редактировать",
                    tint = colorResource(R.color.text_primary),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                if (plant.imageUrl != null) {
                    AsyncImage(
                        model = plant.imageUrl,
                        contentDescription = plant.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(R.color.bg)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plants_selected),
                            contentDescription = plant.name,
                            modifier = Modifier.size(64.dp),
                            tint = colorResource(R.color.text_primary)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.bg))
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.text_primary)
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = plant.type,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.text_primary)
                    ),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = calculatePlantAge(plant.plantingDate),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(R.color.text_primary)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = plant.plantingDate,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(R.color.text_primary).copy(alpha = 0.4f)
                        )
                    )
                }

                Divider(
                    color = colorResource(R.color.text_primary).copy(alpha = 0.6f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = stringResource(R.string.view_description),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.4f)
                    )
                )

                Text(
                    text = plant.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.text_primary)
                    ),
                    modifier = Modifier.padding(top = 12.dp)
                )

                Divider(
                    color = colorResource(R.color.text_primary).copy(alpha = 0.6f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.view_care),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(R.color.text_primary).copy(alpha = 0.6f)
                        )
                    )

                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Настройки",
                            tint = colorResource(R.color.text_primary).copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                CareTaskItem(
                    icon = Icons.Filled.WbSunny,
                    title = "Освещение",
                    plantName = "${plant.name} (${plant.type})",
                    borderWidth = 1.5.dp,
                    iconColor = colorResource(R.color.text_primary),
                    borderColor = colorResource(R.color.text_primary),
                    showTodayBadge = false
                )

                Spacer(modifier = Modifier.height(28.dp))

                CareTaskItem(
                    icon = Icons.Outlined.LocalFlorist,
                    title = "Пересадка",
                    plantName = "${plant.name} (${plant.type})",
                    borderWidth = 3.dp,
                    iconColor = colorResource(R.color.light_green),
                    borderColor = colorResource(R.color.light_green),
                    showTodayBadge = true
                )

                Spacer(modifier = Modifier.height(28.dp))

                CareTaskItem(
                    icon = Icons.Outlined.WaterDrop,
                    title = "Полив",
                    plantName = "${plant.name} (${plant.type})",
                    borderWidth = 3.dp,
                    iconColor = colorResource(R.color.light_green),
                    borderColor = colorResource(R.color.light_green),
                    showTodayBadge = true
                )

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

private fun calculatePlantAge(plantingDate: String): String {
    if (plantingDate.isEmpty()) return ""

    return try {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val plantDate = dateFormat.parse(plantingDate)
        val today = Calendar.getInstance().time

        val diffInMillis = today.time - plantDate.time
        val years = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 365

        when {
            years == 0L -> {
                val months = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 30
                when {
                    months == 0L -> "Недавно посажено"
                    months == 1L -> "1 месяц"
                    months in 2..4 -> "$months месяца"
                    months in 5..11 -> "$months месяцев"
                    else -> "$years лет"
                }
            }
            years == 1L -> "1 год"
            years in 2..4 -> "$years года"
            else -> "$years лет"
        }
    } catch (e: Exception) {
        "Неизвестный возраст"
    }
}

@Composable
fun CareTaskItem(
    icon: ImageVector,
    title: String,
    plantName: String,
    borderWidth: Dp,
    iconColor: Color,
    borderColor: Color,
    showTodayBadge: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(55.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (showTodayBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .background(
                            color = colorResource(R.color.light_green),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "Сегодня",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(R.color.text_primary)
                )
            )

            Text(
                text = plantName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(R.color.text_primary)
                )
            )
        }
    }
}

