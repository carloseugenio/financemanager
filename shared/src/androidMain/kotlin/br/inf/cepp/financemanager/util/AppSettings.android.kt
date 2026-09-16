package br.inf.cepp.financemanager.util

import android.content.Context
import br.inf.cepp.financemanager.AndroidContext
import androidx.core.content.edit

actual object AppSettingsStorage {
    private const val PREFS_NAME = "finance_manager_prefs"
    private const val KEY_DATE_ENTRY_MODE = "date_entry_mode"
    
    private const val KEY_APP_THEME = "app_theme"

    private fun prefs() = AndroidContext.appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual fun loadDateEntryMode(): DateEntryMode {
        val value = prefs().getString(KEY_DATE_ENTRY_MODE, DateEntryMode.PICKER.name)
        return when (value) {
            DateEntryMode.FREE_HAND.name -> DateEntryMode.FREE_HAND
            else -> DateEntryMode.PICKER
        }
    }

    actual fun saveDateEntryMode(mode: DateEntryMode) {
        prefs().edit { putString(KEY_DATE_ENTRY_MODE, mode.name) }
    }

    actual fun loadTheme(): AppTheme {
        val value = prefs().getString(KEY_APP_THEME, AppTheme.SYSTEM_DEFAULT.name)
        return when (value) {
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.SYSTEM_DEFAULT
        }
    }

    actual fun saveTheme(theme: AppTheme) {
        prefs().edit { putString(KEY_APP_THEME, theme.name) }
    }
}
