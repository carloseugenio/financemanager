package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.domain.AccountPositionSummary
import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.model.AccountType
import br.inf.cepp.financemanager.model.FinanceInstitution
import br.inf.cepp.financemanager.ui.util.toComposeColor
import br.inf.cepp.financemanager.ui.util.HexColor
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(onBack: () -> Unit) {
    val viewModel: AccountsViewModel = koinViewModel()
    val accounts by viewModel.accounts.collectAsState()
    val accountSummary by viewModel.accountSummary.collectAsState()
    val creditSummary by viewModel.creditSummary.collectAsState()
    val utils = LocalPlatformUtils.current
    var selectedAccount by remember { mutableStateOf<AccountPositionSummary?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var editingAccount by remember { mutableStateOf<Account?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Accounts", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
                        Text("Balances, usage, and account detail", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { editingAccount = null; showEditor = true }) {
                            Text("Add account")
                        }
                        TextButton(onClick = onBack) {
                            Text("Back")
                        }
                    }
                }
            }

            if (accountSummary != null) {
                item {
                    AccountSummaryCard(
                        totalBalance = formatMoney(utils.getCurrentCurrencySymbol(), accountSummary!!.totalBalance),
                        totalLimit = formatMoney(utils.getCurrentCurrencySymbol(), accountSummary!!.totalLimit),
                        accountCount = accountSummary!!.accountCount,
                        utilizationPercent = accountSummary!!.utilizationPercent
                    )
                }
            }

            if (creditSummary != null && creditSummary!!.cardCount > 0) {
                item {
                    CreditUtilizationCard(
                        summary = creditSummary!!,
                        currencySymbol = utils.getCurrentCurrencySymbol()
                    )
                }
            }

            item {
                AllocationCard(accounts = accounts, currencySymbol = utils.getCurrentCurrencySymbol())
            }

            if (accounts.isEmpty()) {
                item {
                    EmptyAccountsCard(onAddAccount = { editingAccount = null; showEditor = true })
                }
            } else {
                items(accounts, key = { it.account.name }) { snapshot ->
                    AccountRowCard(
                        snapshot = snapshot,
                        currencySymbol = utils.getCurrentCurrencySymbol(),
                        onClick = { selectedAccount = snapshot }
                    )
                }
            }
        }
    }

    if (selectedAccount != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { selectedAccount = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AccountDetailSheet(
                snapshot = selectedAccount!!,
                currencySymbol = utils.getCurrentCurrencySymbol(),
                onEdit = {
                    editingAccount = selectedAccount!!.account
                    selectedAccount = null
                    showEditor = true
                },
                onClose = { selectedAccount = null }
            )
        }
    }

    if (showEditor) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = {
                showEditor = false
                editingAccount = null
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AccountEditorSheet(
                existingAccount = editingAccount,
                onSave = {
                    viewModel.saveAccount(it)
                    showEditor = false
                    editingAccount = null
                },
                onDismiss = {
                    showEditor = false
                    editingAccount = null
                }
            )
        }
    }
}

