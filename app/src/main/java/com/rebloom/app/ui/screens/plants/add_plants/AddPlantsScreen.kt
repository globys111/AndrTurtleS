package com.rebloom.app.ui.screens.plants.add_plants

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.rebloom.app.ui.theme.ContainerColor
import com.rebloom.app.ui.theme.GreenFrame
import com.rebloom.app.ui.theme.HomeTextDay
import com.rebloom.app.ui.theme.HomeTextDow
import com.rebloom.app.ui.theme.TasksChooseBotton
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantsScreen(
    onClose: () -> Unit = {},
    viewModel: AddPlantViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var plantType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateIso by remember { mutableStateOf("") }
    var dateDisplay by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateDisplayFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val dateStorageFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val spacerHeight by animateDpAsState(
        targetValue = if (uiState.selectedPhotoUri != null) 60.dp else 200.dp,
        label = "photo_spacer"
    )

    LaunchedEffect(uiState.selectedPhotoUri) {
        if (uiState.selectedPhotoUri != null) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.setPhoto(it) } }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onClose()
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    val canSave = name.isNotBlank() && plantType.isNotBlank() && !uiState.isLoading

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(scrollState)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(GreenFrame.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(TasksChooseBotton)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = Color.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (canSave) TasksChooseBotton else Color.Gray.copy(alpha = 0.3f))
                        .clickable(
                            enabled = canSave,
                            onClick = { viewModel.save(name, plantType, description, dateIso) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Подтвердить",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacerHeight))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(GreenFrame)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Создание",
                    fontSize = 40.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("имя *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = ContainerColor,
                        focusedContainerColor = ContainerColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF4A6646)
                    )
                )

                OutlinedTextField(
                    value = plantType,
                    onValueChange = { plantType = it },
                    placeholder = { Text("тип растения *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = ContainerColor,
                        focusedContainerColor = ContainerColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF4A6646)
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("описание") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = ContainerColor,
                        focusedContainerColor = ContainerColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF4A6646)
                    )
                )

                OutlinedTextField(
                    value = dateDisplay,
                    onValueChange = {},
                    placeholder = { Text("дата появления") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = ContainerColor,
                        disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                        disabledTextColor = HomeTextDay,
                        disabledPlaceholderColor = Color.Gray.copy(alpha = 0.6f)
                    )
                )

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val localDate = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    dateIso = localDate.format(dateStorageFormatter)
                                    dateDisplay = localDate.format(dateDisplayFormatter)
                                }
                                showDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
                        },
                        colors = DatePickerDefaults.colors(
                            containerColor = GreenFrame,
                            titleContentColor = HomeTextDay,
                            headlineContentColor = HomeTextDay,
                            weekdayContentColor = HomeTextDay.copy(alpha = 0.6f),
                            navigationContentColor = HomeTextDay,
                            yearContentColor = HomeTextDay,
                            currentYearContentColor = HomeTextDow,
                            selectedYearContainerColor = HomeTextDow,
                            selectedYearContentColor = Color.White,
                            dayContentColor = HomeTextDay,
                            selectedDayContainerColor = HomeTextDow,
                            selectedDayContentColor = Color.White,
                            todayContentColor = HomeTextDow,
                            todayDateBorderColor = TasksChooseBotton
                        )
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.selectedPhotoUri != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(140.dp)) {
                            AsyncImage(
                                model = uiState.selectedPhotoUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .clickable { viewModel.clearPhoto() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Удалить фото",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable {
                                photoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        textAlign = TextAlign.Center,
                        text = "Изменить фото",
                        fontSize = 16.sp,
                        color = Color.Black,
                        textDecoration = TextDecoration.Underline
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                photoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        textAlign = TextAlign.Center,
                        text = "Добавить фото",
                        fontSize = 20.sp,
                        color = Color.Black,
                        textDecoration = TextDecoration.Underline
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
