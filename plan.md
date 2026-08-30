Checkpoint: Export UX & Platform polish

Progress:
- Implemented locale-aware date input masking and lenient parsing for project planned items and expense free-hand date.
- Added Android FileProvider configuration and an Android share helper (share intent) in shared module; JVM opens file on desktop.
- Added filename sanitization and an in-memory export history in ExportViewModel; ExportScreen now shows recent exports with a Share button.

Next steps:
1. Improve locale parsing to support more formats and strict validation messages.
2. Persist export history in AppSettings storage.
3. Implement file-open / show-in-folder actions and FileProvider tests.
4. Add unit tests for DateInputUtils and FilenameUtils.

5. Add recurring expenses & reminders (models + UI + scheduling):
   - Model changes (implemented): RecurrenceRule, RecurrenceFrequency, ReminderMethod, Reminder; ExpenseItem now includes optional recurrence and reminder embedded fields.

Ordered implementation steps:
1. UI for recurring expenses (priority):
   - Add controls to create a recurring expense: frequency (daily/weekly/monthly/yearly), interval, optional count or until date, and reminder options (method, lead time, destination).
   - Reuse AddExpenseScreen and Project planned-item sheet; validate recurrence inputs.
   - Add UI tests and story examples.

2. Platform schedulers & reminders (after UI):
   - Android: implement WorkManager or AlarmManager + Notification channels to schedule reminders and materialize recurring entries.
   - JVM/Desktop: provide a simple scheduler (background task) and desktop notifications where available.
   - For SMS/Email: integrate or provide extension points to plug an external service (do not ship credentials). Use platform-specific implementations.

3. Persistence and background job (after scheduler):
   - Persist next-run metadata per recurring rule (nextRunDate, remainingCount).
   - Implement a background worker that runs daily to create Expense entries when due and update nextRunDate / count.
   - Add safe guards for duplicate runs and timezone handling.

Notes:
- Keep scheduling decoupled from model storage: store recurrence rules and let a separate scheduler service read them.
- For production SMS/email, require user-provided service config; add UI settings for provider credentials (opt-in).

Date: 2026-08-29T18:11:00-03:00
