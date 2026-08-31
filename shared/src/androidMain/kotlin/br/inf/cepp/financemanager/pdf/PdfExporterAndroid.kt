package br.inf.cepp.financemanager.pdf

import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.model.Expense
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import java.io.ByteArrayOutputStream

private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MAX_BODY_LINES_PER_PAGE = 46

private fun writeTextLines(doc: PdfDocument, pageIndex: Int, title: String, lines: List<String>) {
    val page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex).create())
    val canvas = page.canvas
    val paint = Paint()
    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    paint.textSize = 12f
    var y = 36f

    canvas.drawText(title, 20f, y, paint)
    y += 20f

    lines.forEach { line ->
       canvas.drawText(line, 20f, y, paint)
       y += 16f
    }
    doc.finishPage(page)
}

actual fun generateProjectsPdf(projects: List<ProjectPlan>, items: Map<String, List<ProjectItem>>): ByteArray {
    val doc = PdfDocument()
    var pageIdx = 1
    projects.forEach { p ->
       val lines = mutableListOf<String>()
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
       lines.chunked(MAX_BODY_LINES_PER_PAGE).forEach { chunk ->
           writeTextLines(doc, pageIdx++, "Project: ${p.name}", chunk)
       }
    }
    val baos = ByteArrayOutputStream()
    doc.writeTo(baos)
    doc.close()
    return baos.toByteArray()
}

actual fun generateMonthlyPdf(expenses: List<Expense>): ByteArray {
    val doc = PdfDocument()
    val lines = mutableListOf<String>()
    lines.add("Generated expenses")
    lines.add("")
    expenses.forEach { e ->
       lines.add("${e.date} | ${e.description} | ${e.amount}")
    }
    lines.chunked(MAX_BODY_LINES_PER_PAGE).forEachIndexed { index, chunk ->
       val title = if (index == 0) "Monthly Statement" else "Monthly Statement (cont.)"
       writeTextLines(doc, index + 1, title, chunk)
    }
    val baos = ByteArrayOutputStream()
    doc.writeTo(baos)
    doc.close()
    return baos.toByteArray()
}
