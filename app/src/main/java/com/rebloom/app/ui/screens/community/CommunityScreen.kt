package com.rebloom.app.ui.screens.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebloom.app.R
import com.rebloom.app.domain.model.Post
import com.rebloom.app.ui.common.UiState

@Composable
fun CommunityScreen(
    vm: CommunityViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EED9))
            .padding(16.dp)
    ) {
        Text(
            "Сообщество",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (state) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
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
                val posts = (state as UiState.Success<List<Post>>).data
                if (posts.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(stringResource(R.string.no_posts))
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(posts) { post ->
                            PostItem(post = post, depth = 0)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post, depth: Int) {
    Column(
        modifier = Modifier
            .padding(start = (depth * 16).dp)
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .background(Color(0xFFDDE5C2))
                    .padding(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                val avatarRes = getDrawableResourceId(post.avatar ?: "ic_profile_main_screen")
                if (avatarRes != 0) {
                    Image(
                        painter = painterResource(avatarRes),
                        contentDescription = "Аватар",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(post.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(post.text, fontSize = 14.sp)
                    post.imageName?.let { image ->
                        val imageRes = getDrawableResourceId(image)
                        if (imageRes != 0) {
                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = "Изображение поста",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
        post.replies.forEach { reply ->
            PostItem(post = reply, depth = depth + 1)
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
