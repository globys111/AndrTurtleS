package com.rebloom.app.ui.screens.articles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.Article
import com.rebloom.app.ui.common.UiState

@Composable
fun ArticlesScreen(
    onArticleClick: (Article) -> Unit,
    vm: ArticlesViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(stringResource(R.string.search_articles)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.block_gap)))
                        Text(text = stringResource(R.string.loading_articles))
                    }
                }
            }
            is UiState.Error -> {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(stringResource(R.string.error_loading))
                    Button(onClick = { vm.load() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
            is UiState.Success -> {
                val articles = (state as UiState.Success<List<Article>>).data
                if (articles.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(stringResource(R.string.no_articles))
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(articles) { article ->
                            ArticleCard(article, onClick = { onArticleClick(article) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: Article, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(8.dp)) {
            val imageRes = getDrawableResourceId(article.imageName)
            if (imageRes != 0) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = article.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(article.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Автор: ${article.author}", fontSize = 14.sp, color = Color.Gray)
                Text("Рейтинг: ${article.rating}", fontSize = 14.sp, color = Color.Gray)
            }
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
