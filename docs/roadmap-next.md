# Finance Application — Roadmap Continuation

This document continues `docs/plan-v2.md` after the completed navigation, dashboard, settings, reconciliation, categories, project planning, and dashboard-polish phases.

## Roadmap principles

1. Deliver vertical slices rather than isolated infrastructure.
2. Keep Room, Koin, and Compose Material 3 as the established stack.
3. Keep domain rules platform-independent.
4. Validate financial calculations with deterministic tests.
5. Apply the navy/blue/gray design system to every new screen.
6. Prefer purposeful charts and graphics over decorative imagery.
7. Do not add a dependency without a concrete requirement.

---

## Phase 6 — Export Foundation
**Status:** Completed

### Goals
Implement reliable export generation and platform integration.

### Deliverables
- Export use cases.
- CSV exporter for transactions and project plans.
- PDF exporter for monthly statements and project plans.
- Export screen using Material 3.
- Export progress/loading/error states.
- Android/JVM file/share integration where supported.
- Tests for CSV structure, PDF content, and error handling.

### UI direction
Use a clean document/export illustration or iconography rather than generic imagery.
Primary action: Navy800 `#123A5A`.
Secondary information: Gray500 `#5B6B78`.

### Exit criteria
A user can export a monthly statement and project plan without corrupting or silently changing financial values.

---

## Phase 7 — Financial Data Foundation
**Status:** In progress

### Goals
Strengthen the domain model for reliable financial aggregation.

### Deliverables
- Introduce explicit money/currency value objects if not already present.
- Centralize rounding rules.
- Add a shared incoming-record model so cashflow is not expense-only.
- Add transaction aggregation use cases.
- Add monthly/category/account summaries.
- Add budgeted-vs-actual calculation service.
- Add deterministic tests for all calculations.

### Exit criteria
Dashboard totals, category totals, project actuals, and exports are derived from shared domain calculations rather than duplicated UI logic.

---

## Phase 8 — Dashboard Visual Redesign
**Status:** Planned

### Goals
Transform the existing dashboard into the new sober investment/finance visual system.

### Deliverables
- New Material 3 theme using the defined light/dark palette.
- Primary financial summary card.
- Budgeted-vs-actual visualization.
- Category allocation donut chart.
- Monthly performance chart.
- Recent transaction list.
- Upcoming planned expenses.
- Empty/loading/error states.
- Responsive layout for desktop/tablet/mobile targets.

### Graphics
Use:
- #2F80C0 for primary chart series.
- #287A5A for positive movement.
- #B54848 for negative movement.
- #D7E0E7 for grids and subtle structure.

Do not rely only on color to communicate financial direction.

### Exit criteria
The dashboard answers these questions at a glance:
- How much have I spent?
- How am I performing against the plan?
- Where is the money going?
- What is coming next?

---

## Phase 9 — Accounts & Asset Overview
**Status:** Planned

### Goals
Improve account visibility and prepare the product for investment-oriented workflows.

### Deliverables
- Account overview screen.
- Account balances.
- Credit-card utilization.
- Account detail screen.
- Asset/holding abstraction where applicable.
- Asset allocation visualization.
- Account-level transaction filtering.

### Visual direction
Use navy cards and subtle blue highlights sparingly. Keep financial numbers dominant and supporting metadata muted.

---

## Phase 10 — Import & Reconciliation Intelligence
**Status:** Planned

### Goals
Make imports safer and reduce manual reconciliation effort.

### Deliverables
- Import preview before persistence.
- Duplicate detection.
- Transaction normalization.
- Merchant normalization.
- Category suggestion workflow.
- Confidence indicators for extracted data.
- Improved batch confirmation.
- Import history.

### Safety requirement
Never automatically convert uncertain imported data into confirmed expenses without an explicit product rule and user-visible confidence/review behavior.

---

## Phase 11 — Receipt & Document Processing
**Status:** Planned

### Deliverables
- Receipt capture flow.
- Document preview.
- Extracted-field review.
- Merchant/date/amount/category suggestions.
- Correction workflow.
- Attachment metadata.

### Visual direction
Use a restrained document/receipt illustration for empty states and first-use screens. Avoid generic stock photography.

---

## Phase 12 — Investment Dashboard
**Status:** Planned

### Goals
Extend the finance application toward investment portfolio management while preserving the existing product language.

### Deliverables
- Portfolio summary.
- Total invested value.
- Current value.
- Absolute and percentage performance.
- Asset allocation.
- Historical performance.
- Holdings list.
- Individual asset detail.
- Time-range selector.

### Charts
Primary performance line: #2F80C0.
Positive state: #287A5A.
Negative state: #B54848.
Baseline/grid: #D7E0E7.

Avoid excessive chart colors. Use labels and typography to distinguish series.

---

## Phase 13 — Search, Filtering & Drill-down
**Status:** Planned

### Deliverables
- Global transaction search.
- Date filters.
- Account filters.
- Category filters.
- Project filters.
- Asset filters.
- Saved filter state where useful.
- Drill-down from dashboard metrics into source transactions.

### Exit criteria
Every aggregate financial number presented in the dashboard can be traced to understandable underlying records.

---

## Phase 14 — Security & Data Protection
**Status:** Planned

### Deliverables
- Review local sensitive-data storage.
- Secure credential/token handling.
- Sensitive log audit.
- Database backup/restore strategy where required.
- App lock/authentication strategy if product scope requires it.
- Privacy review for imported statements, SMS, receipts, and account data.

### Exit criteria
No credentials or sensitive financial information are unnecessarily persisted or logged.

---

## Phase 15 — Quality, Performance & Release Readiness
**Status:** Planned

### Deliverables
- Unit-test coverage for financial domain rules.
- Room migration tests.
- UI tests for critical flows.
- Performance profiling for dashboard aggregation.
- Large transaction-set testing.
- Offline/error behavior validation.
- Accessibility audit.
- Light/dark theme audit.
- Build verification for supported KMP targets.
- Release checklist.

---

## Recommended execution order

```text
Phase 6  Export Foundation
   ↓
Phase 7  Financial Data Foundation
   ↓
Phase 8  Dashboard Visual Redesign
   ↓
Phase 9  Accounts & Asset Overview
   ↓
Phase 10 Import & Reconciliation Intelligence
   ↓
Phase 11 Receipt & Document Processing
   ↓
Phase 12 Investment Dashboard
   ↓
Phase 13 Search / Filtering / Drill-down
   ↓
Phase 14 Security & Data Protection
   ↓
Phase 15 Quality / Performance / Release
```

## Copilot workflow for each phase

For each phase, create one small vertical implementation task at a time.

Recommended prompt:

```text
Implement the next incomplete deliverable in docs/roadmap-next.md.

First inspect the existing code and identify the current implementation pattern.
Use Room for persistence, Koin for dependency injection, and JetBrains Compose Material 3 for UI.
Use .github/copilot-instructions.md and the applicable path-specific instructions.
For UI work, apply the finance-ui skill and design/finance-ui-visual-spec.md.

Implement a vertical slice across presentation, domain, and data layers as required.
Add deterministic tests for business logic.
Handle loading, success, empty, and error states where applicable.
Do not perform unrelated refactoring.
After implementation, report the files changed, tests added, and any remaining risks.
```
