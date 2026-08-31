package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors
import br.inf.cepp.financemanager.util.AppSettings
import br.inf.cepp.financemanager.util.DateEntryMode
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import br.inf.cepp.financemanager.util.RecurrenceInputUtils
import br.inf.cepp.financemanager.util.today
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    categories: List<ExpenseCategory>,
    onBackClick: () -> Unit,
    onSaveExpense: (description: String, amount: Double, category: ExpenseCategory, date: LocalDate, status: ExpenseStatus, source: ExpenseSource, recurrence: RecurrenceRule?, reminder: Reminder?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val descriptionFocus = remember { FocusRequester() }
    val amountFocus = remember { FocusRequester() }
    val categoryFocus = remember { FocusRequester() }
    val dateFocus = remember { FocusRequester() }
    val locale = LocalLocale.current
    val dateEntryMode by AppSettings.dateEntryMode.collectAsState()
    val utils = LocalPlatformUtils.current

    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var date by remember { mutableStateOf(today()) }
    var status by remember { mutableStateOf(ExpenseStatus.CONFIRMED) }
    var source by remember { mutableStateOf(ExpenseSource.MANUAL) }
    var freeHandDateText by remember { mutableStateOf(date.toString()) }
    var dayText by remember { mutableStateOf(date.day.toString()) }
    var month by remember { mutableStateOf(date.month) }
    var year by remember { mutableStateOf(date.year.toString()) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var recurrenceEnabled by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var recurrenceFrequency by remember { mutableStateOf(RecurrenceFrequency.MONTHLY) }
    var recurrenceInterval by remember { mutableStateOf("") }
    var recurrenceCount by remember { mutableStateOf("") }
    var recurrenceUntil by remember { mutableStateOf("") }
    var reminderMethod by remember { mutableStateOf(ReminderMethod.NOTIFICATION) }
    var reminderLeadTime by remember { mutableStateOf("60") }
    var reminderDestination by remember { mutableStateOf("") }
    val recurrenceRule = RecurrenceInputUtils.buildRecurrenceRule(
        enabled = recurrenceEnabled,
        frequency = recurrenceFrequency,
        intervalText = recurrenceInterval,
        countText = recurrenceCount,
        untilText = recurrenceUntil,
        language = locale.language
    )
    val reminderRule = RecurrenceInputUtils.buildReminder(
        enabled = reminderEnabled,
        method = reminderMethod,
        leadTimeText = reminderLeadTime,
        destinationText = reminderDestination
    )

    fun updateDateFromParts() {
        val safeDay = dayText.toIntOrNull()?.coerceIn(1, 31) ?: date.day
        val safeYear = year.toIntOrNull() ?: date.year
        date = LocalDate(safeYear, month, safeDay.coerceIn(1, 28))
        freeHandDateText = formatDateForLocale(date, locale.language)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("Add Expense", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { amountFocus.requestFocus() }),
                shape = RoundedCornerShape(12.dp),
                colors = financeOutlinedTextFieldColors()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                label = { Text("Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(amountFocus),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { categoryFocus.requestFocus() }),
                shape = RoundedCornerShape(12.dp),
                prefix = { Text(utils.getCurrentCurrencySymbol() + " ") },
                singleLine = true,
                colors = financeOutlinedTextFieldColors()
            )

            OutlinedTextField(
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    TextButton(onClick = {
                        if (categories.isNotEmpty()) {
                            val currentIndex = categories.indexOf(selectedCategory)
                            val nextIndex = if (currentIndex < 0) 0 else (currentIndex + 1) % categories.size
                            selectedCategory = categories[nextIndex]
                        }
                    }) { Text("Next") }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(categoryFocus),
                    shape = RoundedCornerShape(12.dp),
                    colors = financeOutlinedTextFieldColors()
            )

            when (dateEntryMode) {
                DateEntryMode.FREE_HAND -> {
                    OutlinedTextField(
                        value = freeHandDateText,
                        onValueChange = { raw ->
                            freeHandDateText = maskDateForLocale(raw, locale.language)
                            parseDateForLocale(freeHandDateText, locale.language)?.let {
                                date = it
                                dayText = it.day.toString()
                                month = it.month
                                year = it.year.toString()
                            }
                        },
                        label = { Text("Date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(dateFocus),
                        placeholder = { Text(if (locale.language == "pt") "dd/MM/yyyy" else "yyyy-MM-dd") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(12.dp),
                        colors = financeOutlinedTextFieldColors()
                    )
                }
                DateEntryMode.PICKER -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dayText,
                            onValueChange = { v ->
                                if (v.isEmpty() || v.toIntOrNull() != null) {
                                    dayText = v
                                    v.toIntOrNull()?.takeIf { it in 1..31 }?.let { updateDateFromParts() }
                                }
                            },
                            label = { Text("Day") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                            modifier = Modifier.fillMaxWidth(0.24f),
                            shape = RoundedCornerShape(12.dp),
                            colors = financeOutlinedTextFieldColors()
                        )
                        OutlinedTextField(
                            value = month.name.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Month") },
                            trailingIcon = {
                                TextButton(onClick = {
                                    val values = Month.values()
                                    val idx = values.indexOf(month)
                                    month = values[(idx + 1) % values.size]
                                    updateDateFromParts()
                                }) { Text("Next") }
                            },
                            modifier = Modifier.fillMaxWidth(0.42f),
                            shape = RoundedCornerShape(12.dp),
                            colors = financeOutlinedTextFieldColors()
                        )
                        OutlinedTextField(
                            value = year,
                            onValueChange = { v ->
                                if (v.isEmpty() || v.toIntOrNull() != null) {
                                    year = v
                                    if (v.toIntOrNull() != null && v.length == 4) updateDateFromParts()
                                }
                            },
                            label = { Text("Year") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = financeOutlinedTextFieldColors()
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Recurrence", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Repeat this expense")
                    Switch(
                        checked = recurrenceEnabled,
                        onCheckedChange = {
                            recurrenceEnabled = it
                            if (it && recurrenceInterval.isBlank()) {
                                recurrenceInterval = "1"
                            }
                        }
                    )
                }
                if (recurrenceEnabled) {
                    Text(
                        "Set how often this expense repeats. Interval is required; count and until are optional.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = recurrenceFrequency.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = {
                            TextButton(onClick = {
                                val values = RecurrenceFrequency.values()
                                val idx = values.indexOf(recurrenceFrequency)
                                recurrenceFrequency = values[(idx + 1) % values.size]
                            }) { Text("Next") }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = financeOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = recurrenceInterval,
                        onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) recurrenceInterval = it },
                        label = { Text("Interval (required)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        colors = financeOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = recurrenceCount,
                        onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) recurrenceCount = it },
                        label = { Text("Count") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        colors = financeOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = recurrenceUntil,
                        onValueChange = { recurrenceUntil = maskDateForLocale(it, locale.language) },
                        label = { Text("Until") },
                        placeholder = { Text(if (locale.language == "pt") "dd/MM/yyyy" else "yyyy-MM-dd") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(12.dp),
                        colors = financeOutlinedTextFieldColors()
                    )
                    recurrenceRule?.let {
                        Text(
                            RecurrenceInputUtils.describeRecurrence(it, locale.language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Reminder", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Enable reminder")
                    Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                }
                if (reminderEnabled) {
                    Text(
                        "Choose when and how the reminder should be sent for the recurring expense.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = reminderMethod.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Method") },
                        trailingIcon = {
                            TextButton(onClick = {
                                val values = ReminderMethod.values()
                                val idx = values.indexOf(reminderMethod)
                                reminderMethod = values[(idx + 1) % values.size]
                            }) { Text("Next") }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = financeOutlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = reminderLeadTime,
                        onValueChange = { if (it.isEmpty() || it.toLongOrNull() != null) reminderLeadTime = it },
                        label = { Text("Lead time (minutes)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        colors = financeOutlinedTextFieldColors()
                    )

                    if (reminderMethod == ReminderMethod.SMS || reminderMethod == ReminderMethod.EMAIL) {
                        OutlinedTextField(
                            value = reminderDestination,
                            onValueChange = { reminderDestination = it },
                            label = { Text(if (reminderMethod == ReminderMethod.SMS) "Phone number" else "Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            shape = RoundedCornerShape(12.dp),
                            colors = financeOutlinedTextFieldColors()
                        )
                    }
                    reminderRule?.let {
                        Text(
                            RecurrenceInputUtils.describeReminder(it),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    selectedCategory?.let {
                        val safeDate = if (dateEntryMode == DateEntryMode.FREE_HAND) {
                            parseDateForLocale(freeHandDateText, locale.language) ?: today()
                        } else {
                            LocalDate(year.toIntOrNull() ?: today().year, month, dayText.toIntOrNull()?.coerceIn(1, 28) ?: today().day)
                        }
                        onSaveExpense(description, amt, it, safeDate, status, source, recurrenceRule, reminderRule)
                        onBackClick()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                enabled = description.isNotBlank() &&
                    amount.isNotBlank() &&
                    selectedCategory != null &&
                    (!recurrenceEnabled || recurrenceRule != null) &&
                    (!reminderEnabled || reminderRule != null)
            ) {
                Text("Save Expense", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

private fun maskDateForLocale(raw: String, language: String): String {
    val digits = raw.filter { it.isDigit() }
    return if (language == "pt") {
        val dateParts = digits.chunked(2)
        val day = dateParts.getOrNull(0).orEmpty()
        val month = dateParts.getOrNull(1).orEmpty()
        val year = dateParts.getOrNull(2).orEmpty()
        buildString {
            if (day.isNotEmpty()) append(day)
            if (month.isNotEmpty()) {
                if (day.isNotEmpty()) append('/')
                append(month)
            }
            if (year.isNotEmpty()) {
                if (day.isNotEmpty() || month.isNotEmpty()) append('/')
                append(year)
            }
        }
    } else {
        val year = digits.take(4)
        val month = digits.drop(4).take(2)
        val day = digits.drop(6).take(2)
        buildString {
            if (year.isNotEmpty()) append(year)
            if (month.isNotEmpty()) {
                if (year.isNotEmpty()) append('-')
                append(month)
            }
            if (day.isNotEmpty()) {
                if (year.isNotEmpty() || month.isNotEmpty()) append('-')
                append(day)
            }
        }
    }
}

private fun parseDateForLocale(raw: String, language: String): LocalDate? {
    val text = raw.trim()
    if (text.isEmpty()) return null
    return try {
        when {
            text.contains('/') -> {
                val parts = text.split('/').map { it.trim() }
                if (parts.size >= 3 && parts[0].isNotEmpty() && parts[1].isNotEmpty() && parts[2].isNotEmpty()) {
                    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                } else null
            }
            text.contains('-') -> {
                val parts = text.split('-').map { it.trim() }
                if (parts.size >= 3 && parts[0].isNotEmpty() && parts[1].isNotEmpty() && parts[2].isNotEmpty()) {
                    LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                } else null
            }
            else -> {
                val digits = text.filter { it.isDigit() }
                if (digits.length >= 8) {
                    val y = digits.substring(0, 4).toIntOrNull() ?: return null
                    val m = digits.substring(4, 6).toIntOrNull() ?: return null
                    val d = digits.substring(6, 8).toIntOrNull() ?: return null
                    LocalDate(y, m, d)
                } else null
            }
        }
    } catch (_: Throwable) {
        null
    }
}

private fun formatDateForLocale(date: LocalDate, language: String): String {
    return if (language == "pt") {
        "%02d/%02d/%04d".format(date.day, date.monthNumber, date.year)
    } else {
        "%04d-%02d-%02d".format(date.year, date.monthNumber, date.day)
    }
}
