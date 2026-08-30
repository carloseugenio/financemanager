package br.inf.cepp.financemanager.util

import java.util.prefs.Preferences

actual object AppSettingsStorage {
    private const val KEY_DATE_ENTRY_MODE = "date_entry_mode"
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
}
