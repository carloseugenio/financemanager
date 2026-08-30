---
applyTo: "**/*.kt,**/*.kts,**/libs.versions.toml"
---
# Kotlin / KMP / Compose-specific instructions

Use idiomatic modern Kotlin and preserve Kotlin Multiplatform compatibility.

For Compose UI:
- Use JetBrains Compose Multiplatform Material 3.
- Prefer Material 3 components before creating custom equivalents.
- Hoist state from Composables.
- Keep Composables deterministic and free of business logic.
- Use semantic theme colors from the project design system.
- Use `Color(0xFFRRGGBB)` for explicit color constants.
- Avoid platform-specific APIs in common code.

For Room:
- Keep `@Entity`, `@Dao`, and database concerns in the data layer.
- Do not expose Room entities to presentation code.
- Prefer Flow for observable database queries where appropriate.

For Koin:
- Prefer constructor injection.
- Register dependencies in the project's Koin module structure.
- Do not instantiate repositories or ViewModels manually when Koin is already responsible for them.

For financial values:
- Never introduce `Double`/`Float` for exact money calculations.
- Preserve currency context.
- Put calculations in domain/use-case code and test them.
