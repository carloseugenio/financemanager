# Copilot Project Setup

This project uses GitHub Copilot repository instructions plus a task-specific finance UI skill.

## Recommended repository structure

```text
.github/
├── copilot-instructions.md
├── instructions/
│   └── kmp-compose.instructions.md
└── skills/
    └── finance-ui/
        └── SKILL.md

docs/
├── plan-v2.md
├── roadmap-next.md
└── copilot-setup.md

design/
└── finance-ui-visual-spec.md
```

GitHub documents `.github/copilot-instructions.md` as the repository-wide instruction mechanism. Path-specific instructions use `.github/instructions/**/*.instructions.md`. Agent skills use a `SKILL.md` in a skill directory and are loaded when Copilot determines that the task is relevant. These mechanisms can be used together. See the official documentation: https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/customize-cloud-agent/add-skills and https://docs.github.com/en/copilot/how-tos/configure-custom-instructions-in-your-ide/add-repository-instructions-in-your-ide.

## JetBrains IDE setup

The project is compatible with JetBrains IDEs with GitHub Copilot enabled.

1. Open the repository as the IDE workspace.
2. Ensure the current GitHub Copilot plugin is installed and authenticated.
3. Keep `.github/copilot-instructions.md` at the repository root.
4. In JetBrains, open Settings → Tools → GitHub Copilot → Customizations.
5. Confirm the workspace/repository custom instructions are enabled.
6. Keep the `.github/instructions/` and `.github/skills/` directories committed to Git.
7. Restart/reload the workspace if the IDE does not immediately recognize newly added customizations.

## VS Code / Copilot agent mode

The same repository files can be used from VS Code. Repository instructions are automatically considered by Copilot when enabled. Agent skills are supported by Copilot agent mode.

## How the files work together

### 1. `copilot-instructions.md`
Always-on project context. Keep architecture, security, financial correctness, testing, and global visual rules here.

### 2. `kmp-compose.instructions.md`
Path-specific Kotlin/KMP/Compose guidance. It applies to Kotlin, Gradle Kotlin DSL, and version-catalog files.

### 3. `finance-ui/SKILL.md`
Task-specific visual workflow. Copilot should use it for new screens, redesigns, charts, visual components, and image/graphic work.

### 4. `docs/plan-v2.md`
Product and technical baseline. Use it to understand the current application and completed phases.

### 5. `docs/roadmap-next.md`
Forward-looking implementation plan. Use this file to determine the next coherent feature when the user asks to continue the project.

### 6. `design/finance-ui-visual-spec.md`
Visual source of truth for colors, typography, spacing, charts, imagery, and screen composition.

## Recommended Copilot prompt pattern

For a feature, ask Copilot to:

```text
Implement the next roadmap item from docs/roadmap-next.md.
First inspect the existing implementation and follow the repository architecture.
Use Room for persistence, Koin for dependency injection, and JetBrains Compose Material 3 for UI.
For UI work, apply the finance-ui skill and design/finance-ui-visual-spec.md.
Implement the smallest coherent change, add tests, and update the roadmap status when the item is complete.
```

## Verification

After adding or changing instructions, ask Copilot to summarize which repository instructions apply to the current file. In supported Copilot interfaces, the References/context area can also be inspected to verify that `.github/copilot-instructions.md` was included.

Do not duplicate large instruction sets across files. Keep always-on rules concise and put specialized workflows in skills.
