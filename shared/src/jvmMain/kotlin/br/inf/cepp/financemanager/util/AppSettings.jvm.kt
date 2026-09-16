package br.inf.cepp.financemanager.util

import java.util.prefs.Preferences

actual object AppSettingsStorage {
    private const val KEY_DATE_ENTRY_MODE = "date_entry_mode"

    private const val APP_THEME = "app_theme"

    private val prefs: Preferences = Preferences.userNodeForPackage(AppSettingsStorage::class.java)

    actual fun loadDateEntryMode(): DateEntryMode {
        val value = prefs.get(KEY_DATE_ENTRY_MODE, DateEntryMode.PICKER.name)
        return when (value) {
            DateEntryMode.FREE_HAND.name -> DateEntryMode.FREE_HAND
            else -> DateEntryMode.PICKER
        }
    }

    actual fun saveDateEntryMode(mode: DateEntryMode) {
        prefs.put(KEY_DATE_ENTRY_MODE, mode.name)
        prefs.flush()
    }

    actual fun saveTheme(theme: AppTheme) {
        prefs.put(APP_THEME, theme.name)
        prefs.flush()
    }

    actual fun loadTheme(): AppTheme {
        val value = prefs.get(APP_THEME, AppTheme.SYSTEM_DEFAULT.name)
        return when (value) {
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.SYSTEM_DEFAULT
        }
    }
}
