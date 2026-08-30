package br.inf.cepp.financemanager.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import br.inf.cepp.financemanager.model.*

actual fun generateProjectsPdf(projects: List<ProjectPlan>, items: Map<String, List<ProjectItem>>): ByteArray {
    PDDocument().use { doc ->
        projects.forEach { p ->
            val page = PDPage(PDRectangle.LETTER)
            doc.addPage(page)
            PDPageContentStream(doc, page).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16f)
                cs.newLineAtOffset(50f, 700f)
                cs.showText("Project: ${p.name}")
                cs.endText()

                cs.beginText()
                cs.setFont(PDType1Font.HELVETICA, 12f)
                cs.newLineAtOffset(50f, 680f)
                cs.showText("Period: ${p.startDate} — ${p.endDate}")
                cs.endText()

                cs.beginText()
                cs.setFont(PDType1Font.HELVETICA, 12f)
                cs.newLineAtOffset(50f, 660f)
                cs.showText("Budget: ${p.budget}")
                cs.endText()

                val planned = items[p.name].orEmpty()
                var y = 640f
                planned.forEach { it ->
                    if (y < 80f) {
                        cs.close()
                        val newPage = PDPage(PDRectangle.LETTER)
                        doc.addPage(newPage)
                        PDPageContentStream(doc, newPage).use { /* continue on new page later */ }
                        y = 700f
                    }
                    PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true).use { cs2 ->
                        cs2.beginText()
                        cs2.setFont(PDType1Font.HELVETICA, 11f)
                        cs2.newLineAtOffset(60f, y)
                        cs2.showText("- ${it.description}: ${it.budget} (expected ${it.expectedDate})")
                        cs2.endText()
                    }
                    y -= 16f
                }
            }
        }
        val baos = java.io.ByteArrayOutputStream()
        doc.save(baos)
        return baos.toByteArray()
    }
}

actual fun generateMonthlyPdf(expenses: List<Expense>): ByteArray {
    PDDocument().use { doc ->
        val page = PDPage(PDRectangle.LETTER)
        doc.addPage(page)
        PDPageContentStream(doc, page).use { cs ->
            cs.beginText()
            cs.setFont(PDType1Font.HELVETICA_BOLD, 16f)
            cs.newLineAtOffset(50f, 700f)
            cs.showText("Monthly Statement")
            cs.endText()

            var y = 680f
            expenses.forEach { e ->
                if (y < 80f) {
                    cs.close()
                    val newPage = PDPage(PDRectangle.LETTER)
                    doc.addPage(newPage)
                    PDPageContentStream(doc, newPage).use { /* continue*/ }
                    y = 700f
                }
                PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true).use { cs2 ->
                    cs2.beginText()
                    cs2.setFont(PDType1Font.HELVETICA, 11f)
                    cs2.newLineAtOffset(50f, y)
                    cs2.showText("${e.date} - ${e.description} - ${e.category.name} - ${e.amount}")
                    cs2.endText()
                }
                y -= 14f
            }
        }
        val baos = java.io.ByteArrayOutputStream()
        doc.save(baos)
        return baos.toByteArray()
    }
}