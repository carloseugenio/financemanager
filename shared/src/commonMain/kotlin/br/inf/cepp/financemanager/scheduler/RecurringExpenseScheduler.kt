package br.inf.cepp.financemanager.scheduler

interface RecurringExpenseScheduler {
    fun start()
    fun stop()
    fun cancel() = Unit  // Default no-op for base implementation
}
