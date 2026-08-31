package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.domain.AccountPositionSummary
import br.inf.cepp.financemanager.domain.CreditCardSummary
import br.inf.cepp.financemanager.domain.FinancialSummaryCalculator
import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.repository.IFinanceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AccountsViewModel(private val financeService: IFinanceService) : ViewModel() {
    private val calculator = FinancialSummaryCalculator()

    private val _accounts = MutableStateFlow<List<AccountPositionSummary>>(emptyList())
    val accounts: StateFlow<List<AccountPositionSummary>> = _accounts.asStateFlow()

    private val _accountSummary = MutableStateFlow<AccountSummaryUiState?>(null)
    val accountSummary: StateFlow<AccountSummaryUiState?> = _accountSummary.asStateFlow()

    private val _creditSummary = MutableStateFlow<CreditSummaryUiState?>(null)
    val creditSummary: StateFlow<CreditSummaryUiState?> = _creditSummary.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            financeService.getAccounts().collect { accounts ->
                updateState(accounts)
            }
        }
    }

    fun saveAccount(account: Account) {
        viewModelScope.launch {
            financeService.saveAccount(account)
        }
    }

    private fun updateState(accounts: List<Account>) {
        val snapshots = accounts
            .map { calculator.summarizeAccount(it, "local") }
            .sortedWith(
                compareByDescending<AccountPositionSummary> { it.account.type == br.inf.cepp.financemanager.model.AccountType.CREDIT }
                    .thenByDescending { it.balance.minorUnits }
                    .thenBy { it.account.name }
            )

        val overall = calculator.summarizeAccounts(accounts, "local")
        val credit = calculator.summarizeCreditCards(accounts, "local")

        _accounts.value = snapshots
        _accountSummary.value = AccountSummaryUiState(
            totalBalance = overall.totalBalance.toMajorUnits(),
            totalLimit = overall.totalLimit.toMajorUnits(),
            accountCount = overall.accountCount,
            utilizationPercent = overall.utilizationPercent,
        )
        _creditSummary.value = CreditSummaryUiState(
            cardCount = credit.cardCount,
            utilizedAmount = credit.utilizedAmount.toMajorUnits(),
            totalLimit = credit.totalLimit.toMajorUnits(),
            utilizationPercent = credit.utilizationPercent,
        )
    }
}

data class AccountSummaryUiState(
    val totalBalance: Double,
    val totalLimit: Double,
    val accountCount: Int,
    val utilizationPercent: Double,
)

data class CreditSummaryUiState(
    val cardCount: Int,
    val utilizedAmount: Double,
    val totalLimit: Double,
    val utilizationPercent: Double,
)
