package br.inf.cepp.financemanager.pdf

import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.model.Expense
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import java.io.ByteArrayOutputStream

private fun writeTextLines(doc: PdfDocument, pageIndex: Int, lines: List<String>) {
    val page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, pageIndex).create())
    val canvas = page.canvas
    val paint = Paint()
    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    paint.textSize = 12f
    var y = 30f
    lines.forEach { line ->
        canvas.drawText(line, 20f, y, paint)
        y += 16f
        if (y > 800f) {
            // page full, stop (simple approach)
        }
    }
    doc.finishPage(page)
}

actual fun generateProjectsPdf(projects: List<ProjectPlan>, items: Map<String, List<ProjectItem>>): ByteArray {
    val doc = PdfDocument()
    var pageIdx = 1
    projects.forEach { p ->
        val lines = mutableListOf<String>()
        lines.add("Project: ${p.name}")
        lines.add("Period: ${p.startDate} - ${p.endDate}")
        lines.add("Budget: ${p.budget}")
        lines.add("")
        val planned = items[p.name].orEmpty()
        if (planned.isEmpty()) {
            lines.add("No planned items")
        } else {
            planned.forEach { it ->
                lines.add("- ${it.description} | ${it.expectedDate} | ${it.budget}")
            }
        }
        writeTextLines(doc, pageIdx++, lines)
    }
    val baos = ByteArrayOutputStream()
    doc.writeTo(baos)
    doc.close()
    return baos.toByteArray()
}

actual fun generateMonthlyPdf(expenses: List<Expense>): ByteArray {
    val doc = PdfDocument()
    val lines = mutableListOf<String>()
    lines.add("Expenses Report")
    lines.add("")
    expenses.forEach { e ->
        lines.add("${e.date} | ${e.description} | ${e.amount}")
    }
    writeTextLines(doc, 1, lines)
    val baos = ByteArrayOutputStream()
    doc.writeTo(baos)
    doc.close()
    return baos.toByteArray()
}
