package com.example.plant_app_andrturtles.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.plant_app_andrturtles.R
import com.example.plant_app_andrturtles.domain.model.Plant

@Composable
fun PlantDetailsScreen(
    plant: Plant?,
    onBack: () -> Unit
) {
    val hpad = dimensionResource(R.dimen.screen_hpad)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg))
            .padding(horizontal = hpad)
            .padding(top = 12.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }

        Text(
            text = stringResource(R.string.plant_details_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        if (plant == null) {
            Text(text = stringResource(R.string.error_title))
            return
        }

        Text(text = plant.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(text = plant.type, color = colorResource(R.color.text_secondary))
        Spacer(Modifier.height(10.dp))
        Text(text = plant.description)
    }
}
