# Study-Sync AI - Frontend Technical Design Document

**Version:** 1.0  
**Last Updated:** January 25, 2026  
**Document Owner:** Frontend Team Lead  
**Status:** Approved for Development

---

## 📚 Table of Contents

1. [Document Purpose](#document-purpose)
2. [Design System](#design-system)
3. [Page Layouts & Wireframes](#page-layouts--wireframes)
4. [Component Library](#component-library)
5. [Technical Architecture](#technical-architecture)
6. [Implementation Specifications](#implementation-specifications)
7. [Animation & Interaction Design](#animation--interaction-design)
8. [Responsive Design Strategy](#responsive-design-strategy)
9. [Accessibility Patterns](#accessibility-patterns)
10. [Code Conventions](#code-conventions)
11. [File Organization](#file-organization)
12. [Appendix: Code Examples](#appendix-code-examples)

---

## 1. Document Purpose

### 1.1 Overview

This Technical Design Document (TDD) provides detailed specifications for implementing the Study-Sync AI frontend. It serves as the single source of truth for:

- Visual design system (colors, typography, spacing)
- Component architecture and reusability patterns
- JSF managed bean structure and scoping
- CSS and JavaScript conventions
- Responsive breakpoints and mobile-first approach
- Accessibility implementation details

### 1.2 Audience

- **Frontend Developers**: Implementation reference
- **UI/UX Designers**: Design system compliance
- **QA Engineers**: Testing specifications
- **Backend Developers**: Understanding frontend constraints

### 1.3 Related Documents

- **Frontend Requirements Specification**: What we're building (requirements)
- **GUIDELINES.md**: How to set up and run the project
- **Integration Contracts**: API specifications for backend handoff

---

## 2. Design System

### 2.1 Brand Identity

**Application Name:** Study-Sync AI  
**Tagline:** "Learn Smarter with AI"  
**Personality:** Professional, modern, approachable, intelligent

**Design Principles:**
1. **Clarity over complexity** - Information hierarchy should be obvious
2. **Consistency** - Same patterns solve same problems
3. **Feedback** - Every action gets immediate visual response
4. **Accessibility first** - Design for all users from the start

---

### 2.2 Color Palette

#### Primary Colors

```css
:root {
    /* Primary - Used for CTAs, active states */
    --primary: #2563eb;           /* Blue 600 */
    --primary-hover: #1d4ed8;     /* Blue 700 */
    --primary-light: #dbeafe;     /* Blue 100 */
    --primary-dark: #1e40af;      /* Blue 800 */
    
    /* Secondary - Used for less important actions */
    --secondary: #64748b;         /* Slate 500 */
    --secondary-hover: #475569;   /* Slate 600 */
    --secondary-light: #f1f5f9;   /* Slate 100 */
    
    /* Accent - Used for highlights, badges */
    --accent: #8b5cf6;            /* Violet 500 */
    --accent-light: #ede9fe;      /* Violet 100 */
}
```

#### Semantic Colors

```css
:root {
    /* Status colors */
    --success: #10b981;           /* Green 500 - indexed, completed */
    --success-light: #d1fae5;     /* Green 100 */
    
    --warning: #f59e0b;           /* Amber 500 - indexing, in-progress */
    --warning-light: #fef3c7;     /* Amber 100 */
    
    --danger: #ef4444;            /* Red 500 - failed, errors */
    --danger-light: #fee2e2;      /* Red 100 */
    
    --info: #06b6d4;              /* Cyan 500 - viva mode, notifications */
    --info-light: #cffafe;        /* Cyan 100 */
}
```

#### Neutral Colors

```css
:root {
    /* Grays for text and backgrounds */
    --gray-50: #f9fafb;
    --gray-100: #f3f4f6;
    --gray-200: #e5e7eb;
    --gray-300: #d1d5db;
    --gray-400: #9ca3af;
    --gray-500: #6b7280;
    --gray-600: #4b5563;
    --gray-700: #374151;
    --gray-800: #1f2937;
    --gray-900: #111827;
    
    /* Semantic neutrals */
    --text-primary: var(--gray-900);
    --text-secondary: var(--gray-600);
    --text-muted: var(--gray-500);
    
    --border-light: var(--gray-200);
    --border-default: var(--gray-300);
    
    --bg-primary: #ffffff;
    --bg-secondary: var(--gray-50);
    --bg-tertiary: var(--gray-100);
}
```

#### Chat-Specific Colors

```css
:root {
    /* User message bubble */
    --chat-user-bg: var(--primary);
    --chat-user-text: #ffffff;
    
    /* AI message bubble */
    --chat-ai-bg: var(--gray-100);
    --chat-ai-text: var(--gray-900);
    
    /* Viva mode accent */
    --viva-accent: var(--info);
    --viva-bg: var(--info-light);
}
```

#### Usage Examples

```css
/* Primary button */
.btn-primary {
    background-color: var(--primary);
    color: white;
}

.btn-primary:hover {
    background-color: var(--primary-hover);
}

/* Status badge */
.badge-success {
    background-color: var(--success-light);
    color: var(--success);
}
```

---

### 2.3 Typography

#### Font Stack

```css
:root {
    /* Primary font (UI, body text) */
    --font-sans: -apple-system, BlinkMacSystemFont, 'Segoe UI', 
                 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 
                 'Fira Sans', 'Droid Sans', 'Helvetica Neue', 
                 sans-serif;
    
    /* Monospace (code snippets, technical content) */
    --font-mono: 'SFMono-Regular', 'Consolas', 'Liberation Mono', 
                 'Menlo', 'Courier', monospace;
}

body {
    font-family: var(--font-sans);
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}
```

#### Type Scale

```css
:root {
    /* Font sizes (using 1rem = 16px base) */
    --text-xs: 0.75rem;     /* 12px - tiny labels */
    --text-sm: 0.875rem;    /* 14px - small text, captions */
    --text-base: 1rem;      /* 16px - body text */
    --text-lg: 1.125rem;    /* 18px - large body */
    --text-xl: 1.25rem;     /* 20px - small headings */
    --text-2xl: 1.5rem;     /* 24px - section headings */
    --text-3xl: 1.875rem;   /* 30px - page titles */
    --text-4xl: 2.25rem;    /* 36px - hero text */
    
    /* Font weights */
    --font-normal: 400;
    --font-medium: 500;
    --font-semibold: 600;
    --font-bold: 700;
    
    /* Line heights */
    --leading-tight: 1.25;
    --leading-normal: 1.5;
    --leading-relaxed: 1.75;
}
```

#### Heading Styles

```css
h1 {
    font-size: var(--text-3xl);
    font-weight: var(--font-bold);
    line-height: var(--leading-tight);
    color: var(--text-primary);
    margin-bottom: 1.5rem;
}

h2 {
    font-size: var(--text-2xl);
    font-weight: var(--font-semibold);
    line-height: var(--leading-tight);
    color: var(--text-primary);
    margin-bottom: 1rem;
}

h3 {
    font-size: var(--text-xl);
    font-weight: var(--font-semibold);
    line-height: var(--leading-normal);
    color: var(--text-primary);
    margin-bottom: 0.75rem;
}

h4 {
    font-size: var(--text-lg);
    font-weight: var(--font-medium);
    line-height: var(--leading-normal);
    color: var(--text-secondary);
    margin-bottom: 0.5rem;
}
```

#### Body Text

```css
body {
    font-size: var(--text-base);
    font-weight: var(--font-normal);
    line-height: var(--leading-relaxed);
    color: var(--text-primary);
}

p {
    margin-bottom: 1rem;
}

.text-secondary {
    color: var(--text-secondary);
}

.text-muted {
    color: var(--text-muted);
}
```

---

### 2.4 Spacing System

Using a **4px base unit** for consistent spacing:

```css
:root {
    /* Spacing scale (multiples of 4px) */
    --space-1: 0.25rem;   /* 4px */
    --space-2: 0.5rem;    /* 8px */
    --space-3: 0.75rem;   /* 12px */
    --space-4: 1rem;      /* 16px */
    --space-5: 1.25rem;   /* 20px */
    --space-6: 1.5rem;    /* 24px */
    --space-8: 2rem;      /* 32px */
    --space-10: 2.5rem;   /* 40px */
    --space-12: 3rem;     /* 48px */
    --space-16: 4rem;     /* 64px */
    --space-20: 5rem;     /* 80px */
}
```

#### Spacing Usage Guidelines

| Element | Spacing | Use Case |
|---------|---------|----------|
| **Inline elements** | `--space-2` to `--space-3` | Icon-to-text, button padding |
| **Component gaps** | `--space-4` to `--space-6` | Card padding, form fields |
| **Section spacing** | `--space-8` to `--space-12` | Between page sections |
| **Page margins** | `--space-12` to `--space-16` | Container padding |

```css
/* Example usage */
.card {
    padding: var(--space-6);
    margin-bottom: var(--space-4);
}

.section {
    margin-bottom: var(--space-12);
}

.btn {
    padding: var(--space-3) var(--space-6);
}
```

---

### 2.5 Border Radius

```css
:root {
    --radius-sm: 0.25rem;   /* 4px - badges, small buttons */
    --radius-md: 0.375rem;  /* 6px - default buttons, inputs */
    --radius-lg: 0.5rem;    /* 8px - cards, modals */
    --radius-xl: 0.75rem;   /* 12px - large cards */
    --radius-2xl: 1rem;     /* 16px - flashcards */
    --radius-full: 9999px;  /* Fully rounded (pills) */
}
```

---

### 2.6 Shadows

```css
:root {
    /* Elevation shadows */
    --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
    --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 
                 0 2px 4px -2px rgb(0 0 0 / 0.1);
    --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 
                 0 4px 6px -4px rgb(0 0 0 / 0.1);
    --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 
                 0 8px 10px -6px rgb(0 0 0 / 0.1);
    
    /* Focus shadows */
    --shadow-focus: 0 0 0 3px rgba(37, 99, 235, 0.2);
}
```

**Usage:**
- `--shadow-sm`: Buttons, badges
- `--shadow-md`: Cards, dropdowns
- `--shadow-lg`: Modals, popovers
- `--shadow-xl`: Flashcards (3D effect)

---

### 2.7 Transitions

```css
:root {
    /* Transition durations */
    --duration-fast: 150ms;
    --duration-normal: 300ms;
    --duration-slow: 500ms;
    
    /* Easing functions */
    --ease-in: cubic-bezier(0.4, 0, 1, 1);
    --ease-out: cubic-bezier(0, 0, 0.2, 1);
    --ease-in-out: cubic-bezier(0.4, 0, 0.2, 1);
}
```

**Standard Transition:**
```css
.btn {
    transition: all var(--duration-fast) var(--ease-in-out);
}

.flashcard-inner {
    transition: transform var(--duration-normal) var(--ease-in-out);
}
```

---

## 3. Page Layouts & Wireframes

### 3.1 Global Layout Structure

All pages use a consistent layout template:

```
┌─────────────────────────────────────────┐
│           Header / Topbar               │ ← 64px height
├─────────────────────────────────────────┤
│                                         │
│                                         │
│          Main Content Area              │
│          (Page-specific)                │
│                                         │
│                                         │
├─────────────────────────────────────────┤
│           Footer (Optional)             │ ← 48px height
└─────────────────────────────────────────┘
```

**Layout Template File:** `src/main/webapp/WEB-INF/templates/layout.xhtml`

```xml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets">
<h:head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title><ui:insert name="title">Study-Sync AI</ui:insert></title>
    
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" 
          rel="stylesheet"/>
    
    <!-- Custom CSS -->
    <h:outputStylesheet name="css/styles.css"/>
    <ui:insert name="page-css"/>
</h:head>

<h:body>
    <div class="app-container">
        <!-- Header -->
        <ui:insert name="header">
            <ui:include src="/WEB-INF/templates/header.xhtml"/>
        </ui:insert>
        
        <!-- Main Content -->
        <main class="main-content">
            <div class="container-fluid">
                <ui:insert name="content">
                    <!-- Page content goes here -->
                </ui:insert>
            </div>
        </main>
        
        <!-- Footer -->
        <ui:insert name="footer">
            <ui:include src="/WEB-INF/templates/footer.xhtml"/>
        </ui:insert>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JS -->
    <h:outputScript name="js/ui-utils.js"/>
    <ui:insert name="page-js"/>
</h:body>
</html>
```

---

### 3.2 Header Component

**Visual Design:**

```
┌─────────────────────────────────────────────────────────┐
│  [Logo] Study-Sync AI    [Subject ▼]  [User] [Logout]  │
└─────────────────────────────────────────────────────────┘
```

**Specifications:**
- Height: 64px
- Background: `var(--bg-primary)` with bottom border `var(--border-default)`
- Logo: 32px height
- Subject dropdown: Right-aligned on mobile, centered on tablet+
- User info: Always right-aligned

**File:** `src/main/webapp/WEB-INF/templates/header.xhtml`

```xml
<header class="app-header">
    <div class="container-fluid">
        <div class="row align-items-center">
            <!-- Logo -->
            <div class="col-auto">
                <h:link outcome="/pages/dashboard.xhtml" styleClass="logo-link">
                    <h:graphicImage name="images/logo.png" alt="Study-Sync AI" 
                                    height="32"/>
                    <span class="logo-text d-none d-md-inline">Study-Sync AI</span>
                </h:link>
            </div>
            
            <!-- Subject Dropdown (if logged in) -->
            <div class="col">
                <h:form rendered="#{userSession.loggedIn}">
                    <h:selectOneMenu id="subjectSelector" 
                                     value="#{dashboardBean.selectedSubjectId}"
                                     styleClass="form-select form-select-sm w-auto">
                        <f:selectItem itemLabel="All Subjects" itemValue="#{null}"/>
                        <f:selectItems value="#{dashboardBean.subjects}" 
                                       var="subject"
                                       itemLabel="#{subject.name}" 
                                       itemValue="#{subject.id}"/>
                        <f:ajax listener="#{dashboardBean.onSubjectChange}" 
                                render="documentList"/>
                    </h:selectOneMenu>
                </h:form>
            </div>
            
            <!-- User Menu -->
            <div class="col-auto">
                <h:panelGroup rendered="#{userSession.loggedIn}">
                    <span class="user-name">#{userSession.username}</span>
                    <h:commandButton value="Logout" 
                                     action="#{loginBean.logout}"
                                     styleClass="btn btn-sm btn-outline-secondary ms-2"/>
                </h:panelGroup>
            </div>
        </div>
    </div>
</header>
```

**CSS:**

```css
.app-header {
    height: 64px;
    background-color: var(--bg-primary);
    border-bottom: 1px solid var(--border-default);
    padding: 0 var(--space-4);
    display: flex;
    align-items: center;
}

.logo-link {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    text-decoration: none;
    color: var(--text-primary);
    font-weight: var(--font-semibold);
    font-size: var(--text-lg);
}

.logo-link:hover {
    color: var(--primary);
}

.user-name {
    color: var(--text-secondary);
    font-size: var(--text-sm);
}
```

---

### 3.3 Login Page Layout

**Visual Wireframe:**

```
┌─────────────────────────────────────┐
│                                     │
│          [Logo + Title]             │
│                                     │
│    ┌──────────────────────┐        │
│    │  Study-Sync AI       │        │
│    │  Learn Smarter       │        │
│    └──────────────────────┘        │
│                                     │
│    ┌──────────────────────┐        │
│    │ Username:            │        │
│    │ [______________]     │        │
│    │                      │        │
│    │ Password:            │        │
│    │ [______________]     │        │
│    │                      │        │
│    │ [  Login Button  ]   │        │
│    │                      │        │
│    │ Don't have account?  │        │
│    │ Register here        │        │
│    └──────────────────────┘        │
│                                     │
└─────────────────────────────────────┘
```

**Specifications:**
- Centered card: max-width 400px
- Card padding: `var(--space-8)`
- Form fields: full width
- Vertical spacing: `var(--space-4)` between fields
- Background: `var(--gray-50)` (subtle pattern optional)

**File:** `src/main/webapp/pages/login.xhtml`

---

### 3.4 Dashboard Layout

**Visual Wireframe:**

```
┌─────────────────────────────────────────────┐
│  Header (with subject dropdown)             │
├─────────────────────────────────────────────┤
│                                             │
│  Upload Area                                │
│  ┌─────────────────────────────────────┐   │
│  │  📁 Drag & drop PDF here            │   │
│  │  or click to browse                 │   │
│  └─────────────────────────────────────┘   │
│                                             │
│  My Documents                               │
│  ┌──────────────┬──────────────┬────────┐  │
│  │ Doc 1        │ Doc 2        │ Doc 3  │  │
│  │ [Indexed]    │ [Indexing]   │ [Fail] │  │
│  │ [Open Chat]  │              │        │  │
│  │ [Flashcards] │              │        │  │
│  └──────────────┴──────────────┴────────┘  │
│                                             │
└─────────────────────────────────────────────┘
```

**Specifications:**
- Upload area: Full width, min-height 150px
- Document grid: 3 columns (desktop), 2 (tablet), 1 (mobile)
- Document card: min-height 200px, `--shadow-md`
- Status badge: top-right corner
- Action buttons: bottom of card, full width

---

### 3.5 Chat Page Layout

**Visual Wireframe:**

```
┌───────────────────────────────────────────────┐
│  [← Back] Operating Systems - Lecture 01.pdf │
│                              [Viva Mode: OFF] │
├───────────────────────────────────────────────┤
│                                               │
│  ┌─────────────────────────────────────────┐ │
│  │ 👤 What is process scheduling?          │ │
│  │                                   10:30 │ │
│  └─────────────────────────────────────────┘ │
│                                               │
│  ┌─────────────────────────────────────────┐ │
│  │ 🤖 Process scheduling is the method...  │ │
│  │                                   10:31 │ │
│  └─────────────────────────────────────────┘ │
│                                               │
│  ┌─────────────────────────────────────────┐ │
│  │ 👤 Can you explain Round Robin?         │ │
│  │                                   10:32 │ │
│  └─────────────────────────────────────────┘ │
│                                               │
│  ┌─────────────────────────────────────────┐ │
│  │ 🤖 Round Robin is a scheduling...       │ │
│  │                                   10:32 │ │
│  └─────────────────────────────────────────┘ │
│                                               │
├───────────────────────────────────────────────┤
│  [Type your question here...      ] [Send]   │
└───────────────────────────────────────────────┘
```

**Specifications:**
- Chat header: Fixed 60px height, sticky
- Messages area: Flex-grow, max-height with scroll
- User messages: Right-aligned, `--chat-user-bg`
- AI messages: Left-aligned, `--chat-ai-bg`
- Input area: Fixed 80px height, sticky bottom
- Message bubble: max-width 70%, padding `var(--space-4)`

---

### 3.6 Flashcards Page Layout

**Visual Wireframe:**

```
┌─────────────────────────────────────────────────┐
│  Flashcards - Operating Systems                 │
│  15 cards generated                             │
│                              [Regenerate] [⚙️]   │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐        │
│  │ What is │  │ Define  │  │ Explain │        │
│  │ deadlock│  │ critical│  │ mutex   │        │
│  │   ?     │  │ section?│  │   ?     │        │
│  │         │  │         │  │         │        │
│  │ [EASY]  │  │ [MEDIUM]│  │ [HARD]  │        │
│  └─────────┘  └─────────┘  └─────────┘        │
│                                                 │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐        │
│  │ ...     │  │ ...     │  │ ...     │        │
│  └─────────┘  └─────────┘  └─────────┘        │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Specifications:**
- Grid: 3 columns (desktop), 2 (tablet), 1 (mobile)
- Card size: aspect-ratio 3:4, min-height 300px
- Card spacing: `var(--space-4)` gap
- Flip on click/keyboard
- Difficulty badge: top-right, sticky during flip

---

## 4. Component Library

### 4.1 Buttons

#### Primary Button

```css
.btn-primary {
    background-color: var(--primary);
    color: white;
    border: none;
    padding: var(--space-3) var(--space-6);
    border-radius: var(--radius-md);
    font-size: var(--text-base);
    font-weight: var(--font-medium);
    cursor: pointer;
    transition: all var(--duration-fast) var(--ease-in-out);
    box-shadow: var(--shadow-sm);
}

.btn-primary:hover {
    background-color: var(--primary-hover);
    box-shadow: var(--shadow-md);
    transform: translateY(-1px);
}

.btn-primary:active {
    transform: translateY(0);
    box-shadow: var(--shadow-sm);
}

.btn-primary:focus {
    outline: none;
    box-shadow: var(--shadow-focus);
}

.btn-primary:disabled {
    background-color: var(--gray-300);
    color: var(--gray-500);
    cursor: not-allowed;
    transform: none;
}
```

#### Secondary Button

```css
.btn-secondary {
    background-color: transparent;
    color: var(--gray-700);
    border: 1px solid var(--border-default);
    padding: var(--space-3) var(--space-6);
    border-radius: var(--radius-md);
    font-size: var(--text-base);
    font-weight: var(--font-medium);
    cursor: pointer;
    transition: all var(--duration-fast) var(--ease-in-out);
}

.btn-secondary:hover {
    background-color: var(--gray-50);
    border-color: var(--gray-400);
}
```

#### Button Sizes

```css
.btn-sm {
    padding: var(--space-2) var(--space-4);
    font-size: var(--text-sm);
}

.btn-lg {
    padding: var(--space-4) var(--space-8);
    font-size: var(--text-lg);
}
```

---

### 4.2 Form Inputs

#### Text Input

```css
.form-input {
    width: 100%;
    padding: var(--space-3) var(--space-4);
    font-size: var(--text-base);
    color: var(--text-primary);
    background-color: var(--bg-primary);
    border: 1px solid var(--border-default);
    border-radius: var(--radius-md);
    transition: all var(--duration-fast) var(--ease-in-out);
}

.form-input:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: var(--shadow-focus);
}

.form-input:disabled {
    background-color: var(--gray-100);
    color: var(--gray-500);
    cursor: not-allowed;
}

.form-input.error {
    border-color: var(--danger);
}
```

#### Label

```css
.form-label {
    display: block;
    margin-bottom: var(--space-2);
    font-size: var(--text-sm);
    font-weight: var(--font-medium);
    color: var(--text-primary);
}

.form-label.required::after {
    content: " *";
    color: var(--danger);
}
```

#### Error Message

```css
.form-error {
    display: block;
    margin-top: var(--space-2);
    font-size: var(--text-sm);
    color: var(--danger);
}
```

---

### 4.3 Cards

```css
.card {
    background-color: var(--bg-primary);
    border: 1px solid var(--border-light);
    border-radius: var(--radius-lg);
    padding: var(--space-6);
    box-shadow: var(--shadow-md);
    transition: all var(--duration-fast) var(--ease-in-out);
}

.card:hover {
    box-shadow: var(--shadow-lg);
    transform: translateY(-2px);
}

.card-header {
    margin-bottom: var(--space-4);
    padding-bottom: var(--space-4);
    border-bottom: 1px solid var(--border-light);
}

.card-title {
    font-size: var(--text-xl);
    font-weight: var(--font-semibold);
    color: var(--text-primary);
    margin: 0;
}

.card-body {
    color: var(--text-secondary);
}

.card-footer {
    margin-top: var(--space-4);
    padding-top: var(--space-4);
    border-top: 1px solid var(--border-light);
    display: flex;
    gap: var(--space-2);
}
```

---

### 4.4 Badges

```css
.badge {
    display: inline-flex;
    align-items: center;
    padding: var(--space-1) var(--space-3);
    font-size: var(--text-xs);
    font-weight: var(--font-semibold);
    border-radius: var(--radius-full);
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.badge-success {
    background-color: var(--success-light);
    color: var(--success);
}

.badge-warning {
    background-color: var(--warning-light);
    color: var(--warning);
}

.badge-danger {
    background-color: var(--danger-light);
    color: var(--danger);
}

.badge-info {
    background-color: var(--info-light);
    color: var(--info);
}
```

---

### 4.5 Loading Spinner

```css
.spinner {
    display: inline-block;
    width: 20px;
    height: 20px;
    border: 3px solid var(--gray-200);
    border-top-color: var(--primary);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.spinner-lg {
    width: 40px;
    height: 40px;
    border-width: 4px;
}
```

---

### 4.6 Toast Notifications

```css
.toast-container {
    position: fixed;
    top: 80px;
    right: var(--space-4);
    z-index: 9999;
}

.toast {
    min-width: 300px;
    background-color: var(--bg-primary);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-xl);
    padding: var(--space-4);
    margin-bottom: var(--space-2);
    display: flex;
    align-items: start;
    gap: var(--space-3);
    animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
    from {
        transform: translateX(400px);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}

.toast-success {
    border-left: 4px solid var(--success);
}

.toast-error {
    border-left: 4px solid var(--danger);
}

.toast-icon {
    flex-shrink: 0;
    width: 20px;
    height: 20px;
}

.toast-content {
    flex-grow: 1;
}

.toast-title {
    font-weight: var(--font-semibold);
    margin-bottom: var(--space-1);
}

.toast-message {
    font-size: var(--text-sm);
    color: var(--text-secondary);
}
```

**JavaScript:**

```javascript
// src/main/webapp/resources/js/ui-utils.js

function showToast(message, type = 'info', duration = 5000) {
    const container = document.querySelector('.toast-container') || createToastContainer();
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-icon">
            ${getToastIcon(type)}
        </div>
        <div class="toast-content">
            <div class="toast-title">${getToastTitle(type)}</div>
            <div class="toast-message">${message}</div>
        </div>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease-in forwards';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
}

function getToastIcon(type) {
    const icons = {
        success: '✓',
        error: '✕',
        warning: '⚠',
        info: 'ℹ'
    };
    return icons[type] || icons.info;
}

function getToastTitle(type) {
    const titles = {
        success: 'Success',
        error: 'Error',
        warning: 'Warning',
        info: 'Info'
    };
    return titles[type] || titles.info;
}
```

---

## 5. Technical Architecture

### 5.1 JSF Component Hierarchy

```
Application Root
├── layout.xhtml (Master Template)
│   ├── header.xhtml
│   ├── content (page-specific)
│   └── footer.xhtml
│
├── pages/
│   ├── login.xhtml
│   ├── dashboard.xhtml
│   │   └── upload-component.xhtml
│   ├── chat.xhtml
│   │   └── chat-message.xhtml (repeated)
│   └── flashcards.xhtml
│       └── flashcard.xhtml (repeated)
│
└── components/ (reusable)
    ├── upload-component.xhtml
    ├── chat-message.xhtml
    └── flashcard.xhtml
```

---

### 5.2 Managed Bean Architecture

#### Bean Scoping Strategy

| Bean | Scope | Reason |
|------|-------|--------|
| `LoginBean` | `@RequestScoped` | Single login action |
| `DashboardBean` | `@SessionScoped` | Maintains subject/document selection |
| `ChatBean` | `@SessionScoped` | Persistent chat history across requests |
| `FlashcardBean` | `@ViewScoped` | Tied to single flashcard page view |
| `UploadBean` | `@RequestScoped` | Single upload action |
| `UserSession` | `@SessionScoped` | User authentication state |

#### Bean Communication Pattern

```
LoginBean (Request)
    ↓ (sets session)
UserSession (Session)
    ↓ (injects)
DashboardBean (Session) ← ChatBean (Session)
    ↓                        ↓
activeDocumentId      messages[]
selectedSubjectId     vivaMode
```

---

### 5.3 State Management

#### Session State

```java
@Named("userSession")
@SessionScoped
public class UserSession implements Serializable {
    
    private Integer userId;
    private String username;
    private Integer activeDocumentId;
    private Integer activeSubjectId;
    
    public boolean isLoggedIn() {
        return userId != null;
    }
    
    // Getters and setters...
}
```

#### Cookie State (for persistence)

```java
public void saveLastViewedSubject(int subjectId) {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletResponse response = 
        (HttpServletResponse) context.getExternalContext().getResponse();
    
    Cookie cookie = new Cookie("lastSubjectId", String.valueOf(subjectId));
    cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
    cookie.setPath("/");
    response.addCookie(cookie);
}
```

---

### 5.4 AJAX Rendering Strategy

**Rule:** Minimize the components that get re-rendered.

#### Example: Chat Message Submission

```xml
<!-- BAD: Re-renders entire page -->
<h:commandButton value="Send" action="#{chatBean.sendMessage}">
    <f:ajax execute="@form" render="@all" />
</h:commandButton>

<!-- GOOD: Only updates changed components -->
<h:commandButton value="Send" action="#{chatBean.sendMessage}">
    <f:ajax execute="userInput" 
            render="messagesPanel userInput loadingIndicator" />
</h:commandButton>
```

#### Loading State Pattern

```xml
<h:panelGroup id="loadingIndicator">
    <h:panelGroup rendered="#{chatBean.loading}" styleClass="loading">
        <div class="spinner"></div>
        <span>AI is thinking...</span>
    </h:panelGroup>
</h:panelGroup>
```

```java
@Named("chatBean")
@SessionScoped
public class ChatBean implements Serializable {
    
    private boolean loading = false;
    
    public void sendMessage() {
        loading = true;
        
        // Simulate API call
        ChatResponse response = chatService.getResponse(currentMessage, vivaMode);
        
        messages.add(new ChatMessage(currentMessage, true, new Date()));
        messages.add(new ChatMessage(response.getAnswer(), false, new Date()));
        
        currentMessage = "";
        loading = false;
    }
    
    // Getters and setters...
}
```

---

## 6. Implementation Specifications

### 6.1 Upload Component

**File:** `src/main/webapp/components/upload-component.xhtml`

```xml
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:h="http://xmlns.jcp.org/jsf/html"
                xmlns:f="http://xmlns.jcp.org/jsf/core"
                xmlns:ui="http://xmlns.jcp.org/jsf/facelets">

    <div class="upload-area" 
         ondragover="event.preventDefault(); this.classList.add('drag-over');"
         ondragleave="this.classList.remove('drag-over');"
         ondrop="handleDrop(event, this);">
        
        <div class="upload-icon">
            <svg><!-- Upload icon SVG --></svg>
        </div>
        
        <h:form id="uploadForm" enctype="multipart/form-data">
            <p class="upload-text">Drag &amp; drop PDF here</p>
            <p class="upload-subtext">or</p>
            
            <h:inputFile id="fileInput" 
                         value="#{uploadBean.uploadedFile}"
                         styleClass="d-none"
                         onchange="validateFile(this)"/>
            
            <label for="uploadForm:fileInput" class="btn btn-primary">
                Browse Files
            </label>
            
            <div class="file-info" style="display: none;">
                <p id="fileName"></p>
                <p id="fileSize"></p>
            </div>
            
            <h:commandButton id="uploadBtn" 
                             value="Upload" 
                             action="#{uploadBean.upload}"
                             styleClass="btn btn-primary mt-3"
                             style="display: none;">
                <f:ajax execute="@form" 
                        render="uploadProgress uploadResult documentList"
                        onevent="handleUploadProgress"/>
            </h:commandButton>
        </h:form>
        
        <h:panelGroup id="uploadProgress" styleClass="progress-container">
            <h:panelGroup rendered="#{uploadBean.uploading}">
                <div class="progress">
                    <div class="progress-bar" 
                         style="width: #{uploadBean.progress}%"
                         role="progressbar">
                        #{uploadBean.progress}%
                    </div>
                </div>
            </h:panelGroup>
        </h:panelGroup>
        
        <h:panelGroup id="uploadResult">
            <h:panelGroup rendered="#{not empty uploadBean.message}">
                <div class="alert alert-#{uploadBean.success ? 'success' : 'danger'}">
                    #{uploadBean.message}
                </div>
            </h:panelGroup>
        </h:panelGroup>
    </div>
</ui:composition>
```

**CSS:**

```css
.upload-area {
    border: 2px dashed var(--border-default);
    border-radius: var(--radius-lg);
    padding: var(--space-12);
    text-align: center;
    transition: all var(--duration-fast) var(--ease-in-out);
    background-color: var(--bg-secondary);
}

.upload-area.drag-over {
    border-color: var(--primary);
    background-color: var(--primary-light);
}

.upload-icon {
    width: 64px;
    height: 64px;
    margin: 0 auto var(--space-4);
    color: var(--gray-400);
}

.upload-text {
    font-size: var(--text-lg);
    font-weight: var(--font-medium);
    color: var(--text-primary);
    margin-bottom: var(--space-2);
}

.upload-subtext {
    font-size: var(--text-sm);
    color: var(--text-secondary);
    margin-bottom: var(--space-4);
}

.file-info {
    margin-top: var(--space-4);
    padding: var(--space-4);
    background-color: var(--bg-primary);
    border-radius: var(--radius-md);
}

.progress-container {
    margin-top: var(--space-4);
}

.progress {
    height: 24px;
    background-color: var(--gray-200);
    border-radius: var(--radius-full);
    overflow: hidden;
}

.progress-bar {
    height: 100%;
    background-color: var(--primary);
    transition: width 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: var(--text-sm);
    font-weight: var(--font-semibold);
}
```

**JavaScript:**

```javascript
// src/main/webapp/resources/js/validation.js

function validateFile(input) {
    const file = input.files[0];
    if (!file) return;
    
    const maxSize = 20 * 1024 * 1024; // 20MB
    const allowedTypes = ['application/pdf'];
    
    // Validate file type
    if (!allowedTypes.includes(file.type)) {
        showToast('Only PDF files are allowed', 'error');
        input.value = '';
        return false;
    }
    
    // Validate file size
    if (file.size > maxSize) {
        showToast('File must be under 20MB', 'error');
        input.value = '';
        return false;
    }
    
    // Display file info
    document.getElementById('fileName').textContent = file.name;
    document.getElementById('fileSize').textContent = formatFileSize(file.size);
    document.querySelector('.file-info').style.display = 'block';
    document.getElementById('uploadBtn').style.display = 'inline-block';
    
    return true;
}

function formatFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
}

function handleDrop(event, element) {
    event.preventDefault();
    element.classList.remove('drag-over');
    
    const file = event.dataTransfer.files[0];
    if (file) {
        const input = document.getElementById('uploadForm:fileInput');
        input.files = event.dataTransfer.files;
        validateFile(input);
    }
}
```

---

### 6.2 Chat Message Component

**File:** `src/main/webapp/components/chat-message.xhtml`

```xml
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:h="http://xmlns.jcp.org/jsf/html"
                xmlns:ui="http://xmlns.jcp.org/jsf/facelets">
    
    <div class="chat-message #{isUser ? 'user-message' : 'ai-message'}">
        <div class="message-avatar">
            <h:graphicImage value="/resources/images/#{isUser ? 'user' : 'ai'}-avatar.png" 
                            alt="#{isUser ? 'User' : 'AI'}"
                            styleClass="avatar-img"/>
        </div>
        
        <div class="message-bubble">
            <div class="message-content">
                <h:outputText value="#{content}" escape="true"/>
            </div>
            
            <div class="message-meta">
                <span class="message-time">#{timestamp}</span>
                <h:panelGroup rendered="#{not isUser and hashedSources}">
                    <span class="sources-indicator" title="Based on page #{pageNum}">
                        📄
                    </span>
                </h:panelGroup>
            </div>
        </div>
    </div>
</ui:composition>
```

**CSS:**

```css
.chat-message {
    display: flex;
    gap: var(--space-3);
    margin-bottom: var(--space-4);
    animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}

.chat-message.user-message {
    flex-direction: row-reverse;
}

.message-avatar {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
}

.avatar-img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
}

.message-bubble {
    max-width: 70%;
    padding: var(--space-4);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
}

.user-message .message-bubble {
    background-color: var(--chat-user-bg);
    color: var(--chat-user-text);
    border-bottom-right-radius: var(--radius-sm);
}

.ai-message .message-bubble {
    background-color: var(--chat-ai-bg);
    color: var(--chat-ai-text);
    border-bottom-left-radius: var(--radius-sm);
}

.message-content {
    font-size: var(--text-base);
    line-height: var(--leading-relaxed);
    margin-bottom: var(--space-2);
}

.message-meta {
    display: flex;
    align-items: center;
    gap: var(--space-2);
    font-size: var(--text-xs);
    opacity: 0.7;
}

.sources-indicator {
    cursor: help;
}
```

---

### 6.3 Flashcard Component

**File:** `src/main/webapp/components/flashcard.xhtml`

```xml
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:h="http://xmlns.jcp.org/jsf/html"
                xmlns:ui="http://xmlns.jcp.org/jsf/facelets">
    
    <div class="flashcard" 
         tabindex="0"
         onclick="this.classList.toggle('flipped')"
         onkeydown="if(event.key==='Enter' || event.key===' ') { event.preventDefault(); this.classList.toggle('flipped'); }">
        
        <div class="flashcard-inner">
            <!-- Front Side -->
            <div class="flashcard-front">
                <div class="difficulty-badge difficulty-#{difficulty}">
                    #{difficulty}
                </div>
                
                <div class="card-content">
                    <div class="card-label">Question</div>
                    <p class="card-text">#{question}</p>
                </div>
                
                <div class="flip-hint">
                    Click or press Enter to reveal answer
                </div>
            </div>
            
            <!-- Back Side -->
            <div class="flashcard-back">
                <div class="card-content">
                    <div class="card-label">Answer</div>
                    <p class="card-text">#{answer}</p>
                </div>
                
                <div class="flip-hint">
                    Click or press Enter to go back
                </div>
            </div>
        </div>
    </div>
</ui:composition>
```

**CSS:**

```css
.flashcard {
    width: 100%;
    height: 350px;
    perspective: 1000px;
    cursor: pointer;
}

.flashcard:focus {
    outline: none;
}

.flashcard:focus-visible .flashcard-inner {
    box-shadow: var(--shadow-focus), var(--shadow-xl);
}

.flashcard-inner {
    position: relative;
    width: 100%;
    height: 100%;
    transition: transform var(--duration-normal) var(--ease-in-out);
    transform-style: preserve-3d;
    box-shadow: var(--shadow-xl);
    border-radius: var(--radius-2xl);
}

.flashcard.flipped .flashcard-inner {
    transform: rotateY(180deg);
}

.flashcard-front,
.flashcard-back {
    position: absolute;
    width: 100%;
    height: 100%;
    backface-visibility: hidden;
    border-radius: var(--radius-2xl);
    padding: var(--space-6);
    display: flex;
    flex-direction: column;
    justify-content: center;
    background: white;
    border: 1px solid var(--border-light);
}

.flashcard-back {
    transform: rotateY(180deg);
    background: var(--gray-50);
}

.difficulty-badge {
    position: absolute;
    top: var(--space-4);
    right: var(--space-4);
    padding: var(--space-2) var(--space-3);
    border-radius: var(--radius-full);
    font-size: var(--text-xs);
    font-weight: var(--font-bold);
    text-transform: uppercase;
}

.difficulty-easy {
    background-color: var(--success-light);
    color: var(--success);
}

.difficulty-medium {
    background-color: var(--warning-light);
    color: var(--warning);
}

.difficulty-hard {
    background-color: var(--danger-light);
    color: var(--danger);
}

.card-content {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.card-label {
    font-size: var(--text-sm);
    font-weight: var(--font-semibold);
    color: var(--text-secondary);
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: var(--space-2);
}

.card-text {
    font-size: var(--text-lg);
    line-height: var(--leading-relaxed);
    color: var(--text-primary);
    text-align: center;
}

.flip-hint {
    font-size: var(--text-xs);
    color: var(--text-muted);
    text-align: center;
    margin-top: var(--space-4);
}

/* Responsive: Smaller cards on mobile */
@media (max-width: 767px) {
    .flashcard {
        height: 280px;
    }
    
    .card-text {
        font-size: var(--text-base);
    }
}
```

---

## 7. Animation & Interaction Design

### 7.1 Animation Principles

**Guidelines:**
1. **Purpose over decoration** - Animations should communicate state changes
2. **Fast and subtle** - 150-300ms for most UI interactions
3. **Consistent easing** - Use `ease-in-out` for most transitions
4. **Performance first** - Animate `transform` and `opacity` only (GPU accelerated)

### 7.2 Micro-interactions

#### Button Click

```css
.btn {
    transition: all 150ms ease-in-out;
}

.btn:active {
    transform: scale(0.98);
}
```

#### Card Hover

```css
.card {
    transition: all 200ms ease-in-out;
}

.card:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
}
```

#### Input Focus

```css
.form-input {
    transition: all 150ms ease-in-out;
}

.form-input:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}
```

### 7.3 Page Transitions

```css
/* Fade in on page load */
.page-content {
    animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
```

### 7.4 Loading States

#### Skeleton Loader

```css
.skeleton {
    background: linear-gradient(
        90deg,
        var(--gray-200) 0%,
        var(--gray-300) 50%,
        var(--gray-200) 100%
    );
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
    border-radius: var(--radius-md);
}

@keyframes shimmer {
    0% { background-position: 200% 0; }
    100% { background-position: -200% 0; }
}

.skeleton-text {
    height: 16px;
    margin-bottom: 8px;
}

.skeleton-title {
    height: 24px;
    width: 60%;
    margin-bottom: 12px;
}
```

---

## 8. Responsive Design Strategy

### 8.1 Breakpoints

```css
/* Mobile-first approach */
:root {
    --breakpoint-sm: 640px;   /* Small tablets */
    --breakpoint-md: 768px;   /* Tablets */
    --breakpoint-lg: 1024px;  /* Small laptops */
    --breakpoint-xl: 1280px;  /* Desktops */
    --breakpoint-2xl: 1536px; /* Large screens */
}
```

### 8.2 Responsive Grid

```css
.container {
    width: 100%;
    margin: 0 auto;
    padding: 0 var(--space-4);
}

@media (min-width: 640px) {
    .container { max-width: 640px; }
}

@media (min-width: 768px) {
    .container { max-width: 768px; }
}

@media (min-width: 1024px) {
    .container { max-width: 1024px; }
}

@media (min-width: 1280px) {
    .container { max-width: 1280px; }
}
```

### 8.3 Component Responsiveness

#### Dashboard Cards

```css
.document-grid {
    display: grid;
    gap: var(--space-4);
    grid-template-columns: 1fr; /* Mobile: 1 column */
}

@media (min-width: 768px) {
    .document-grid {
        grid-template-columns: repeat(2, 1fr); /* Tablet: 2 columns */
    }
}

@media (min-width: 1024px) {
    .document-grid {
        grid-template-columns: repeat(3, 1fr); /* Desktop: 3 columns */
    }
}
```

#### Chat Messages

```css
.message-bubble {
    max-width: 85%; /* Mobile: wider bubbles */
}

@media (min-width: 768px) {
    .message-bubble {
        max-width: 70%; /* Desktop: narrower bubbles */
    }
}
```

---

## 9. Accessibility Patterns

### 9.1 Focus Management

```css
/* Remove default outline, add custom */
*:focus {
    outline: none;
}

*:focus-visible {
    outline: 2px solid var(--primary);
    outline-offset: 2px;
}

/* Button focus */
.btn:focus-visible {
    box-shadow: var(--shadow-focus);
}
```

### 9.2 Screen Reader Support

```xml
<!-- Visually hidden but accessible to screen readers -->
<span class="sr-only">Upload PDF document</span>

<!-- ARIA labels for icons -->
<button aria-label="Send message">
    <svg><!-- Icon --></svg>
</button>

<!-- Live region for dynamic updates -->
<div aria-live="polite" aria-atomic="true" id="chat-status">
    #{chatBean.loading ? 'AI is typing...' : ''}
</div>
```

```css
.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border-width: 0;
}
```

### 9.3 Keyboard Navigation

```javascript
// Trap focus in modal
function trapFocus(element) {
    const focusableElements = element.querySelectorAll(
        'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );
    
    const firstElement = focusableElements[0];
    const lastElement = focusableElements[focusableElements.length - 1];
    
    element.addEventListener('keydown', (e) => {
        if (e.key === 'Tab') {
            if (e.shiftKey && document.activeElement === firstElement) {
                e.preventDefault();
                lastElement.focus();
            } else if (!e.shiftKey && document.activeElement === lastElement) {
                e.preventDefault();
                firstElement.focus();
            }
        }
        
        if (e.key === 'Escape') {
            closeModal();
        }
    });
}
```

---

## 10. Code Conventions

### 10.1 CSS Naming (BEM-ish)

```css
/* Block */
.chat-message { }

/* Element */
.chat-message__avatar { }
.chat-message__bubble { }
.chat-message__content { }

/* Modifier */
.chat-message--user { }
.chat-message--ai { }
```

### 10.2 JavaScript Conventions

```javascript
// Use ES6+ features
const getUserId = () => sessionStorage.getItem('userId');

// Named functions for clarity
function validatePDFFile(file) {
    return file.type === 'application/pdf' && file.size < MAX_FILE_SIZE;
}

// Async/await for promises
async function fetchChatResponse(message) {
    try {
        const response = await fetch('/api/chat', {
            method: 'POST',
            body: JSON.stringify({ message })
        });
        return await response.json();
    } catch (error) {
        console.error('Chat API error:', error);
        showToast('Failed to send message', 'error');
    }
}
```

### 10.3 JSF/XHTML Conventions

```xml
<!-- Use semantic IDs -->
<h:form id="loginForm">
    <h:inputText id="username" value="#{loginBean.username}" />
    <h:inputSecret id="password" value="#{loginBean.password}" />
    <h:commandButton id="submitBtn" value="Login" action="#{loginBean.login}" />
</h:form>

<!-- Consistent indentation (4 spaces) -->
<ui:repeat value="#{dashboardBean.documents}" var="doc">
    <div class="document-card">
        <h5>#{doc.fileName}</h5>
        <p>#{doc.uploadedAt}</p>
    </div>
</ui:repeat>

<!-- Escape by default, be explicit when not -->
<h:outputText value="#{message.content}" escape="true" />
```

---

## 11. File Organization

### 11.1 CSS Architecture

```
resources/css/
├── styles.css              # Global styles, variables
├── components/
│   ├── buttons.css         # Button variants
│   ├── forms.css           # Form elements
│   ├── cards.css           # Card components
│   └── badges.css          # Badge styles
├── pages/
│   ├── login.css           # Login page specific
│   ├── dashboard.css       # Dashboard specific
│   ├── chat.css            # Chat interface
│   └── flashcards.css      # Flashcard grid
└── utilities/
    ├── animations.css      # Keyframes, transitions
    └── helpers.css         # Utility classes
```

### 11.2 JavaScript Architecture

```
resources/js/
├── ui-utils.js             # Toast, modal, helpers
├── validation.js           # Form validation
├── chat-utils.js           # Chat-specific functions
└── upload.js               # File upload logic
```

---

## 12. Appendix: Code Examples

### 12.1 Complete Chat Page Example

**File:** `src/main/webapp/pages/chat.xhtml`

```xml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets">

<ui:composition template="/WEB-INF/templates/layout.xhtml">
    <ui:define name="title">Chat - Study-Sync AI</ui:define>
    
    <ui:define name="page-css">
        <h:outputStylesheet name="css/chat.css"/>
    </ui:define>
    
    <ui:define name="content">
        <div class="chat-container">
            <!-- Header -->
            <div class="chat-header">
                <div class="header-left">
                    <h:link outcome="/pages/dashboard.xhtml" styleClass="back-link">
                        ← Back
                    </h:link>
                    <h4 class="document-title">#{chatBean.documentName}</h4>
                </div>
                
                <div class="header-right">
                    <h:form>
                        <div class="viva-toggle">
                            <h:selectBooleanCheckbox id="vivaMode" 
                                                     value="#{chatBean.vivaMode}"
                                                     styleClass="form-check-input">
                                <f:ajax render="vivaIndicator"/>
                            </h:selectBooleanCheckbox>
                            <label for="vivaMode" class="form-check-label">
                                Viva Mode
                            </label>
                        </div>
                    </h:form>
                    
                    <h:panelGroup id="vivaIndicator">
                        <span class="badge badge-info" 
                              rendered="#{chatBean.vivaMode}">
                            Viva Active
                        </span>
                    </h:panelGroup>
                </div>
            </div>
            
            <!-- Messages Area -->
            <div class="chat-messages" id="chatMessages">
                <h:panelGroup id="messagesPanel">
                    <ui:repeat value="#{chatBean.messages}" var="msg">
                        <ui:include src="/components/chat-message.xhtml">
                            <ui:param name="content" value="#{msg.content}"/>
                            <ui:param name="isUser" value="#{msg.isUser}"/>
                            <ui:param name="timestamp" value="#{msg.timestamp}"/>
                        </ui:include>
                    </ui:repeat>
                    
                    <h:panelGroup rendered="#{empty chatBean.messages}">
                        <div class="empty-state">
                            <p>No messages yet. Start by asking a question!</p>
                        </div>
                    </h:panelGroup>
                </h:panelGroup>
            </div>
            
            <!-- Input Area -->
            <div class="chat-input-container">
                <h:form id="chatForm">
                    <div class="input-wrapper">
                        <h:inputText id="userInput" 
                                     value="#{chatBean.currentMessage}"
                                     placeholder="Ask a question about this document..."
                                     styleClass="form-control"
                                     maxlength="500">
                            <f:ajax event="keyup" 
                                    listener="#{chatBean.onInputChange}"
                                    render="charCount"/>
                        </h:inputText>
                        
                        <h:commandButton id="sendBtn" 
                                         value="Send"
                                         action="#{chatBean.sendMessage}"
                                         styleClass="btn btn-primary"
                                         disabled="#{empty chatBean.currentMessage}">
                            <f:ajax execute="userInput" 
                                    render="messagesPanel userInput loadingIndicator charCount"
                                    onevent="scrollToBottom"/>
                        </h:commandButton>
                    </div>
                    
                    <div class="input-meta">
                        <h:panelGroup id="charCount" styleClass="char-count">
                            #{500 - fn:length(chatBean.currentMessage)} characters remaining
                        </h:panelGroup>
                        
                        <h:panelGroup id="loadingIndicator">
                            <span class="loading-text" rendered="#{chatBean.loading}">
                                <span class="spinner spinner-sm"></span>
                                AI is thinking...
                            </span>
                        </h:panelGroup>
                    </div>
                </h:form>
            </div>
        </div>
    </ui:define>
    
    <ui:define name="page-js">
        <h:outputScript name="js/chat-utils.js"/>
        <script>
            function scrollToBottom() {
                setTimeout(() => {
                    const messages = document.getElementById('chatMessages');
                    if (messages) {
                        messages.scrollTop = messages.scrollHeight;
                    }
                }, 100);
            }
            
            // Scroll on page load
            window.addEventListener('DOMContentLoaded', scrollToBottom);
        </script>
    </ui:define>
</ui:composition>
</html>
```

---

### 12.2 Managed Bean Example

```java
package com.studysync.frontend.beans;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("chatBean")
@SessionScoped
public class ChatBean implements Serializable {
    
    @Inject
    private UserSession userSession;
    
    @Inject
    private MockChatService chatService;
    
    private List<ChatMessage> messages = new ArrayList<>();
    private String currentMessage;
    private boolean vivaMode = false;
    private boolean loading = false;
    private String documentName;
    
    @PostConstruct
    public void init() {
        // Load document name from session
        if (userSession.getActiveDocumentId() != null) {
            documentName = loadDocumentName(userSession.getActiveDocumentId());
            loadChatHistory();
        }
    }
    
    public void sendMessage() {
        if (currentMessage == null || currentMessage.trim().isEmpty()) {
            return;
        }
        
        loading = true;
        
        // Add user message
        messages.add(new ChatMessage(
            currentMessage, 
            true, 
            new Date()
        ));
        
        // Get AI response (mock or real API)
        ChatResponse response = chatService.getChatResponse(
            currentMessage, 
            vivaMode
        );
        
        // Add AI message
        messages.add(new ChatMessage(
            response.getAnswer(), 
            false, 
            new Date()
        ));
        
        // Clear input
        currentMessage = "";
        loading = false;
    }
    
    public void onInputChange() {
        // Called on keyup via AJAX
        // Can implement typing indicator here
    }
    
    private void loadChatHistory() {
        // Load from session or database
        messages = chatService.getChatHistory(
            userSession.getActiveDocumentId()
        );
    }
    
    private String loadDocumentName(int docId) {
        // Fetch from service
        return "Lecture_01.pdf"; // Mock
    }
    
    // Getters and setters
    public List<ChatMessage> getMessages() { return messages; }
    public String getCurrentMessage() { return currentMessage; }
    public void setCurrentMessage(String msg) { this.currentMessage = msg; }
    public boolean isVivaMode() { return vivaMode; }
    public void setVivaMode(boolean mode) { this.vivaMode = mode; }
    public boolean isLoading() { return loading; }
    public String getDocumentName() { return documentName; }
}
```

---

## Document Approval

**Approved By:**

| Role | Name | Date |
|------|------|------|
| Frontend Lead | _____________ | _______ |
| UI/UX Designer | _____________ | _______ |
| Tech Lead | _____________ | _______ |

**Version History:**
- v1.0 (Jan 25, 2026): Initial design document approved

---

**End of Document**