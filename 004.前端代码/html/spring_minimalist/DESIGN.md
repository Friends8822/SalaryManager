---
name: Spring Minimalist
colors:
  surface: '#edfee5'
  surface-dim: '#cedfc7'
  surface-bright: '#edfee5'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#e7f9e0'
  surface-container: '#e2f3da'
  surface-container-high: '#dcedd5'
  surface-container-highest: '#d6e8cf'
  on-surface: '#111f10'
  on-surface-variant: '#41493a'
  inverse-surface: '#263423'
  inverse-on-surface: '#e5f6dd'
  outline: '#717a68'
  outline-variant: '#c1cab5'
  surface-tint: '#2e6c00'
  primary: '#2e6c00'
  on-primary: '#ffffff'
  primary-container: '#7ec850'
  on-primary-container: '#215100'
  inverse-primary: '#8eda5f'
  secondary: '#556252'
  on-secondary: '#ffffff'
  secondary-container: '#d8e7d2'
  on-secondary-container: '#5a6858'
  tertiary: '#006a65'
  on-tertiary: '#ffffff'
  tertiary-container: '#48c8bf'
  on-tertiary-container: '#00504c'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#a9f778'
  primary-fixed-dim: '#8eda5f'
  on-primary-fixed: '#092100'
  on-primary-fixed-variant: '#215100'
  secondary-fixed: '#d8e7d2'
  secondary-fixed-dim: '#bccbb7'
  on-secondary-fixed: '#131e12'
  on-secondary-fixed-variant: '#3d4a3b'
  tertiary-fixed: '#7cf6ec'
  tertiary-fixed-dim: '#5dd9d0'
  on-tertiary-fixed: '#00201e'
  on-tertiary-fixed-variant: '#00504c'
  background: '#edfee5'
  on-background: '#111f10'
  surface-variant: '#d6e8cf'
  bg-light: '#F7FBF4'
  bg-dark: '#1A1E19'
  card-bg-light: '#EDF6E5'
  card-bg-dark: '#252B23'
  brand-primary-dark: '#8ED860'
  category-shopping: '#FFD93D'
  category-housing: '#6C5CE7'
  status-error: '#FF8B94'
  category-fun: '#A8E6CF'
typography:
  display-amount:
    fontFamily: Hanken Grotesk
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  display-amount-mobile:
    fontFamily: Hanken Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Hanken Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-md:
    fontFamily: Hanken Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-sm:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  container-margin: 20px
  gutter: 12px
  touch-target: 44px
  card-padding: 16px
  section-gap: 24px
---

## Brand & Style

The design system embodies a **"Spring Minimalist"** aesthetic, specifically tailored for a personal finance context to transform the chore of bookkeeping into a refreshing, nurturing habit. The brand personality is calm, efficient, and approachable—moving away from the cold, spreadsheet-like nature of traditional finance apps.

The visual style is a blend of **Minimalism** and **Modern Corporate**, utilizing heavy whitespace and a "Less is More" philosophy to reduce cognitive load. It leverages soft tonal layering and a nature-inspired palette to evoke feelings of growth (the "Sprout" metaphor) and financial clarity. High-priority information is delivered through large typography and simple geometric shapes, ensuring that the most critical data is understood at a glance.

**Key visual principles:**
- **Clarity over Density:** Generous margins and breathing room between financial records.
- **Speed-First Interaction:** Primary touch targets are concentrated in the bottom half of the screen for ergonomic, single-handed use.
- **Emotional Resonance:** Use of soft greens and hand-drawn accents to lower "financial anxiety."

## Colors

