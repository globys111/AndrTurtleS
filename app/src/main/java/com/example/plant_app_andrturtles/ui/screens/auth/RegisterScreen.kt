package com.example.plant_app_andrturtles.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_app_andrturtles.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Фоновое изображение (листья) - новое изображение
        Image(
            painter = painterResource(id = R.drawable.bg_leaves_light),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopEnd),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(140.dp))

            // Заголовок
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Регистрация",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                Text(
                    text = "Создайте аккаунт",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Поле Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = { Text("Электронная почта", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = errorMessage != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE2E6D5),
                    focusedContainerColor = Color(0xFFE2E6D5),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedBorderColor = Color(0xFF4A6646)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Поле Пароль
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = { Text("Пароль", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.DarkGray
                        )
                    }
                },
                singleLine = true,
                isError = errorMessage != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE2E6D5),
                    focusedContainerColor = Color(0xFFE2E6D5),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedBorderColor = Color(0xFF4A6646)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Поле Повторить пароль
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                placeholder = { Text("Повторить пароль", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.DarkGray
                        )
                    }
                },
                singleLine = true,
                isError = errorMessage != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE2E6D5),
                    focusedContainerColor = Color(0xFFE2E6D5),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedBorderColor = Color(0xFF4A6646)
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка регистрации
            Button(
                onClick = {
                    when {
                        email.isEmpty() -> errorMessage = "Введите email"
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                            errorMessage = "Введите корректный email"
                        password.isEmpty() -> errorMessage = "Введите пароль"
                        password.length < 6 -> errorMessage = "Пароль должен содержать минимум 6 символов"
                        password != confirmPassword -> errorMessage = "Пароли не совпадают"
                        else -> {
                            isLoading = true
                            Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6646)),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Зарегистрироваться", fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ссылка на вход
            Text(
                text = buildAnnotatedString {
                    append("Есть аккаунт? ")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                        append("Войти")
                    }
                },
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )

            // Разделитель
            Box(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Divider(color = Color.LightGray, thickness = 1.dp)
                Text(
                    text = " или\nзарегистрироваться с помощью ",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.White)
                        .padding(horizontal = 8.dp)
                )
            }

            // Кнопки соцсетей
            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SocialIcon(iconRes = R.drawable.ic_vk, context = context)
                SocialIcon(iconRes = R.drawable.ic_google, context = context)
                SocialIcon(iconRes = R.drawable.ic_more, context = context)
            }
        }
    }
}

@Composable
fun SocialIcon(iconRes: Int, context: android.content.Context) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFE2E6D5),
        modifier = Modifier
            .size(56.dp)
            .clickable {
                Toast.makeText(context, "Вход через соцсеть", Toast.LENGTH_SHORT).show()
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }
    }
}