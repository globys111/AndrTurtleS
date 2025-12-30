package com.example.plant_app_andrturtles.ui.screens.articles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_app_andrturtles.R
import com.example.plant_app_andrturtles.domain.model.Article

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsScreen(
    article: Article?,
    onBack: () -> Unit
) {
    if (article == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Ошибка: статья не найдена")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text(article.title) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        )

        val imageRes = getDrawableResourceId(article.imageName)
        if (imageRes != 0) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(Modifier.padding(16.dp)) {
            Text(
                "Автор: ${article.author}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Рейтинг: ${article.rating}", fontSize = 16.sp, color = Color.Gray)
                Spacer(Modifier.width(4.dp))
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < article.rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                article.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

private fun getDrawableResourceId(imageName: String): Int {
    return try {
        val nameWithoutExtension = imageName.substringBeforeLast(".")
        R.drawable::class.java.getDeclaredField(nameWithoutExtension).getInt(null)
    } catch (e: Exception) {
        0
    }
}