# GitHub Copilot Custom Project Instructions

## Tech Stack & Architecture
- Always use **Kotlin** with modern Jetpack Compose for UI elements.
- Follow clean architecture principles (Separation of concerns between UI, ViewModel, and Data layers).
- Use standard Gradle Kotlin DSL (`.gradle.kts`) for build files.

## Coding Conventions
- Prefer writing immutable variables (`val`) over mutable variables (`var`).
- Ensure all function parameters are explicitly typed.
- Provide descriptive variable names following lowerCamelCase styling guidelines.

## Error Handling & Logging
- Always wrap asynchronous or network database calls inside clean `try-catch` exception blocks.
- Use Android `Log.d` or `Log.e` parameters for debugging tracking logs, rather than raw `println()`.

## Documentation Rules
- Write KDoc comments above all newly generated public functions or abstract repository interfaces.

Finance App Visual System

Use a sober, professional financial aesthetic based primarily on deep navy blue, slate blue, white, and cool gray. Avoid gradients, excessive shadows, saturated colors, neon colors, and decorative visual effects. Use generous whitespace, clear typography, subtle borders, restrained corner radii, and strong information hierarchy.

Primary colors must use the defined navy/blue scale. Gray should be used for backgrounds, borders, secondary information, and disabled states. Green and red are reserved exclusively for financial performance and semantic status.

All colors must be represented using HEX notation in Kotlin Compose format: 0xFFRRGGBB.

Prefer semantic color names such as Primary, Background, Surface, TextPrimary, TextSecondary, Positive, and Negative rather than screen-specific color names.

The UI should feel appropriate for an investment platform, wealth-management application, brokerage application, or professional financial dashboard.
