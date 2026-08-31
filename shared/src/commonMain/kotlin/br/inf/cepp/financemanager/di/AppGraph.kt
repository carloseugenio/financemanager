package br.inf.cepp.financemanager.di

import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.getDatabaseBuilder
import br.inf.cepp.financemanager.repository.DatabaseInitializer
import br.inf.cepp.financemanager.repository.FinanceService
import br.inf.cepp.financemanager.ui.components.CategoriesViewModel
import org.koin.dsl.module
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

import br.inf.cepp.financemanager.ui.components.FinanceManagerViewModel
import br.inf.cepp.financemanager.ui.screen.AddExpenseViewModel
import br.inf.cepp.financemanager.ui.screen.AddIncomeViewModel
import br.inf.cepp.financemanager.ui.screen.AccountsViewModel
import br.inf.cepp.financemanager.ui.screen.DashboardViewModel
import br.inf.cepp.financemanager.ui.screen.ImportViewModel
import br.inf.cepp.financemanager.ui.screen.ProjectsViewModel
import br.inf.cepp.financemanager.ui.screen.ReconciliationViewModel
import br.inf.cepp.financemanager.ui.screen.ExportViewModel
import br.inf.cepp.financemanager.ui.screen.ExpensesViewModel

// 🚀 Explicitly bind the ViewModel so it is guaranteed to load without compiler magic
val appModule = module {
    single<AppDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single { DatabaseInitializer(get()) }
    single<br.inf.cepp.financemanager.repository.IFinanceService> { FinanceService(get()) }
    factory { CategoriesViewModel(get()) }
    factory { DashboardViewModel(get()) }
    factory { FinanceManagerViewModel(get()) }
    factory { AddExpenseViewModel(get()) }
    factory { AddIncomeViewModel(get()) }
    factory { AccountsViewModel(get()) }
    factory { ImportViewModel(get()) }
    factory { ReconciliationViewModel(get()) }
    factory { ProjectsViewModel(get()) }
    factory { ExportViewModel(get()) }
    factory { ExpensesViewModel(get()) }
}
