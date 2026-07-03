package com.rebloom.app.ui.screens.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import com.rebloom.app.R
import com.rebloom.app.ui.theme.HomeFramePicture
import com.rebloom.app.ui.theme.HomeTextDay
import com.rebloom.app.ui.theme.ProfileExit

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSignedOut) {
        if (state.isSignedOut) onSignedOut()
    }
    val sidePadding = 30.dp
    val blockColor = HomeFramePicture

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(horizontal = sidePadding, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBack() }
                )
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(999.dp))
                ) {
                    if (state.avatarUrl != null) {
                        AsyncImage(
                            model = state.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.ic_profile_pic_profile_screen),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = state.username.ifBlank { "Имя пользователя" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = state.email.ifBlank { "email@example.com" },
                    fontSize = 14.sp,
                    color = Color(0x99242823)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = state.bio.ifBlank { "Обо мне…" },
                    fontSize = 14.sp,
                    color = Color(0x99242823)
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = blockColor)
                ) {
                    Text(
                        text = stringResource(R.string.profile_edit),
                        color = Color(0xFF242823)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileExit)
                ) {
                    Text(text = stringResource(R.string.logout), color = Color.White)
                }

                Button(
                    onClick = { viewModel.deleteAccount() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileExit)
                ) {
                    Text(text = stringResource(R.string.delete_account), color = Color.White)
                }
            }
        }
    }
}

private data class SettingsRow(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val onClick: () -> Unit
)

@Composable
private fun SettingsGroupCard(
    @StringRes titleRes: Int,
    items: List<SettingsRow>,
    blockColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(titleRes),
            fontSize = 14.sp,
            color = Color(0x99242823),
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = blockColor)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items.forEachIndexed { index, row ->
                    SettingsRowItem(
                        iconRes = row.iconRes,
                        title = stringResource(row.titleRes),
                        onClick = row.onClick,
                        containerColor = blockColor
                    )

                    if (index != items.lastIndex) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = Color(0x33242823),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    @DrawableRes iconRes: Int,
    title: String,
    onClick: () -> Unit,
    containerColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = HomeTextDay
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = HomeTextDay
        )
    }
}