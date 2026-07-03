// app/src/main/java/com/rebloom/app/ui/screens/profile/EditProfileScreen.kt
package com.rebloom.app.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.rebloom.app.R
import com.rebloom.app.ui.theme.HomeFramePicture
import com.rebloom.app.ui.theme.ProfileExit

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var username by remember(state.username) { mutableStateOf(state.username) }
    var bio by remember(state.bio) { mutableStateOf(state.bio) }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Лончер для выбора фото из галереи
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadAndSaveAvatar(it) }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Image(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Назад",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Редактировать профиль",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HomeFramePicture)
                }
            }

            // Аватарка
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(2.dp, HomeFramePicture, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.avatarUrl != null) {
                        AsyncImage(
                            model = state.avatarUrl,
                            contentDescription = "Аватарка",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.ic_profile_pic_profile_screen),
                            contentDescription = "Аватарка",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // Полупрозрачный оверлей с подсказкой
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x44000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✎", color = Color.White, fontSize = 22.sp)
                    }
                }
            }

            // === Секция 1: Имя и bio ===
            SectionCard(title = "Основная информация") {
                ProfileTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Имя пользователя"
                )
                ProfileTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "О себе",
                    singleLine = false,
                    minLines = 3
                )
                Button(
                    onClick = { viewModel.saveProfile(username, bio) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HomeFramePicture)
                ) {
                    Text("Сохранить", color = Color(0xFF242823))
                }
            }

            // === Секция 2: Смена email ===
            SectionCard(title = "Изменить email") {
                Text(
                    text = "Текущий: ${state.email}",
                    fontSize = 13.sp,
                    color = Color(0x99242823)
                )
                ProfileTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = "Новый email",
                    keyboardType = KeyboardType.Email
                )
                Button(
                    onClick = { if (newEmail.isNotBlank()) viewModel.updateEmail(newEmail) },
                    enabled = !state.isLoading && newEmail.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HomeFramePicture)
                ) {
                    Text("Изменить email", color = Color(0xFF242823))
                }
            }

            // === Секция 3: Смена пароля ===
            SectionCard(title = "Изменить пароль") {
                ProfileTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Новый пароль",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Скрыть" else "Показать", fontSize = 12.sp)
                        }
                    }
                )
                ProfileTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Повторите пароль",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(
                    onClick = { viewModel.updatePassword(newPassword, confirmPassword) },
                    enabled = !state.isLoading && newPassword.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileExit)
                ) {
                    Text("Изменить пароль", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color(0x99242823),
            modifier = Modifier.padding(start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = HomeFramePicture)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF242823),
            unfocusedBorderColor = Color(0x33242823),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}