package br.inf.cepp.financemanager.scheduler

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.inf.cepp.financemanager.getDatabaseBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class DesktopRecurringExpenseScheduler : RecurringExpenseScheduler {
    private val started = AtomicBoolean(false)
    private val executor = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "recurring-expense-scheduler").apply { isDaemon = true }
    }
    private var future: ScheduledFuture<*>? = null

    override fun start() {
        if (!started.compareAndSet(false, true)) return
        future = executor.scheduleAtFixedRate(
            { runBlocking(Dispatchers.IO) { runOnce() } },
            0,
            24,
            TimeUnit.HOURS
        )
    }

    override fun stop() {
        future?.cancel(false)
        future = null
        started.set(false)
    }

    override fun cancel() {
        stop()
    }

    private suspend fun runOnce() {
        val database = getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        try {
            RecurringExpenseProcessor.process(database)
        } finally {
            database.close()
        }
    }
}