@Composable
private fun AccountSummaryCard(
    totalBalance: String,
    totalLimit: String,
    accountCount: Int,
    utilizationPercent: Double,
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Portfolio summary", color = colors.onPrimaryContainer.copy(alpha = 0.82f), fontSize = 12.sp)
            Text(totalBalance, color = colors.onPrimaryContainer, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("Across $accountCount accounts", color = colors.onPrimaryContainer.copy(alpha = 0.82f), fontSize = 12.sp)
            LinearProgressIndicator(
                progress = { (utilizationPercent.coerceIn(0.0, 100.0) / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = colors.secondary,
                trackColor = colors.surfaceVariant
            )
            Text("Net balance vs total limit $totalLimit", color = colors.onPrimaryContainer.copy(alpha = 0.82f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun CreditUtilizationCard(
    summary: CreditSummaryUiState,
    currencySymbol: String,
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Credit utilization", fontWeight = FontWeight.SemiBold, color = colors.onSurface, fontSize = 16.sp)
            Text(
                text = "${summary.cardCount} credit card(s)",
                color = colors.onSurfaceVariant,
                fontSize = 12.sp
            )
            LinearProgressIndicator(
                progress = { (summary.utilizationPercent.coerceIn(0.0, 100.0) / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = if (summary.utilizationPercent <= 30.0) colors.secondary else colors.error,
                trackColor = colors.surfaceVariant
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Used", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    Text(formatMoney(currencySymbol, summary.utilizedAmount), color = colors.onSurface, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Limit", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    Text(formatMoney(currencySymbol, summary.totalLimit), color = colors.onSurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AllocationCard(accounts: List<AccountPositionSummary>, currencySymbol: String) {
    val colors = MaterialTheme.colorScheme
    val totalAbsolute = accounts.sumOf { kotlin.math.abs(it.balance.minorUnits) }.toDouble()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Balance allocation", fontWeight = FontWeight.SemiBold, color = colors.onSurface, fontSize = 16.sp)
            if (accounts.isEmpty() || totalAbsolute == 0.0) {
                Text("No balances to allocate yet.", color = colors.onSurfaceVariant, fontSize = 12.sp)
            } else {
                accounts.forEach { snapshot ->
                    val share = kotlin.math.abs(snapshot.balance.minorUnits).toDouble() / totalAbsolute
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(snapshot.account.name, color = colors.onSurface, fontSize = 12.sp)
                            Text("${(share * 100).toInt()}%", color = colors.onSurfaceVariant, fontSize = 12.sp)
                        }
                        LinearProgressIndicator(
                            progress = { share.toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = snapshot.account.color.toComposeColor(),
                            trackColor = colors.surfaceVariant
                        )
                        Text(
                            text = formatMoney(currencySymbol, snapshot.balance.toMajorUnits()),
                            color = colors.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountRowCard(
    snapshot: AccountPositionSummary,
    currencySymbol: String,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(snapshot.account.name, fontWeight = FontWeight.SemiBold, color = colors.onSurface, fontSize = 16.sp)
                    Text("${snapshot.account.type.name.lowercase().replaceFirstChar { it.uppercase() }} • ${snapshot.account.financeInstitution.name}", color = colors.onSurfaceVariant, fontSize = 12.sp)
                }
                AccountTypeBadge(snapshot.account.type)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Balance", color = colors.onSurfaceVariant, fontSize = 11.sp)
                    Text(formatMoney(currencySymbol, snapshot.balance.toMajorUnits()), color = colors.onSurface, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Available", color = colors.onSurfaceVariant, fontSize = 11.sp)
                    Text(formatMoney(currencySymbol, snapshot.availableAmount.toMajorUnits()), color = colors.onSurface, fontWeight = FontWeight.Bold)
                }
            }
            LinearProgressIndicator(
                progress = { (snapshot.utilizationPercent.coerceIn(0.0, 100.0) / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = if (snapshot.account.type == AccountType.CREDIT) colors.secondary else colors.primary,
                trackColor = colors.surfaceVariant
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Utilization ${snapshot.utilizationPercent.toInt()}%", color = colors.onSurfaceVariant, fontSize = 11.sp)
                Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = colors.secondary)) {
                    Text("Details")
                }
            }
        }
    }
}

@Composable
private fun AccountDetailSheet(
    snapshot: AccountPositionSummary,
    currencySymbol: String,
    onEdit: () -> Unit,
    onClose: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(snapshot.account.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colors.onSurface)
                Text(snapshot.account.financeInstitution.name, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
            AccountTypeBadge(snapshot.account.type)
        }

        DetailLine("Balance", formatMoney(currencySymbol, snapshot.balance.toMajorUnits()))
        DetailLine("Limit", formatMoney(currencySymbol, snapshot.limit.toMajorUnits()))
        DetailLine("Available", formatMoney(currencySymbol, snapshot.availableAmount.toMajorUnits()))
        DetailLine("Utilization", "${snapshot.utilizationPercent.toInt()}%")
        DetailLine("Branch", snapshot.account.branch)
        DetailLine("Account number", maskAccountNumber(snapshot.account.number))
        DetailLine("Network", snapshot.account.network.ifBlank { "Not set" })
        DetailLine("Due day", snapshot.account.monthlyDueDate.toString())

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text("Close")
            }
            Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                Text("Edit account")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = colors.onSurfaceVariant, fontSize = 12.sp)
        Text(value, color = colors.onSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AccountTypeBadge(type: AccountType) {
    val colors = MaterialTheme.colorScheme
    val background = when (type) {
        AccountType.CHECKING -> colors.surfaceVariant
        AccountType.SAVINGS -> colors.secondary
        AccountType.CREDIT -> colors.error
        AccountType.DIGITAL -> colors.primary
    }
    val foreground = when (type) {
        AccountType.CREDIT -> Color.White
        else -> colors.onSurface
    }
    Text(
        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
        color = foreground,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.background(background, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun EmptyAccountsCard(onAddAccount: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("No accounts yet", fontWeight = FontWeight.SemiBold, color = colors.onSurface)
            Text("Add an account to track balances, limits, and utilization.", color = colors.onSurfaceVariant, fontSize = 12.sp)
            Button(onClick = onAddAccount, colors = ButtonDefaults.buttonColors(containerColor = colors.primaryContainer)) {
                Text("Add account")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountEditorSheet(
    existingAccount: Account?,
    onSave: (Account) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val nameFocus = remember { FocusRequester() }
    val branchFocus = remember { FocusRequester() }
    val numberFocus = remember { FocusRequester() }
    val limitFocus = remember { FocusRequester() }
    val balanceFocus = remember { FocusRequester() }
    val dueDayFocus = remember { FocusRequester() }
    val colorFocus = remember { FocusRequester() }
    var name by remember(existingAccount?.name) { mutableStateOf(existingAccount?.name ?: "") }
    var institutionName by remember(existingAccount?.name) { mutableStateOf(existingAccount?.financeInstitution?.name ?: "North Bank") }
    var institutionType by remember(existingAccount?.name) { mutableStateOf(existingAccount?.financeInstitution?.type ?: "Bank") }
    var type by remember(existingAccount?.name) { mutableStateOf(existingAccount?.type ?: AccountType.CHECKING) }
    var branch by remember(existingAccount?.name) { mutableStateOf(existingAccount?.branch ?: "") }
    var number by remember(existingAccount?.name) { mutableStateOf(existingAccount?.number ?: "") }
    var network by remember(existingAccount?.name) { mutableStateOf(existingAccount?.network ?: "") }
    var limit by remember(existingAccount?.name) { mutableStateOf(existingAccount?.limit?.toString() ?: "") }
    var balance by remember(existingAccount?.name) { mutableStateOf(existingAccount?.balance?.toString() ?: "") }
    var monthlyDueDay by remember(existingAccount?.name) { mutableStateOf(existingAccount?.monthlyDueDate?.toString() ?: "1") }
    var colorHex by remember(existingAccount?.name) { mutableStateOf(existingAccount?.color?.hex ?: "#1F5A82") }
    var iconKey by remember(existingAccount?.name) { mutableStateOf(existingAccount?.iconKey ?: "wallet") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(if (existingAccount == null) "Add account" else "Edit account", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colors.onSurface)
        Text("Track balances, limits, and utilization for each account.", color = colors.onSurfaceVariant, fontSize = 12.sp)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Account name") },
            modifier = Modifier.fillMaxWidth().focusRequester(nameFocus),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { branchFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = type.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type") },
            trailingIcon = {
                TextButton(onClick = {
                    val values = AccountType.values()
                    val index = values.indexOf(type)
                    type = values[(index + 1) % values.size]
                }) { Text("Next") }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = institutionName,
            onValueChange = { institutionName = it },
            label = { Text("Institution name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = institutionType,
            onValueChange = { institutionType = it },
            label = { Text("Institution type") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { branchFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = branch,
            onValueChange = { branch = it },
            label = { Text("Branch") },
            modifier = Modifier.fillMaxWidth().focusRequester(branchFocus),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { numberFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Account number") },
            modifier = Modifier.fillMaxWidth().focusRequester(numberFocus),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { limitFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = network,
            onValueChange = { network = it },
            label = { Text("Network") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { limitFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = limit,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) limit = it },
            label = { Text("Limit") },
            modifier = Modifier.fillMaxWidth().focusRequester(limitFocus),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { balanceFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = balance,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) balance = it },
            label = { Text("Balance") },
            modifier = Modifier.fillMaxWidth().focusRequester(balanceFocus),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { dueDayFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = monthlyDueDay,
            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) monthlyDueDay = it },
            label = { Text("Due day") },
            modifier = Modifier.fillMaxWidth().focusRequester(dueDayFocus),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { colorFocus.requestFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = colorHex,
            onValueChange = { colorHex = it },
            label = { Text("Color hex") },
            modifier = Modifier.fillMaxWidth().focusRequester(colorFocus),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onNext = { focusManager.clearFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        OutlinedTextField(
            value = iconKey,
            onValueChange = { iconKey = it },
            label = { Text("Icon key") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focusManager.clearFocus() }),
            colors = br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val account = Account(
                        name = name.trim(),
                        type = type,
                        financeInstitution = FinanceInstitution(institutionName.trim(), institutionType.trim()),
                        branch = branch.trim(),
                        number = number.trim(),
                        network = network.trim(),
                        limit = limit.toDoubleOrNull() ?: 0.0,
                        monthlyDueDate = monthlyDueDay.toIntOrNull()?.coerceIn(1, 31) ?: 1,
                        balance = balance.toDoubleOrNull() ?: 0.0,
                        color = HexColor(colorHex.trim().ifBlank { "#1F5A82" }),
                        iconKey = iconKey.trim().ifBlank { "wallet" }
                    )
                    if (account.name.isNotBlank() && account.financeInstitution.name.isNotBlank()) {
                        onSave(account)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryContainer, contentColor = colors.onPrimaryContainer)
            ) {
                Text("Save account")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun formatMoney(symbol: String, amount: Double): String {
    return "$symbol ${"%.2f".format(amount)}"
}

private fun maskAccountNumber(value: String): String {
    val digits = value.filter { it.isDigit() }
    if (digits.length <= 4) return value
    return "****${digits.takeLast(4)}"
}
