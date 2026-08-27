package br.inf.cepp.financemanager.ui.screen

import kotlinx.serialization.Serializable

// Navigation targets
sealed interface Screen {
    @Serializable data object Dashboard : Screen
    @Serializable data object CategoryManager : Screen

}
