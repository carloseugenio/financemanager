
# GitHub Copilot Custom Project Instructions

## Tech Stack & Architecture
- Always use **Kotlin** with modern Jetpack Compose for UI elements (Compose Multiplatform patterns).
- Follow clean architecture principles (Separation of concerns between UI, ViewModel, and Data layers).
- Use standard Gradle Kotlin DSL (`.gradle.kts`) for build files.
- All core business logic, math calculations, currency formatting (R$), and data parsing must reside strictly in the `:shared:commonMain` module.
- Keep the ViewModels/Presenters shared across targets to enforce identical behavioral logic.

## Coding Conventions
- Prefer writing immutable variables (`val`) over mutable variables (`var`).
- Ensure all function parameters are explicitly typed.
- Provide descriptive variable names following lowerCamelCase styling guidelines.

## Error Handling & Logging
- Always wrap asynchronous or network database calls inside clean `try-catch` exception blocks.
- Use Android `Log.d` or `Log.e` parameters for debugging tracking logs, rather than raw `println()`.

## Documentation Rules
- Write KDoc comments above all newly generated public functions or abstract repository interfaces.

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
