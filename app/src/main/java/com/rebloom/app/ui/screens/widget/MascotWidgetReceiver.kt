package com.rebloom.app.ui.screens.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.rebloom.app.MainActivity
import com.rebloom.app.R

class MascotWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
            val completedSet = prefs.getStringSet("completed_ids", emptySet()) ?: emptySet()
            val count = completedSet.size
            val totalTasks = prefs.getInt("total_tasks", 0)
            val views = RemoteViews(context.packageName, R.layout.default_widget_model)
            val bgRes = if (count == 0) {
                R.drawable.dont_ok_widget
            } else if (count < totalTasks) {
                R.drawable.norm_widget
            } else {
                R.drawable.ok_widget
            }
            views.setImageViewResource(R.id.widget_background, bgRes)
            views.setTextViewText(R.id.widget_text, context.getString(R.string.widget, count, totalTasks))
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_background, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}