The palette is anchored by **"Spring Green" (#7EC850)**, representing growth and positive financial health. This is balanced by **"Deep Moss Gray" (#3D4A3B)** for high-contrast typography, ensuring legibility without the harshness of pure black.

- **Primary Action:** Used for the Floating Action Button (FAB), active states, and success indicators.
- **Surface Strategy:** Backgrounds use a "Soft Eye-Protection" off-white (#F7FBF4) to reduce glare. Components and cards use a slightly more saturated tint (#EDF6E5) to create subtle containment.
- **Semantic Accents:** A secondary palette of muted pastels is reserved for category identification (e.g., Shopping, Housing) and status warnings.
- **Dark Mode:** In dark mode, the primary green shifts to a more luminous "Dark Mode Primary" (#8ED860) to maintain WCAG accessibility against the deep charcoal (#1A1E19) background.

## Typography

This design system uses **Hanken Grotesk** across all levels. It is a clean, sharp, and contemporary typeface that provides the professional precision required for financial data while remaining approachable and modern.

The hierarchy is dominated by the **Display Amount** style, designed to make the monthly total the undeniable focal point of the dashboard. For accessibility, large display sizes are capped at 32px on mobile devices. Weights are used strategically: **SemiBold/Bold** for headers and monetary values to ensure they are the first things a user sees, and **Regular** for secondary metadata like dates and category labels to maintain a clean, uncluttered interface.

## Layout & Spacing

The layout follows a **Fluid Grid** model optimized for mobile-first interaction. Content is organized into a single-column stack on mobile, expanding to a maximum width container on larger screens.

- **The 44px Rule:** All interactive elements (buttons, list items, category icons) adhere to a minimum 44px touch target to ensure speed and accuracy during single-handed operation.
- **Rhythm:** A 4px base unit governs all spacing. Use `16px` (base * 4) for standard internal card padding and `20px` for screen margins to create a sense of openness.
- **The "Bottom-Heavy" Model:** The layout logic prioritizes the lower 50% of the screen. Input sheets, numeric keypads, and the Floating Action Button are positioned within the "natural thumb zone."

## Elevation & Depth

Hierarchy is established through **Tonal Layers** rather than traditional shadows. This maintains the "Minimalist" aesthetic and keeps the interface feeling light and "spring-like."

- **Level 0 (Background):** The base `#F7FBF4` surface.
- **Level 1 (Cards):** Use a subtle fill change (`#EDF6E5`) and a very soft, low-opacity 2px stroke rather than a shadow. This clearly defines areas like the "Monthly Summary" without adding visual weight.
- **Level 2 (Overlays):** Bottom sheets and modals use a soft, ultra-diffused shadow (15% opacity of the brand secondary color) to suggest they are floating above the main surface.
- **Transitions:** Depth is also communicated through motion. Modals slide up from the bottom, reinforcing the physical metaphor of a drawer being pulled over the content.

## Shapes

The shape language is **Rounded**, utilizing a `0.5rem (8px)` base radius for standard components. This softened geometry complements the "Spring" theme and makes the financial data feel less rigid.

- **Primary Buttons & Cards:** Follow the standard `rounded-md` (8px) for a balanced, modern look.
- **Progress Bars & Category Tags:** Use `rounded-xl` (24px) or full pill shapes to emphasize their status as decorative or secondary indicators.
- **Input Fields:** Use the standard 8px radius to maintain consistency with buttons, creating a unified interactive language.

## Components

### Buttons
Primary buttons use the brand-primary green with white or dark-moss text. They feature a subtle "press" animation (95% scale) to provide tactile feedback.

### Large Number Display
The "Hero Value" on the dashboard should be rendered in `display-amount`. Use a slightly lighter weight for the currency symbol (e.g., ¥ or $) to keep the focus on the integer.

### Category Roundels
Icons are housed in circular containers with a 10% opacity background of the category's specific color. Selected states are indicated by a 2px solid border in the brand-primary color.

### Progress Bars
Budget bars are thick (8px height) with fully rounded caps. They transition from `brand-primary` to `status-error` as the user approaches their budget limit.

### Concise List Items
Records are grouped by date. Each item includes a category roundel, a title, and the amount. Amounts are right-aligned; expenses are prepended with a minus sign, while income (if applicable) uses a plus sign in brand-primary.

### Charts
Ring charts (Donuts) should use a thick stroke (12-16px) with rounded ends on the segments. Central white space is used to display the "Total" or "Remaining" amount in `headline-md`.