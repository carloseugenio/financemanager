# Plan

## Intent & Goal

A personal expense management app that allows users to track monthly finances via a daily-updated visual dashboard, featuring bank statement import and reconciliation (OFX, CSV, PDF, text), bank SMS reading, receipt scanning, and project-based budget planning. The goal is to provide full visibility into actual and planned spending, simplifying personal financial control through "budgeted vs. actual" comparisons.

## Progress

Phase 1 (Core navigation & scaffold): Complete — left navigation drawer, nav mapping, placeholder screens, top app bar added.

Phase 2 (Dashboard wiring): Complete — month navigation added, DashboardViewModel wired to FinanceService, recent-expense mapping and currency fixes are in place.

Phase 2.5 (Settings persistence): Complete — date entry mode is now persisted to the platform-backed preferences store and restored at startup, enabling free-hand or picker mode to survive app restarts.

Phase 3 (Import & reconciliation UX): Complete — the app now has a real reconciliation draft list and confirm actions, with draft statuses persisted in the service layer.

Phase 4 (Categories CRUD): Complete — categories can be created, edited, and deleted from the management screen, and the repository/view-model layer persists these changes.

Phase 5 (Project planning): Complete — project plans can be created from the new Projects screen, seeded project data is available, and the app exposes a working project planning entry point.

Phase 2.6 (Dashboard polish & smoke fixes): Complete — the loose categories button was removed, month navigation updates the chart correctly, empty months render a meaningful no-expense state, and the top bar includes a direct home/dashboard shortcut. Focused JVM tests cover month navigation and empty-state messaging.

Next phase: export scaffolding and file-generation plumbing.

## Audience & Roles

Single user (personal use). A single role: the authenticated user, who has full access to all app features—dashboard, import, reconciliation, accounts, categories, projects, and exports.

## Core Flows
- These flows must work end-to-end:

### Monthly Dashboard:

- User opens the app → views dashboard showing total spending for the current month, a donut chart by category (with percentages and colored icons), a list of incurred expenses grouped by category, and a section for upcoming planned expenses (with dates and amounts).

### Statement Import:

- User accesses the import screen → selects account type (checking, credit card, other) → uploads a file from the device (OFX, CSV, PDF, TXT) or provides a URL for the system to download automatically → system processes and stores the imported data in the database.

### SMS Reconciliation:

- User accesses reconciliation → system displays unread bank SMS messages detected on the device → user can also paste or manually type SMS text → system extracts amount, date, and merchant from the SMS and creates an expense draft.

### Receipt Reconciliation:

- User accesses reconciliation → selects a stored PDF receipt or scans a receipt using the device's camera → system extracts data from the receipt → generates an expense draft for review. ### Reconciliation and confirmation screen:

- User views a list of imported/extracted expenses in draft status → can edit category, amount, date, and description for each item → confirms individually or in batches → confirmed expenses update the monthly tracker on the main dashboard.

### Account management:

- User accesses account settings → can add a bank account (bank, branch, account number), credit card (network, limit, due date), or digital/online wallet → each account becomes available for linking to expenses and for statement imports.

### Category analysis:

- User accesses the categories screen → views a donut chart + detailed list of spending by category for the selected month → can navigate between periods (monthly) → each category displays a colored icon, total spent, and percentage of the total.

### Project planning:

- User creates a project → defines name, timeframe (start and end dates), and total budget → adds planned expense items by category with estimated amounts and projected dates → tracks the projected vs. actual comparison as expenses are confirmed.

### Export:

- On the project screen or monthly dashboard, the user can export the plan or statement → chooses PDF format (print-ready) or CSV (raw data) → file is generated and made available for download/sharing.

## Technical Requirements

- Core entities:
- Account (type, bank, name, details)
- Expense (amount, date, category, account, status: draft/confirmed, source: manual/import/SMS/receipt)
- Category (name, icon, color)
- ProjectPlan (name, timeframe, budget)
- ProjectItem (category, projected amount, actual amount, projected date).

- File import:
- Upload of OFX, CSV, PDF, or TXT files from the device or via URL.

- SMS reconciliation:
- Reading SMS messages from the device (Web API where available) + manual SMS text input.

- Reconciliation via receipt:
- PDF upload or camera capture (input type=file accept=image/*,capture).

- Export:
- PDF generation and native CSV export.
-
- Storage of imported files in the database.

## Design Preferences

- Colorful, friendly visual style typical of personal finance apps (references: Mobills, Organizze).
- Each category features a unique SVG icon and distinct color (food: orange, transport: blue, health: green, leisure: purple, etc.).
- Light background with rounded white cards and soft shadows.
- Vibrant donut charts.
- Modern sans-serif typography with a clear size hierarchy.
- Buttons in a vibrant primary color (e.g., #4F46E5 indigo or #10B981 green).
- Bottom navigation with 5 icons: Dashboard, Import, Reconcile, Categories, Projects.
- Smooth micro-interactions on cards. Mobile-first responsive layout.

## Schema changes

- The database schema was updated during the recent project planning changes. AppDatabase.version was incremented to 2 and the JVM database builder was configured with a destructive fallback to keep local developer databases compatible with the evolving schema.
- Consequence: existing local/mock data will be cleared automatically on JVM runs. This is intentional for the current dev phase. If preserving real user data becomes necessary, implement a proper v1→v2 migration and remove the destructive fallback.

## Next phase (updated)

Primary goal: Export scaffolding and file generation.

Planned deliverables:
- Implement an Export screen accessible from the Projects screen and the Monthly Dashboard.
- Add CSV and PDF generation support for:
  - Project plans (planned vs actual) and
  - Monthly statements (detailed expense rows + summary).
- Wire UI actions to trigger export generation and provide download/share UX on supported platforms (JVM, Android).
- Add unit and shared-JVM tests to validate generated CSV/PDF contents and integration with the UI.
- After exports are validated, remove destructive migration and add a proper migration path if schema changes remain.

Timeline: next sprint — implement export UI + generation, add tests, then iterate on platform sharing behavior.

