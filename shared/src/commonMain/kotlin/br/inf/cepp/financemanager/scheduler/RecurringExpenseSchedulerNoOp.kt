package br.inf.cepp.financemanager.scheduler

class RecurringExpenseSchedulerNoOp : RecurringExpenseScheduler {
    override fun start() = Unit
    override fun stop() = Unit
}
