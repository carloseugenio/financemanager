package br.inf.cepp.financemanager.scheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import br.inf.cepp.financemanager.AndroidContext
import br.inf.cepp.financemanager.model.Expense

actual fun postReminderNotification(expense: Expense) {
    val ctx = AndroidContext.appContext
    val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    val channelId = "recurring_expense_reminders"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Recurring expense reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }

    val title = "Expense reminder"
    val body = "${expense.description} is due on ${expense.date} (${expense.amount})"
    val notification = NotificationCompat.Builder(ctx, channelId)
        .setSmallIcon(android.R.drawable.stat_notify_more)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    manager.notify(("reminder-" + expense.id + "-" + expense.date).hashCode(), notification)
}
