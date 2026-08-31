package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseSource
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.IncomingRecord
import br.inf.cepp.financemanager.model.Income
import br.inf.cepp.financemanager.model.IncomeCategory
import br.inf.cepp.financemanager.model.OutgoingRecord
import br.inf.cepp.financemanager.model.RecordType
import br.inf.cepp.financemanager.model.toIncome
import br.inf.cepp.financemanager.model.toIncomingRecord
import br.inf.cepp.financemanager.repository.allCategories
import br.inf.cepp.financemanager.repository.allIncomeCategories
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecordModelTest {
    @Test
    fun incomingRecordsCanBeRepresentedAsFinancialRecords() {
        val income = IncomingRecord(
            description = "Salary",
            category = IncomeCategory.SALARY,
            date = LocalDate(2026, Month.AUGUST, 31),
            amount = 2500.0,
            source = ExpenseSource.MANUAL
        )

        assertEquals(2500.0, income.amount)
        assertEquals(ExpenseSource.MANUAL, income.source)
        assertEquals("Salary", income.description)
        assertEquals(RecordType.INCOME, income.type)
    }

    @Test
    fun outgoingRecordsWrapExistingExpenseEntities() {
        val category = ExpenseCategory("Food", HexColor("#123456"), "cart")
        val expense = Expense(
            id = 1,
            description = "Groceries",
            category = category,
            date = LocalDate(2026, Month.AUGUST, 31),
            amount = 87.5,
            status = ExpenseStatus.CONFIRMED,
            source = ExpenseSource.MANUAL
        )

        val record = OutgoingRecord(expense)

        assertEquals(RecordType.EXPENSE, record.type)
        assertEquals(87.5, record.amount)
        assertEquals("Groceries", record.description)
    }

    @Test
    fun incomeEntitiesRoundTripToIncomingRecords() {
        val income = Income(
            id = 7,
            description = "Salary",
            category = IncomeCategory.SALARY,
            date = LocalDate(2026, Month.AUGUST, 31),
            amount = 2500.0,
            source = ExpenseSource.IMPORT
        )

        val record = income.toIncomingRecord()
        val entity = record.toIncome(id = income.id)

        assertEquals("Salary", record.description)
        assertEquals(IncomeCategory.SALARY, record.category)
        assertEquals(ExpenseSource.IMPORT, record.source)
        assertEquals(income.id, entity.id)
        assertEquals(income.description, entity.description)
        assertEquals(income.category, entity.category)
        assertEquals(income.amount, entity.amount)
        assertEquals(income.source, entity.source)
    }

    @Test
    fun rivalFeatureCategoryPresetsAreSeeded() {
        assertTrue(allIncomeCategories.contains(IncomeCategory.AWARDS))
        assertTrue(allIncomeCategories.contains(IncomeCategory.SALARY))
        assertTrue(allCategories.any { it.name == "Transportation" })
        assertTrue(allCategories.any { it.name == "Bills" })
    }
}
