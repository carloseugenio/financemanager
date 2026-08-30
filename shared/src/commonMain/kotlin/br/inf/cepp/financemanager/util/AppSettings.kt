package br.inf.cepp.financemanager.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DateEntryMode {
    FREE_HAND,
    PICKER
}

expect object AppSettingsStorage {
    fun loadDateEntryMode(): DateEntryMode
    fun saveDateEntryMode(mode: DateEntryMode)
}

object AppSettings {
    private val _dateEntryMode = MutableStateFlow(AppSettingsStorage.loadDateEntryMode())
    val dateEntryMode: StateFlow<DateEntryMode> = _dateEntryMode.asStateFlow()

    fun setDateEntryMode(mode: DateEntryMode) {
        AppSettingsStorage.saveDateEntryMode(mode)
        _dateEntryMode.value = mode
    }
}
