# Finance KMP — Copilot Project Package

This package contains the repository-level Copilot configuration, finance UI skill, updated product plan, roadmap continuation, visual specification, and palette graphic.

## Files

- `.github/copilot-instructions.md` — always-on repository rules.
- `.github/instructions/kmp-compose.instructions.md` — Kotlin/KMP/Compose path-specific rules.
- `.github/skills/finance-ui/SKILL.md` — task-specific finance UI/design skill.
- `docs/copilot-setup.md` — installation and usage instructions.
- `docs/plan-v2.md` — updated version of the previous plan.
- `docs/roadmap-next.md` — continuation roadmap.
- `design/finance-ui-visual-spec.md` — visual source of truth.
- `design/finance-palette.svg` — palette reference graphic.

## Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`

## Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- Desktop tests: `./gradlew :shared:jvmTest`
