package com.rebloom.app

import android.app.Application
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import com.rebloom.app.data.notification.Notification
import com.rebloom.app.data.notification.WateringCheck
import com.rebloom.app.BuildConfig

class RebloomApp : Application() {

    companion object {
        lateinit var supabase: io.github.jan.supabase.SupabaseClient
            private set
    }

    override fun onCreate() {
        super.onCreate()
        supabase = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
        Notification.createChannel(this)
        WateringCheck.schedule(this)
    }
}
