package br.inf.cepp.financemanager.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.parseOrNull
import kotlinx.datetime.todayIn
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Clock

class KotlinDateUtils {
}

// Pull today's date safely anywhere across Android, iOS, or Desktop
fun today() = Clock.System.todayIn(TimeZone.currentSystemDefault())

object SafeLocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString()) // Serializes normally to "YYYY-MM-DD"
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val stringValue = decoder.decodeString()
        // If the API returns a bad format, default to today instead of crashing the parser
        return LocalDate.parseOrNull(stringValue) ?: today()
    }
}

fun LocalDate.toFormattedString(): String {
    // Pads single digits with a leading zero (e.g., 5 -> "05")
    val day = day.toString().padStart(2, '0')
    val month = month.number.toString().padStart(2, '0')

    // Returns dd/MM/yyyy format natively in commonMain
    return "$day/$month/${this.year}"
}
