package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.model.ExpenseSource
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.Income
import br.inf.cepp.financemanager.model.IncomeCategory
import br.inf.cepp.financemanager.model.RecurrenceRule
import br.inf.cepp.financemanager.repository.IFinanceService
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AddIncomeViewModel(private val financeService: IFinanceService) : ViewModel() {
    fun saveIncome(
        description: String,
        amount: Double,
        category: IncomeCategory,
        date: LocalDate,
        source: ExpenseSource,
        status: ExpenseStatus,
        recurrence: RecurrenceRule?,
        existingIncome: Income? = null
    ) {
        viewModelScope.launch {
            financeService.saveIncome(
                (existingIncome ?: Income(
                    description = description,
                    category = category,
                    date = date,
                    amount = amount,
                    source = source,
                    status = status,
                    recurrence = recurrence
                )).copy(
                    description = description,
                    amount = amount,
                    category = category,
                    date = date,
                    source = source,
                    status = status,
                    recurrence = recurrence
                )
            )
        }
    }
}
