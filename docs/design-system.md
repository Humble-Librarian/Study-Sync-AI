# Study-Sync AI - Design System

## Brand Identity
- **Name:** Study-Sync AI
- **Tagline:** "Learn Smarter with AI"
- **Personality:** Professional, modern, approachable, intelligent

---

## Color Palette

### Primary Colors
| Name           | Hex       | Usage                    |
|----------------|-----------|--------------------------|
| Primary        | `#2563eb` | CTAs, active states      |
| Primary Hover  | `#1d4ed8` | Button hover             |
| Primary Light  | `#dbeafe` | Backgrounds, highlights  |

### Semantic Colors
| Name           | Hex       | Usage                    |
|----------------|-----------|--------------------------|
| Success        | `#10b981` | Indexed, completed       |
| Success Light  | `#d1fae5` | Success badges           |
| Warning        | `#f59e0b` | Indexing, in-progress    |
| Warning Light  | `#fef3c7` | Warning badges           |
| Danger         | `#ef4444` | Failed, errors           |
| Danger Light   | `#fee2e2` | Error badges             |
| Info           | `#06b6d4` | Viva mode                |
| Info Light     | `#cffafe` | Info backgrounds         |

### Neutral Colors
| Name           | Hex       | Usage                    |
|----------------|-----------|--------------------------|
| Gray 50        | `#f9fafb` | Page backgrounds         |
| Gray 100       | `#f3f4f6` | Card backgrounds         |
| Gray 500       | `#6b7280` | Muted text               |
| Gray 900       | `#111827` | Primary text             |

---

## Typography

### Font Stack
```css
--font-sans: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
--font-mono: 'SFMono-Regular', Consolas, monospace;
```

### Font Sizes
| Name    | Size      | Usage                    |
|---------|-----------|--------------------------|
| xs      | 0.75rem   | Captions, badges         |
| sm      | 0.875rem  | Secondary text           |
| base    | 1rem      | Body text                |
| lg      | 1.125rem  | Subheadings              |
| xl      | 1.25rem   | Card titles              |
| 2xl     | 1.5rem    | Page titles              |
| 3xl     | 1.875rem  | Hero headings            |

---

## Spacing System (4px base)
| Token   | Value     | Usage                    |
|---------|-----------|--------------------------|
| space-1 | 0.25rem   | Tight spacing            |
| space-2 | 0.5rem    | Small gaps               |
| space-4 | 1rem      | Standard padding         |
| space-6 | 1.5rem    | Card padding             |
| space-8 | 2rem      | Section gaps             |

---

## Border Radius
| Token      | Value     | Usage                    |
|------------|-----------|--------------------------|
| radius-sm  | 0.25rem   | Small buttons            |
| radius-md  | 0.375rem  | Inputs, badges           |
| radius-lg  | 0.5rem    | Cards                    |
| radius-xl  | 0.75rem   | Modals                   |
| radius-full| 9999px    | Pills, avatars           |

---

## Shadows
| Token      | Value                              | Usage           |
|------------|------------------------------------| ----------------|
| shadow-sm  | 0 1px 2px 0 rgb(0 0 0 / 0.05)      | Subtle elevation|
| shadow-md  | 0 4px 6px -1px rgb(0 0 0 / 0.1)   | Cards           |
| shadow-lg  | 0 10px 15px -3px rgb(0 0 0 / 0.1) | Dropdowns       |
