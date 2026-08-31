package br.inf.cepp.financemanager.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import br.inf.cepp.financemanager.model.*
import java.io.ByteArrayOutputStream

private const val PAGE_MARGIN_LEFT = 50f
private const val PAGE_MARGIN_TOP = 720f
private const val LINE_HEIGHT = 14f
private const val MAX_BODY_LINES_PER_PAGE = 42

private fun String.pdfSafe(): String {
    return replace('\u2014', '-').replace('\n', ' ').replace('\r', ' ')
}

private fun writePage(
    doc: PDDocument,
    heading: String,
    bodyLines: List<String>,
) {
    val page = PDPage(PDRectangle.LETTER)
    doc.addPage(page)
    PDPageContentStream(doc, page).use { cs ->
        cs.beginText()
        cs.setFont(PDType1Font.HELVETICA_BOLD, 16f)
        cs.newLineAtOffset(PAGE_MARGIN_LEFT, PAGE_MARGIN_TOP)
        cs.showText(heading.pdfSafe())
        cs.endText()

        var y = PAGE_MARGIN_TOP - 22f
        bodyLines.forEach { line ->
            cs.beginText()
            cs.setFont(PDType1Font.HELVETICA, 11f)
            cs.newLineAtOffset(PAGE_MARGIN_LEFT, y)
            cs.showText(line.pdfSafe())
            cs.endText()
            y -= LINE_HEIGHT
        }
    }
}

actual fun generateProjectsPdf(projects: List<ProjectPlan>, items: Map<String, List<ProjectItem>>): ByteArray {
    PDDocument().use { doc ->
        projects.forEach { p ->
            val planned = items[p.name].orEmpty()
            val body = buildList {
                add("Period: ${p.startDate} - ${p.endDate}")
                add("Budget: ${p.budget}")
                add("")
                if (planned.isEmpty()) {
                    add("No planned items")
                } else {
                    planned.forEach { item ->
                        add("- ${item.description}: ${item.budget} (expected ${item.expectedDate})")
                    }
                }
            }
            body.chunked(MAX_BODY_LINES_PER_PAGE).forEach { chunk ->
                writePage(doc, "Project: ${p.name}", chunk)
            }
        }
        val baos = ByteArrayOutputStream()
        doc.save(baos)
        return baos.toByteArray()
    }
}

actual fun generateMonthlyPdf(expenses: List<Expense>): ByteArray {
    PDDocument().use { doc ->
        val body = buildList {
            add("Expenses: ${expenses.size}")
            add("")
            expenses.forEach { expense ->
                add("${expense.date} - ${expense.description} - ${expense.category.name} - ${expense.amount}")
            }
        }
        body.chunked(MAX_BODY_LINES_PER_PAGE).forEach { chunk ->
            writePage(doc, "Monthly Statement", chunk)
        }
        val baos = ByteArrayOutputStream()
        doc.save(baos)
        return baos.toByteArray()
    }
}