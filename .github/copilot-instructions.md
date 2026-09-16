# Finance KMP — GitHub Copilot Repository Instructions

## Project context
This repository contains a Kotlin Multiplatform personal finance and investment application. The product provides a monthly financial dashboard, statement import/reconciliation, SMS and receipt reconciliation, account/category management, project-based budgeting, and export capabilities.

The product must feel sober, trustworthy, professional, and appropriate for financial/investment software.

## Tech Stack & Architecture
Use the existing architecture and do not introduce competing patterns.

Preferred dependency flow:

Presentation → Domain → Data

Typical flow:

Composable → ViewModel/State holder → Use Case → Repository → Data Source

Technology decisions:
- Kotlin Multiplatform
- Compose Multiplatform using JetBrains Compose Material 3
- Room for local database persistence
- Koin for dependency injection
- Kotlin Coroutines and Flow/StateFlow
- Use the project's existing networking, serialization, navigation, and testing libraries before adding dependencies.
- Follow clean architecture principles (Separation of concerns between UI, ViewModel, and Data layers).
- Use standard Gradle Kotlin DSL (`.gradle.kts`) for build files.

UI code must not access Room DAOs, HTTP clients, repositories, or platform APIs directly.

## Room
- Keep Room entities in the data layer.
- Use DAO interfaces for persistence operations.
- Map Room entities to domain models.
- Keep schema changes explicit and migration-safe.
- Do not add destructive migration fallbacks for production behavior.
- If a destructive fallback is temporarily required for development, document it and create a migration task before release.

## Koin
- Register dependencies through Koin modules.
- Prefer constructor injection.
- Avoid service locators and manually constructed dependency graphs in production code.
- Keep modules organized by feature/layer where the existing project structure permits it.

## Kotlin
- Prefer immutable data and `val`.
- Use sealed interfaces/classes for finite UI/domain states.
- Avoid `!!` unless an invariant is explicitly guaranteed.
- Use structured concurrency; never use `GlobalScope`.
- Keep business rules out of Composables.

## Financial correctness
Never use `Float` or `Double` for exact monetary calculations. Use the project's money/decimal representation or introduce a dedicated domain abstraction when necessary.

Keep currency, amount, quantity, price, rate, and percentage semantically distinct.

Do not silently assume a currency.

Financial calculations belong in the domain layer and must have deterministic unit tests.

## UI and visual system
Use JetBrains Compose Material 3 and the project's semantic theme tokens. Do not hard-code arbitrary colors in screens.

Light palette:
- Navy900: #0B1F33
- Navy800: #123A5A
- Navy700: #1F5A82
- Blue600: #2F80C0
- Gray50: #F5F7FA
- Gray100: #EEF2F5
- Gray200: #D7E0E7
- Gray400: #9AA7B2
- Gray500: #5B6B78
- Gray900: #172B3A
- Positive: #287A5A
- Negative: #B54848
- Warning: #A87519

Dark palette:
- Background: #08131F
- Surface: #0D1D2C
- Elevated: #13283A
- Primary: #4A91C5
- PrimaryLight: #73B1D8
- TextPrimary: #E8EEF3
- TextSecondary: #AAB9C5
- Border: #263D50
- Positive: #4DA67D
- Negative: #D06A6A
- Warning: #D0A34A

In Kotlin Compose use `Color(0xFFRRGGBB)`.

Design language:
- sober, institutional, modern fintech
- white/cool-gray surfaces with deep navy hierarchy
- restrained elevation and borders
- no neon colors
- no decorative gradients unless explicitly requested
- avoid excessive rounded cards and visual noise
- green/red are semantic financial states, not decorative colors
- never communicate financial state by color alone

## Visual assets
When a screen benefits from imagery or graphics, prefer purposeful financial visuals: portfolio charts, allocation graphics, sparklines, account/transaction illustrations, document-import illustrations, and subtle market/investment imagery.

Do not use generic stock-photo decoration in information-dense screens. Graphics must support comprehension.

For image-generation or visual-design tasks, consult `.github/skills/finance-ui/SKILL.md`.

## Compose
Prefer stateless, reusable Composables with state hoisted to ViewModels/state holders.

Every non-trivial screen should account for loading, success, empty, error, and refreshing states as applicable.

Use accessible semantics, content descriptions for meaningful imagery, adequate contrast, and touch targets.

## Testing
Add or update tests for business logic, financial calculations, ViewModel state transitions, Room behavior, and important error paths.

Prefer deterministic tests. Do not make unit tests depend on real network services, current wall-clock time, or random values.

## Security
Never log tokens, passwords, complete account numbers, sensitive personal information, or raw financial documents. Never hard-code secrets.

## Implementation behavior
Before coding:
1. Inspect existing implementations for similar behavior.
2. Reuse existing abstractions and libraries.
3. Identify affected presentation/domain/data layers.
4. Implement the smallest coherent change.
5. Add tests.
6. Check light/dark theme behavior and accessibility for UI changes.
7. Avoid unrelated refactoring.

When requirements are ambiguous, follow repository conventions and the planning documents in `docs/` rather than inventing a new architecture.

## Finance App Visual System
- Use a sober, professional financial aesthetic based primarily on deep navy blue, slate blue, white, and cool gray. Avoid gradients, excessive shadows, saturated colors, neon colors, and decorative visual effects. Use generous whitespace, clear typography, subtle borders, restrained corner radii, and strong information hierarchy.
- Primary colors must use the defined navy/blue scale. Gray should be used for backgrounds, borders, secondary information, and disabled states. Green and red are reserved exclusively for financial performance and semantic status.
- All colors must be represented using HEX notation in Kotlin Compose format: 0xFFRRGGBB.
- Prefer semantic color names such as Primary, Background, Surface, TextPrimary, TextSecondary, Positive, and Negative rather than screen-specific color names.
- The UI should feel appropriate for an investment platform, wealth-management application, brokerage application, or professional financial dashboard.
- **Typography & Labels:** Ensure text layouts account for text wrapping and dynamic string bounds. Avoid layout truncation or word splitting (e.g., ensure full words like "Categories" display on a single line without breaking into separate trailing fragments like "categorie / s").

## 💰 Monetization & Feature Gating Rules
The app enforces a Freemium business model. When adding or modifying repositories or ViewModels, intercept actions inside the shared layer (`commonMain`) and apply these exact limits for non-premium users:

1. **Import & Reconcile:**
   - *Free Tier:* Manual entry only. Block automated statement parsing and batch reconciliation confirmations if user is not premium.
2. **Projects Engine:**
   - *Free Tier:* Max 1 active project at a time (e.g., "Home renovation"). Return `LimitReached` on repository if a free user attempts to create a second one.
3. **Accounts & Credit Portfolio:**
   - *Free Tier:* Max 2 registered accounts. Limit advanced credit card utilization tracking and portfolio overview dashboard aggregates.
4. **Data Export:**
   - *Free Tier:* Intercept click events on "Export data" and "Open Export" buttons. If `isPremiumUser` evaluates to false, dispatch a navigation route event to display the subscription paywall view instead of executing the CSV/Excel processing loop.

