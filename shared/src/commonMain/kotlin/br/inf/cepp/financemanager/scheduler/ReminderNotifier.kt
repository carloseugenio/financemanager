package br.inf.cepp.financemanager.scheduler

import br.inf.cepp.financemanager.model.Expense

expect fun postReminderNotification(expense: Expense)
