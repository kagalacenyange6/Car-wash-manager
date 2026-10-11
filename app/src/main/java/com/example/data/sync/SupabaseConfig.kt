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

        // Default project endpoint provided for Supabase integration
        const val DEFAULT_URL = "https://hzeneirrrbjvildraoik.supabase.co"
        const val DEFAULT_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh6ZW5laXJycmJqdmlsZHJhb2lrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTA5ODY0NjYsImV4cCI6MjEwNjU2MjQ2Nn0.h9k_Rz8U8fDzZL5GDj2TYONEqpuTmIMkpzYa5kq5Yco"
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
