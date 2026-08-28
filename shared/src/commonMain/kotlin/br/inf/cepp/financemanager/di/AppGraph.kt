package br.inf.cepp.financemanager.di

import br.inf.cepp.financemanager.ui.components.CategoriesViewModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.module

/**
 * Builds the core application graph scanning this package and subpackages
 */
//@KoinApplication
//@ComponentScan("br.inf.cepp.financemanager")
//class AppGraph

// 🚀 Explicitly bind the ViewModel so it is guaranteed to load without compiler magic
val appModule = module {
    factory { CategoriesViewModel() }
}
