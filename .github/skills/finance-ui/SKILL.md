---
name: finance-ui
description: Design and implement sober finance/investment UI for this Kotlin Multiplatform application using JetBrains Compose Material 3, the project navy/blue/gray palette, accessible financial charts, purposeful graphics, and reusable components. Use this skill for new screens, UI redesigns, charts, visual assets, and design-system work.
---

# Finance UI Skill

## Goal
Create a calm, professional investment/finance interface. The visual hierarchy should communicate trust, precision, and financial clarity rather than gamification.

## Color tokens
Use these exact HEX values unless the design specification explicitly adds a new semantic token.

### Light
- Primary/Navy900: #0B1F33
- PrimaryContainer/Navy800: #123A5A
- Secondary/Navy700: #1F5A82
- Accent/Blue600: #2F80C0
- Background/Gray50: #F5F7FA
- Surface/GrayWhite: #FFFFFF
- SurfaceVariant/Gray100: #EEF2F5
- Outline/Gray200: #D7E0E7
- Disabled/Gray400: #9AA7B2
- TextSecondary/Gray500: #5B6B78
- TextPrimary/Gray900: #172B3A
- Positive: #287A5A
- Negative: #B54848
- Warning: #A87519

### Dark
- Background: #08131F
- Surface: #0D1D2C
- ElevatedSurface: #13283A
- Primary: #4A91C5
- PrimaryLight: #73B1D8
- TextPrimary: #E8EEF3
- TextSecondary: #AAB9C5
- Border: #263D50
- Positive: #4DA67D
- Negative: #D06A6A
- Warning: #D0A34A

In Kotlin use `Color(0xFFRRGGBB)`.

## Layout
Prefer:
- clear alignment
- 8dp-based spacing
- restrained corner radii
- subtle 1dp borders
- limited elevation
- strong typography hierarchy
- compact but readable financial tables

Avoid:
- gradients
- neon colors
- excessive shadows
- excessive pill-shaped controls
- decorative illustrations that compete with financial data

## Reusable components
Prefer or extend components such as:
- `FinanceScaffold`
- `MetricCard`
- `PortfolioCard`
- `AccountSummaryCard`
- `PerformanceIndicator`
- `MoneyText`
- `PercentageText`
- `AssetRow`
- `TransactionRow`
- `AllocationChart`
- `PerformanceChart`
- `EmptyState`
- `ErrorState`

Do not create a new component if an equivalent project component exists.

## Financial graphics
Use graphics to explain data:
- donut charts for allocation/category composition
- line/area charts for portfolio performance
- bar charts for budgeted vs actual
- sparklines for compact asset performance
- progress indicators for budget utilization

Charts must remain readable without relying only on color. Include labels, legends, icons, or text indicators as appropriate.

Avoid misleading axis truncation and unnecessary 3D effects.

## Image guidance
When generating or requesting images, use:
- dark navy, slate blue, cool gray, and white
- subtle blue highlights
- professional investment/wealth-management context
- clean editorial or premium product-photography style where photography is appropriate
- abstract financial graphics for dashboards

Avoid:
- cryptocurrency clichés unless the feature is specifically crypto-related
- piles of cash, gold coins, generic Wall Street imagery
- excessive green/red imagery
- generic smiling-business-team stock photos

## Screen composition
A typical finance screen should follow:
1. concise title and period/context
2. primary financial metric
3. supporting metrics
4. visual analysis/chart
5. detailed data
6. primary action

Do not overload the first viewport. Prioritize the user's financial decision or question.

## Accessibility
Do not encode gain/loss solely with color. Pair color with sign, icon, label, or text.
Maintain accessible contrast and meaningful semantics.

## Visual prompts
When an external image-generation model is used, prefer prompts such as:

"Premium investment application visual, sober institutional fintech aesthetic, deep navy #0B1F33, slate blue #1F5A82, accent blue #2F80C0, cool gray #F5F7FA, clean financial data visualization, restrained composition, subtle lighting, no gradients, no neon colors, professional wealth-management product design."

For dashboard graphics:

"Abstract financial portfolio visualization, navy and slate-blue data lines, cool gray background, precise geometric chart elements, minimal editorial fintech aesthetic, high legibility, no decorative clutter, suitable for a professional investment application."
