
package com.rebloom.app.ui.screens.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.rebloom.app.R
import androidx.glance.GlanceModifier
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize

class Widget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        val completedSet = prefs.getStringSet("completed_ids", emptySet()) ?: emptySet()
        val completedCount = completedSet.size

        provideContent {
            Box(modifier = GlanceModifier.fillMaxSize()) {
                Image(
                    provider = ImageProvider(R.drawable.test_widget_white),
                    contentDescription = "Маскот",
                    modifier = GlanceModifier.fillMaxSize()
                )
                Text(
                    text = "Выполнено: $completedCount",
                    modifier = GlanceModifier.fillMaxWidth(),
                    style = TextStyle(
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

class MascotWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = Widget()
}