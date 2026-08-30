package br.inf.cepp.financemanager.pdf

import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.model.Expense

expect fun generateProjectsPdf(projects: List<ProjectPlan>, items: Map<String, List<ProjectItem>>): ByteArray
expect fun generateMonthlyPdf(expenses: List<Expense>): ByteArray
