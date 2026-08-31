package br.inf.cepp.financemanager.scheduler

import br.inf.cepp.financemanager.model.Expense
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

actual fun postReminderNotification(expense: Expense) {
    val message = "Reminder: ${expense.description} is due on ${expense.date} (${expense.amount})"
    try {
        if (!SystemTray.isSupported()) {
            println(message)
            return
        }

        val tray = SystemTray.getSystemTray()
        val image = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        graphics.dispose()

        val icon = TrayIcon(image, "Finance Manager")
        icon.toolTip = message
        tray.add(icon)
        icon.displayMessage("Expense reminder", message, TrayIcon.MessageType.INFO)
        Executors.newSingleThreadScheduledExecutor().schedule({
            tray.remove(icon)
        }, 5, TimeUnit.SECONDS)
    } catch (_: Throwable) {
        println(message)
    }
}
