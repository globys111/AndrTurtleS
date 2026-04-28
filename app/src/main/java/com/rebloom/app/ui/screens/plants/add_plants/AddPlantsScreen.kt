package com.rebloom.app.ui.screens.plants.add_plants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rebloom.app.ui.theme.GreenFrame
import com.rebloom.app.ui.theme.TasksChooseBotton
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import com.rebloom.app.R
import com.rebloom.app.ui.theme.ContainerColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantsScreen(
    onClose: () -> Unit = {},
    onConfirm: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(GreenFrame.copy(alpha = 0.4f))
        )
        {
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
                        .background(TasksChooseBotton)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Подтвердить",
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(200.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(GreenFrame)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    value = field1,
                    onValueChange = { field1 = it },
                    placeholder = { Text("имя") },
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
                    value = field2,
                    onValueChange = { field2 = it },
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
                    value = field3,
                    onValueChange = { field3 = it },
                    placeholder = { Text("дата появления") },
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

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 2.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TasksChooseBotton
                        )
                    )
                    {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd,
                        )
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.keyboard_arrow_right),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Text(
                        text = "вид",
                        color = Color.Black,
                        modifier = Modifier.padding(end = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {},
                        textAlign = TextAlign.Center,
                        text = "Редактировать фото",
                        fontSize = 20.sp,
                        color = Color.Black,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}