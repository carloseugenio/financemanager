Checkpoint: Reconciliation + unified financial records

Problem and approach:
- Add real reconciliation capabilities by introducing a unified financial record flow that supports manual creation and receipt import, with explicit account/category associations and transfer semantics.
- Keep architecture consistent (Presentation -> Domain -> Data), move business rules to use cases, and keep UI aligned with existing sober finance screen guidelines and Material 3 patterns already established in the app.

Progress baseline from previous checkpoint:
- Export UX/platform polish and recurring-expense planning already documented in repository plan.md.

Scope additions requested:
1. Manual "Create new record" functionality:
   - New screen/entry point to create a record with:
     - Description text
     - Date and time (default: today/current time)
     - Type selector: Expense, Income, Transfer
   - Required associations by type:
     - Expense: account + category required
     - Income: account + category required
     - Transfer: source account + destination account required; no category; include note, date/time, amount
   - Amount/value required for all record types.

2. Data/model/repository support:
   - Extend domain and persistence model to represent unified records and relationship invariants:
     - RecordType enum/sealed type
     - Record aggregate with shared fields and type-specific required fields
   - Enforce referential associations in repository/use-case validation:
     - Account must exist
     - Category must exist for Expense/Income
     - Source and destination accounts must both exist and differ for Transfer
   - Ensure existing features consuming expenses/incomes/transfers stay consistent through mapping/adapters where needed.

3. Receipt import to reconciliation:
   - Import receipt creates a linked financial record automatically (user-confirmed behavior).
   - Default imported receipt flow to Expense record and allow user to edit account/category before saving.
   - On save, record is persisted and reflected in account balance/reconciliation totals through standard domain flows (no direct UI-layer balance mutation).

4. Screen-guideline compliance rules (must follow):
   - Use existing Compose Material 3 components and semantic theme tokens; no arbitrary colors.
   - Keep Composables stateless where possible; state in ViewModel/state holder.
   - Cover loading/success/empty/error states for new flows.
   - Keep financial-state indicators accessible (not color-only).
   - Reuse established form patterns/spacing/typography from current stabilized screens.

Ordered implementation steps:
1. Discovery and reuse mapping:
   - Locate current transaction/expense/income/transfer models, DAOs, repositories, and screen patterns.
   - Identify reusable form components, pickers, and validation messaging patterns.

2. Domain model and rules:
   - Introduce/extend unified financial record domain model and type-specific validation.
   - Add use cases for:
     - CreateManualRecord
     - CreateTransferRecord
     - CreateRecordFromReceipt
   - Keep money handling with existing decimal/money abstractions (no Float/Double).

3. Data layer and persistence wiring:
   - Update Room entities/relations and repository implementations for account/category associations.
   - Add migration-safe schema changes for new/updated columns and relations.
   - Map entities <-> domain with clear invariants.

4. Presentation layer:
   - Add "Create new record" screen/flow with defaults (today/current time).
   - Implement type-switching UI rules:
     - Expense/Income shows account + category selectors
     - Transfer shows source/destination account selectors and hides category
   - Add field validation and disabled-save states until required data is valid.

5. Receipt import integration:
   - Update receipt-import flow to prefill Expense draft.
   - Require account/category confirmation before save.
   - Persist through unified create-record use case and update reconciliation/account summaries via existing reactive streams.

6. Reconciliation behavior:
   - Ensure newly created/manual/imported records appear in reconciliation views and totals.
   - Validate transfer effect correctness across both involved accounts without double-counting net worth.

7. Tests:
   - Domain tests for type-specific validation and balance effects.
   - Repository/data tests for associations and migrations.
   - ViewModel tests for create-record states and receipt-import edit/save path.
   - UI tests for form behavior per record type and default date/time rendering.

Notes and decisions captured from clarifications:
- Receipt import behavior: create linked financial record automatically.
- Receipt import default: Expense, editable account/category before save.
- Transfer semantics: source + destination account required; no category; includes note, date/time, amount.
