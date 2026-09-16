package br.inf.cepp.financemanager.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DateEntryMode {
    FREE_HAND,
    PICKER
}

/**
 * Selects the theme used by the application
 */
enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT
}

expect object AppSettingsStorage {
    fun loadDateEntryMode(): DateEntryMode
    fun saveDateEntryMode(mode: DateEntryMode)
    fun loadTheme(): AppTheme
    fun saveTheme(theme: AppTheme)
}

object AppSettings {

    // Private mutable state flow
    private val _dateEntryMode = MutableStateFlow(AppSettingsStorage.loadDateEntryMode())

    /**
     * Public read-only state flow exposed to UI for the dateEntryMode
     */
    val dateEntryMode: StateFlow<DateEntryMode> = _dateEntryMode.asStateFlow()

    private val _theme = MutableStateFlow(AppSettingsStorage.loadTheme())

    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    fun setDateEntryMode(mode: DateEntryMode) {
        AppSettingsStorage.saveDateEntryMode(mode)
        _dateEntryMode.value = mode
    }

    fun setTheme(theme: AppTheme) {
        AppSettingsStorage.saveTheme(theme)
        _theme.value = theme
    }
}
