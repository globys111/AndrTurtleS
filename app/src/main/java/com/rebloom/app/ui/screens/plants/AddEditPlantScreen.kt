package com.rebloom.app.ui.screens.plants

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.rebloom.app.R
import com.rebloom.app.domain.model.Plant
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
fun AddEditPlantScreen(
    plant: Plant? = null,
    onClose: () -> Unit,
    viewModel: AddEditPlantViewModel = viewModel()
) {
    val isEditMode = plant != null
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    val dateDisplayFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val dateStorageFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }

    LaunchedEffect(plant) {
        if (plant != null) viewModel.initFromPlant(plant)
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onClose()
    }

    BackHandler {
        if (isEditMode) showCancelDialog = true
        // create mode: physical back blocked, use the X button
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.setPhoto(it) } }

    val canSave = uiState.name.isNotBlank() && uiState.plantType.isNotBlank() && !uiState.isLoading

    val displayPhoto: Any? = uiState.newPhotoUri ?: uiState.existingImageUrl

    Box(modifier = Modifier.fillMaxSize().background(GreenFrame)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Photo area at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (displayPhoto != null) {
                    AsyncImage(
                        model = displayPhoto,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_plants_selected),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = colorResource(R.color.text_primary)
                    )
                }
            }

            // Form panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(GreenFrame)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = if (isEditMode) "Редактирование" else "Создание",
                    fontSize = 40.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.updateName(it) },
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
                    value = uiState.plantType,
                    onValueChange = { viewModel.updatePlantType(it) },
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
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
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
                    value = uiState.dateDisplay,
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    textAlign = TextAlign.Center,
                    text = "Редактировать фото",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Header overlay (X and ✓ buttons)
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
                        .clickable {
                            if (isEditMode) showCancelDialog = true else onClose()
                        },
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
                        .clickable(enabled = canSave) {
                            if (isEditMode) showSaveDialog = true else viewModel.create()
                        },
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
    }

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
                        viewModel.updateDate(
                            iso  = localDate.format(dateStorageFormatter),
                            disp = localDate.format(dateDisplayFormatter)
                        )
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            },
            colors = DatePickerDefaults.colors(
                containerColor           = GreenFrame,
                titleContentColor        = HomeTextDay,
                headlineContentColor     = HomeTextDay,
                weekdayContentColor      = HomeTextDay.copy(alpha = 0.6f),
                navigationContentColor   = HomeTextDay,
                yearContentColor         = HomeTextDay,
                currentYearContentColor  = HomeTextDow,
                selectedYearContainerColor = HomeTextDow,
                selectedYearContentColor = Color.White,
                dayContentColor          = HomeTextDay,
                selectedDayContainerColor = HomeTextDow,
                selectedDayContentColor  = Color.White,
                todayContentColor        = HomeTextDow,
                todayDateBorderColor     = TasksChooseBotton
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Cancel dialog (edit mode only)
    if (showCancelDialog) {
        Dialog(onDismissRequest = { showCancelDialog = false }) {
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
                        text = "Вы хотите отменить изменения и выйти?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = { showCancelDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.dark_green)
                        )
                    ) { Text("Нет, остаться", color = Color.White) }
                    Button(
                        onClick = { showCancelDialog = false; onClose() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_gray_transparent),
                            contentColor = colorResource(R.color.text_primary)
                        )
                    ) { Text("Отменить и выйти") }
                }
            }
        }
    }

    // Save dialog (edit mode only)
    if (showSaveDialog) {
        Dialog(onDismissRequest = { showSaveDialog = false }) {
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
                        text = "Сохранить изменения и выйти?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = {
                            showSaveDialog = false
                            plant?.let { viewModel.update(it.id) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.dark_green)
                        )
                    ) { Text("Да", color = Color.White) }
                    Button(
                        onClick = { showSaveDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_gray_transparent),
                            contentColor = colorResource(R.color.text_primary)
                        )
                    ) { Text("Нет, остаться") }
                }
            }
        }
    }
}
