package br.inf.cepp.financemanager.util

import br.inf.cepp.financemanager.model.*
import kotlinx.datetime.LocalDate

/**
 * Simple CSV generators for shared module.
 * These produce comma-separated values with basic quoting for fields that contain commas or quotes.
 */
internal fun quoteCsv(value: String): String {
    if (value.contains(',') || value.contains('"') || value.contains('\n')) {
        return '"' + value.replace("\"", "\"\"") + '"'
    }
    return value
}

fun exportProjectsToCsv(projects: List<ProjectPlan>, items: Map<String, List<ProjectItem>>): String {
    val sb = StringBuilder()
    sb.append("Project Name,Start Date,End Date,Budget,Status,Planned Item Description,Planned Item Budget,Planned Item Expected Date,Planned Item Category\n")
    projects.forEach { p ->
        val planned = items[p.name].orEmpty()
        if (planned.isEmpty()) {
            sb.append(
                listOf(
                    quoteCsv(p.name),
                    p.startDate.toString(),
                    p.endDate.toString(),
                    p.budget.toString(),
                    p.status.name,
                    "",
                    "",
                    "",
                    ""
                ).joinToString(",") + "\n"
            )
        } else {
            planned.forEachIndexed { idx, it ->
                sb.append(
                    listOf(
                        quoteCsv(p.name),
                        p.startDate.toString(),
                        p.endDate.toString(),
                        p.budget.toString(),
                        p.status.name,
                        quoteCsv(it.description),
                        it.budget.toString(),
                        it.expectedDate.toString(),
                        quoteCsv(it.category.name)
                    ).joinToString(",") + "\n"
                )
            }
        }
    }
    return sb.toString()
}

fun exportMonthlyToCsv(expenses: List<Expense>): String {
    val sb = StringBuilder()
    sb.append("Date,Description,Category,Amount,Status,Source\n")
    expenses.forEach { e ->
        sb.append(
            listOf(
                e.date.toString(),
                quoteCsv(e.description),
                quoteCsv(e.category.name),
                e.amount.toString(),
                e.status.name,
                e.source.name
            ).joinToString(",") + "\n"
        )
    }
    return sb.toString()
}

/** File saving abstraction - implemented per-platform */
expect fun saveToFile(filename: String, content: ByteArray): String
