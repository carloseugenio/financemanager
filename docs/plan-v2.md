# Finance Application Plan — Visual and Architecture Update

## 1. Intent & Goal

A personal finance and investment management application that provides a daily-updated visual dashboard, statement import and reconciliation, SMS/receipt reconciliation, account and category management, project-based budgeting, and export capabilities.

The core product objective remains full visibility into actual and planned finances through clear **budgeted vs. actual** comparisons.

The original plan is retained conceptually, but the implementation baseline is now explicitly Kotlin Multiplatform with Room, Koin, and JetBrains Compose Material 3. fileciteturn2file0L12-L14

## 2. Current Progress

Completed:

- Core navigation and application scaffold.
- Monthly dashboard wiring and month navigation.
- Settings persistence for date-entry mode.
- Import/reconciliation draft UX and confirmation actions.
- Category CRUD.
- Project planning entry point and seeded project data.
- Dashboard polish, including month-aware charts, empty-month messaging, and focused JVM smoke tests. fileciteturn2file0L16-L32

Current next functional area from the original plan: export scaffolding and file generation. fileciteturn2file0L107-L120

## 3. Product Flows

### Monthly dashboard
Show:
- current month and period navigation
- total spending
- portfolio/financial summary where applicable
- incoming records alongside expenses where applicable
- category allocation donut chart
- recent/included expenses
- upcoming planned expenses
- budgeted vs actual indicators

The original dashboard requirement includes total monthly spending, category distribution, incurred expenses, and upcoming planned expenses. fileciteturn2file0L38-L43

### Statement import
Support OFX, CSV, PDF, and TXT input, subject to platform capabilities and the selected import implementation. Imported data must pass through validation and reconciliation before becoming confirmed expenses. fileciteturn2file0L45-L47

### SMS reconciliation
Allow detected or manually supplied bank-message text to produce reviewable expense drafts. fileciteturn2file0L49-L51

### Receipt reconciliation
Allow receipt documents/images to be processed into reviewable expense drafts. The confirmation flow must remain explicit and user-controlled. fileciteturn2file0L53-L57

### Accounts
Support bank accounts, credit cards, and digital wallets, with reusable account metadata for imports and expenses. fileciteturn2file0L59-L61

### Categories
Provide monthly category analysis with a donut chart and detailed totals/percentages. fileciteturn2file0L63-L65

### Project planning
Projects define a name, timeframe, total budget, planned expense items, projected dates, and actual-vs-projected tracking. fileciteturn2file0L67-L69

### Export
Provide PDF and CSV export for project plans and monthly statements, with platform-appropriate sharing/download behavior. fileciteturn2file0L71-L73

## 4. Architecture Baseline

```text
Compose Material 3 UI
        ↓
ViewModel / State Holder
        ↓
Use Cases / Domain
        ↓
Repository interfaces
        ↓
Repository implementations
        ↓
Room / APIs / platform data sources
```

### Persistence
Room is the authoritative local database mechanism.

Core entities remain:
- Account
- Expense
- Category
- ProjectPlan
- ProjectItem

These entities and their existing responsibilities are inherited from the original plan. fileciteturn2file0L75-L82

### Dependency injection
Koin is the project's dependency-injection mechanism.

### UI
JetBrains Compose Multiplatform Material 3 is the UI foundation.

## 5. Visual Design System

The application adopts a sober navy/blue/gray financial visual system.

### Light palette
- Primary navy: #0B1F33
- Secondary navy: #123A5A
- Steel blue: #1F5A82
- Accent blue: #2F80C0
- Background: #F5F7FA
- Surface: #FFFFFF
- Secondary surface: #EEF2F5
- Border: #D7E0E7
- Secondary text: #5B6B78
- Primary text: #172B3A
- Positive: #287A5A
- Negative: #B54848
- Warning: #A87519

### Dark palette
- Background: #08131F
- Surface: #0D1D2C
- Elevated surface: #13283A
- Primary: #4A91C5
- Primary light: #73B1D8
- Primary text: #E8EEF3
- Secondary text: #AAB9C5
- Border: #263D50
- Positive: #4DA67D
- Negative: #D06A6A
- Warning: #D0A34A

In Compose, use `Color(0xFFRRGGBB)`.

## 6. Graphics and Images

Replace generic decorative imagery with purposeful financial graphics.

Preferred visuals:
- portfolio performance charts
- allocation charts
- budget-versus-actual graphics
- transaction/document illustrations
- account cards and financial instrument icons
- restrained editorial imagery for onboarding or empty states

Charts should be data-first, accessible, and consistent with the palette. Avoid rainbow charts, gradients, 3D charts, and decorative stock imagery.

The detailed visual source of truth is `design/finance-ui-visual-spec.md` and the implementation workflow is `.github/skills/finance-ui/SKILL.md`.

## 7. Data and Financial Correctness

- Monetary values must use exact decimal/money semantics.
- Do not use Float/Double for exact money calculations.
- Preserve currency context.
- Keep financial calculations in domain/use-case code.
- Test rounding, percentage, currency, and aggregation behavior.
- Never silently alter imported financial values.

## 8. Database Migration Note

The previous plan recorded a temporary Room/JVM destructive fallback after incrementing the database version to 2. This was explicitly described as development-only behavior. fileciteturn2file0L102-L105

Before release, replace the destructive fallback with explicit Room migrations and test upgrade paths.

## 9. Immediate Deliverable

Complete export scaffolding:
- Export screen from dashboard and projects.
- CSV generation.
- PDF generation.
- Project plan export.
- Monthly statement export.
- Platform sharing/download integration where supported.
- Unit/shared-JVM tests for generated output.

These requirements continue the original next-phase definition. fileciteturn2file0L107-L120

## 10. Definition of Done

A feature is complete when it:
- follows KMP architecture
- uses Room and Koin consistently
- uses Compose Material 3
- follows the finance visual system
- supports appropriate loading/empty/error states
- is accessible
- contains relevant tests
- does not log sensitive data
- does not introduce unnecessary dependencies
- does not include unrelated refactoring
