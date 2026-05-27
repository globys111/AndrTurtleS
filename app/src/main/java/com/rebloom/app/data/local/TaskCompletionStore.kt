package com.rebloom.app.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TaskCompletionStore {

    private lateinit var prefs: SharedPreferences
    private val _completedIds = MutableStateFlow<Set<String>>(emptySet())
    val completedIds: StateFlow<Set<String>> = _completedIds.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        _completedIds.value = prefs.getStringSet("completed_ids", emptySet())?.toSet() ?: emptySet()
    }

    fun toggle(key: String) {
        val current = _completedIds.value.toMutableSet()
        if (current.contains(key)) current.remove(key) else current.add(key)
        _completedIds.value = current
        prefs.edit().putStringSet("completed_ids", current).commit()
    }

    fun saveTodayCounts(completed: Int, total: Int) {
        prefs.edit()
            .putInt("today_completed", completed)
            .putInt("today_total", total)
            .apply()
    }
}
