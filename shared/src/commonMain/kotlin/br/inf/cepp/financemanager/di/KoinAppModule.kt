package br.inf.cepp.financemanager.di

import br.inf.cepp.financemanager.ui.components.CategoriesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Singletons (Repositories, Databases)
    // single { ExpenseRepository() }

    // ViewModels (automatically lifecycle-managed)
//    viewModelOf(::CategoriesViewModel)
//    viewModelOf(::FinanceManagerViewModel)

}
