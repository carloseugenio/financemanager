package br.inf.cepp.financemanager.repository

import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.model.*
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

class DatabaseInitializer(private val database: AppDatabase) {

    suspend fun initializeIfNeeded() {
        val categoryDao = database.expenseCategoryDao()
        val existingCategories = categoryDao.getAll().first()

        if (existingCategories.isEmpty()) {
            // Seed Categories
            allCategories.forEach { category ->
                categoryDao.insert(category)
            }

            // Seed Institutions
            val institutionDao = database.institutionDao()
            financeInstitutions.forEach { institution ->
                institutionDao.insert(institution)
            }
        }
        
        val expenseDao = database.expenseDao()
        val existingExpenses = expenseDao.getAll().first()
        if (existingExpenses.isEmpty()) {
            allExpenses.forEach { expense ->
                expenseDao.insert(expense)
            }
        }
        
        val accountDao = database.accountDao()
        val existingAccounts = accountDao.getAll().first()
        if (existingAccounts.isEmpty()) {
            allAccounts.forEach { account ->
                accountDao.insert(account)
            }
        }
        
        val itemDao = database.expenseItemDao()
        val existingItems = itemDao.getAll().first()
        if (existingItems.isEmpty()) {
            plannedExpenses.forEach { item ->
                itemDao.insert(item)
            }
            recentExpenses.forEach { item ->
                itemDao.insert(item)
            }
        }

        val projectDao = database.projectDao()
        val existingPlans = projectDao.getAllPlans().first()
        if (existingPlans.isEmpty()) {
            projectDao.insertPlan(
                ProjectPlan(
                    name = "Home renovation",
                    startDate = LocalDate(2026, 8, 1),
                    endDate = LocalDate(2026, 9, 30),
                    budget = 15000.0,
                    status = ProjectStatus.ACTIVE
                )
            )
        }
    }
}
