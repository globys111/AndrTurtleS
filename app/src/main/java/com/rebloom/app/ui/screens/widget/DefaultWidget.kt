/*package com.rebloom.app.ui.screens.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Column
import androidx.glance.layout.Modifier
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.unit.dp
import com.rebloom.app.R

class DefaultWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId){provideContent{Column {Image(provider = ImageProvider(R.drawable.default_widget), contentDescription = "Маскот", modifier = Modifier.fillMaxWidth().padding(8.dp))}}}}

class MascotWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DefaultWidget()}
 */

package com.rebloom.app.ui.screens.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Column
import com.rebloom.app.R


class Widget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column {
                Image(
                    provider = ImageProvider(R.drawable.default_widget),
                    contentDescription = "Маскот"
                )
            }
        }
    }
}

class MascotWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = Widget()
}