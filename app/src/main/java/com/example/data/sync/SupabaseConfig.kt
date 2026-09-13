package com.example.data.sync

import android.content.Context
import android.content.SharedPreferences

data class SupabaseConfig(
    val url: String,
    val anonKey: String,
    val autoSyncEnabled: Boolean = true
) {
    val isConfigured: Boolean
        get() = url.isNotBlank() && anonKey.isNotBlank() && url.startsWith("https://")
}

class SupabaseConfigManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("supabase_config_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_URL = "supabase_url"
        private const val KEY_ANON_KEY = "supabase_anon_key"
        private const val KEY_AUTO_SYNC = "supabase_auto_sync"

        // Sample / default project endpoint for demonstration if user hasn't set one yet
        const val DEFAULT_URL = "https://carwash-demo.supabase.co"
        const val DEFAULT_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.carwash_pro_anon_key"
    }

    fun getConfig(): SupabaseConfig {
        val url = prefs.getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL
        val anonKey = prefs.getString(KEY_ANON_KEY, DEFAULT_KEY) ?: DEFAULT_KEY
        val autoSync = prefs.getBoolean(KEY_AUTO_SYNC, true)
        return SupabaseConfig(url = url, anonKey = anonKey, autoSyncEnabled = autoSync)
    }

    fun saveConfig(url: String, anonKey: String, autoSync: Boolean = true) {
        prefs.edit()
            .putString(KEY_URL, url.trim().removeSuffix("/"))
            .putString(KEY_ANON_KEY, anonKey.trim())
            .putBoolean(KEY_AUTO_SYNC, autoSync)
            .apply()
    }

    fun resetToDefault() {
        prefs.edit().clear().apply()
    }
}
