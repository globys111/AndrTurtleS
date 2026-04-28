package com.rebloom.app

import android.app.Application
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
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
        }
    }
}
