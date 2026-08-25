package br.inf.cepp.financemanager

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock


class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello("Hello on ${platform.name}")
    }

    fun todayDate(): String {
        fun LocalDateTime.format() = toString().substringBefore('T')

        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        return now.toLocalDateTime(zone).format()
    }

    fun currentMonth(): String {
        fun LocalDateTime.format() = toString().substringBefore('T')
        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        return now.toLocalDateTime(zone).format()
    }
}