# Study-Sync AI - Frontend Development Guidelines

**Version:** 1.0 (Frontend-Only)  
**Last Updated:** January 24, 2026  
**Project Type:** Academic Mini Project (Frontend Development)

---

## 📚 Table of Contents

1. [Project Overview](#project-overview)
2. [Frontend Tech Stack](#frontend-tech-stack)
3. [Frontend Project Structure](#frontend-project-structure)
4. [Setup & Installation](#setup--installation)
5. [Important Commands](#important-commands)
6. [Development Workflow](#development-workflow)
7. [Mock API & Test Data](#mock-api--test-data)
8. [Frontend Architecture](#frontend-architecture)
9. [Team Responsibilities (Frontend)](#team-responsibilities-frontend)
10. [Component Specifications](#component-specifications)
11. [Testing Guidelines](#testing-guidelines)
12. [Backend Integration Preparation](#backend-integration-preparation)
13. [Viva Preparation](#viva-preparation)
14. [Troubleshooting](#troubleshooting)

---

## 📖 Project Overview

### What is Study-Sync AI?

**Study-Sync AI** is an AI-powered collaborative resource library designed to help students interact intelligently with their course materials. Instead of passively reading PDFs, students can:

- **Upload PDFs/Notes** and have them automatically indexed
- **Chat with their Subject** - Ask questions and get contextual answers from uploaded documents
- **Generate Flashcards** - AI automatically creates question-answer pairs for study
- **Practice Viva Questions** - Simulate oral examination with AI examiner

### Frontend Team Focus

Since **backend is not ready yet**, the frontend team will:

✅ Build all UI pages and components (JSF/XHTML)  
✅ Implement client-side validation and UX flows  
✅ Use **mock APIs** with static JSON data for development  
✅ Create responsive layouts with Bootstrap 5  
✅ Prepare integration contracts for backend handoff  
✅ Ensure accessibility and cross-browser compatibility  

### Key Features (Frontend Scope)

✅ **Login/Register UI** - Form validation, error display  
✅ **Dashboard** - Subject selector, document list, upload component  
✅ **PDF Upload Component** - Drag-drop, progress bar, validation  
✅ **Chat Interface** - Message bubbles, AJAX updates, viva mode toggle  
✅ **Flashcard Grid** - Flip animation, keyboard navigation  
✅ **Session Management** - Active document indicator, cookies  

---

## 🛠️ Frontend Tech Stack

### Core Technologies

| Technology | Purpose | Version |
|------------|---------|---------|
| **JSF (JavaServer Faces)** | Component-based UI framework | 2.3+ |
| **Facelets (XHTML)** | Templating engine for JSF | 2.3+ |
| **AJAX (`<f:ajax>`)** | Async updates without page reloads | Built-in |
| **Bootstrap 5** | Responsive CSS framework | 5.3.0 |
| **Vanilla JavaScript** | Client-side validation, utilities | ES6+ |
| **CSS3** | Custom styling, animations | - |

### Development Tools

| Tool | Purpose |
|------|---------|
| **Apache Tomcat 10** | Application server for JSF |
| **Maven** | Build automation, dependency management |
| **Git** | Version control |
| **VS Code / IntelliJ IDEA** | Code editor |
| **Chrome DevTools** | Debugging, responsive testing |

### Why JSF? (Academic Requirement)

> "JavaServer Faces was mandated by faculty to demonstrate understanding of Java EE web frameworks and stateful component management."

---

## 📁 Frontend Project Structure

```
study-sync-ai-frontend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.studysync.frontend/
│   │   │       ├── beans/                    # JSF Managed Beans
│   │   │       │   ├── LoginBean.java
│   │   │       │   ├── DashboardBean.java
│   │   │       │   ├── ChatBean.java
│   │   │       │   └── FlashcardBean.java
│   │   │       ├── validators/               # Custom validators
│   │   │       │   └── PDFValidator.java
│   │   │       └── mock/                     # Mock API services (temporary)
│   │   │           ├── MockAuthService.java
│   │   │           ├── MockDocumentService.java
│   │   │           └── MockChatService.java
│   │   ├── resources/
│   │   │   ├── messages.properties           # i18n resource bundle
│   │   │   └── mock-data/                    # JSON test data
│   │   │       ├── subjects.json
│   │   │       ├── documents.json
│   │   │       ├── chat-history.json
│   │   │       └── flashcards.json
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                   # Servlet configuration
│   │       │   ├── faces-config.xml          # JSF configuration
│   │       │   └── templates/                # Reusable templates
│   │       │       ├── layout.xhtml          # Main page layout
│   │       │       ├── header.xhtml          # Top navigation
│   │       │       └── footer.xhtml          # Footer
│   │       ├── resources/
│   │       │   ├── css/
│   │       │   │   ├── styles.css            # Global styles
│   │       │   │   ├── dashboard.css
│   │       │   │   ├── chat.css
│   │       │   │   └── flashcard-flip.css    # Flip animation
│   │       │   ├── js/
│   │       │   │   ├── ui-utils.js           # Helper functions
│   │       │   │   ├── validation.js         # Client validation
│   │       │   │   └── chat-utils.js         # Chat-specific JS
│   │       │   └── images/
│   │       │       ├── logo.png
│   │       │       └── placeholder.svg
│   │       ├── components/                   # Reusable JSF components
│   │       │   ├── upload-component.xhtml
│   │       │   ├── chat-message.xhtml
│   │       │   └── flashcard.xhtml
│   │       ├── pages/                        # Main pages
│   │       │   ├── login.xhtml
│   │       │   ├── register.xhtml
│   │       │   ├── dashboard.xhtml
│   │       │   ├── chat.xhtml
│   │       │   └── flashcards.xhtml
│   │       └── index.xhtml                   # Entry point
├── docs/
│   ├── wireframes/                           # UI mockups
│   ├── design-system.md                      # Colors, typography, spacing
│   └── integration-contracts.md              # API contracts for backend
├── pom.xml                                   # Maven dependencies
├── README.md
└── GUIDELINES.md                             # This file
```

---

## ⚙️ Setup & Installation

### Prerequisites

Ensure you have the following installed:

- ✅ **JDK 11+** (Java Development Kit)
- ✅ **Apache Maven 3.6+**
- ✅ **Apache Tomcat 10** (for running JSF apps)
- ✅ **Git**
- ✅ **IDE**: IntelliJ IDEA / Eclipse / VS Code

### Step 1: Clone Repository

```bash
git clone https://github.com/your-org/study-sync-ai-frontend.git
cd study-sync-ai-frontend
```

### Step 2: Install Dependencies

```bash
mvn clean install
```

This will download:
- JSF libraries (Mojarra implementation)
- Bootstrap 5 (via WebJars)
- Commons FileUpload (for PDF uploads)

### Step 3: Configure JSF

Verify `src/main/webapp/WEB-INF/web.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    
    <display-name>Study-Sync AI</display-name>
    
    <!-- JSF Servlet -->
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>
    
    <!-- Welcome file -->
    <welcome-file-list>
        <welcome-file>pages/login.xhtml</welcome-file>
    </welcome-file-list>
    
    <!-- Session timeout: 24 hours -->
    <session-config>
        <session-timeout>1440</session-timeout>
    </session-config>
</web-app>
```

### Step 4: Set Up Mock Data

Create `src/main/resources/mock-data/subjects.json`:

```json
[
  {"id": 1, "name": "Operating Systems"},
  {"id": 2, "name": "Data Structures"},
  {"id": 3, "name": "Database Management"}
]
```

Create `src/main/resources/mock-data/documents.json`:

```json
[
  {
    "id": 101,
    "subjectId": 1,
    "fileName": "OS_Lecture_01.pdf",
    "uploadedAt": "2026-01-20T10:30:00Z",
    "status": "indexed",
    "pageCount": 25
  },
  {
    "id": 102,
    "subjectId": 1,
    "fileName": "Process_Scheduling.pdf",
    "uploadedAt": "2026-01-22T14:15:00Z",
    "status": "indexed",
    "pageCount": 18
  }
]
```

*(See "Mock API & Test Data" section for complete examples)*

### Step 5: Build & Deploy to Tomcat

```bash
# Build WAR file
mvn clean package

# Copy to Tomcat webapps
cp target/study-sync-frontend.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh
```

### Step 6: Access Application

Open browser:

```
http://localhost:8080/study-sync-frontend/pages/login.xhtml
```

**Mock Login Credentials:**
- Username: `student1`
- Password: `password` (any value works with mock)

---

## 🚀 Important Commands

### Maven Commands

```bash
# Clean build artifacts
mvn clean

# Compile source code
mvn compile

# Package as WAR
mvn package

# Clean + Install dependencies + Package
mvn clean install

# Skip tests during build (for faster iteration)
mvn clean package -DskipTests

# Run embedded Tomcat (if configured)
mvn tomcat7:run
```

### Tomcat Management

```bash
# Start Tomcat
$TOMCAT_HOME/bin/startup.sh

# Stop Tomcat
$TOMCAT_HOME/bin/shutdown.sh

# View logs (watch for errors)
tail -f $TOMCAT_HOME/logs/catalina.out

# Hot redeploy (after code changes)
touch $TOMCAT_HOME/webapps/study-sync-frontend.war
```

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/chat-ui

# Stage changes
git add src/main/webapp/pages/chat.xhtml
git add src/main/webapp/resources/css/chat.css

# Commit with semantic message
git commit -m "feat: implement chat message bubbles with AJAX"

# Push to remote
git push origin feature/chat-ui

# After review, merge to main
git checkout main
git merge feature/chat-ui
git push origin main
```

### Frontend Testing

```bash
# Validate XHTML syntax
xmllint --noout src/main/webapp/pages/*.xhtml

# Run JavaScript linter (if using ESLint)
npm run lint

# Visual regression testing (manual)
# Open browser DevTools → Responsive Design Mode
# Test: 320px, 768px, 1920px widths
```

---

## 🔄 Development Workflow (5-Week Timeline)

### Week 1: Foundation & Design ✅

**Goals:**
- Set up project structure
- Create wireframes
- Build static HTML prototypes
- Define design system

**Tasks:**
- [ ] Initialize Maven project with JSF dependencies
- [ ] Create wireframes (Figma/Balsamiq)
- [ ] Build static HTML versions of all pages (no JSF yet)
- [ ] Document design system (colors, typography, spacing)
- [ ] Set up Git repository and branches

**Deliverables:**
- Wireframes (PDF/PNG)
- `design-system.md`
- Static HTML prototypes in `docs/prototypes/`

---

### Week 2: Core Pages & JSF Integration 🔨

**Goals:**
- Convert static HTML to JSF pages
- Implement navigation
- Create reusable templates

**Tasks:**
- [ ] Create `layout.xhtml` template
- [ ] Build `login.xhtml` with form validation
- [ ] Build `dashboard.xhtml` with subject dropdown
- [ ] Create managed beans: `LoginBean`, `DashboardBean`
- [ ] Implement mock authentication
- [ ] Test navigation between pages

**Deliverables:**
- Working login → dashboard flow
- JSF templates in `WEB-INF/templates/`
- Mock authentication service

---

### Week 3: Upload & Chat Components 💬

**Goals:**
- Build upload component with validation
- Create chat interface with AJAX

**Tasks:**
- [ ] Build `upload-component.xhtml` with drag-drop
- [ ] Implement client-side PDF validation (JS)
- [ ] Create progress bar animation
- [ ] Build `chat.xhtml` with message bubbles
- [ ] Implement `<f:ajax>` for message submission
- [ ] Add Viva Mode toggle
- [ ] Use mock chat API responses

**Deliverables:**
- Functional upload component
- Chat interface with AJAX (no page reload)
- `ChatBean` with session-scoped chat history

---

### Week 4: Flashcards & Animations 🎴

**Goals:**
- Build flashcard grid
- Implement flip animation
- Add keyboard accessibility

**Tasks:**
- [ ] Build `flashcards.xhtml` with Bootstrap grid
- [ ] Create `flashcard.xhtml` component
- [ ] Implement CSS 3D flip animation
- [ ] Add keyboard navigation (Tab, Enter, Space)
- [ ] Load flashcards from mock JSON
- [ ] Add "Generate Flashcards" button (shows mock data)

**Deliverables:**
- Flashcard grid with flip animation (60fps)
- Keyboard-accessible flashcards
- Mock flashcard generation flow

---

### Week 5: Polish & Testing ✨

**Goals:**
- Responsive design fixes
- Accessibility audit
- Cross-browser testing
- Documentation

**Tasks:**
- [ ] Test on mobile (320px), tablet (768px), desktop (1920px)
- [ ] Run accessibility scan (axe DevTools)
- [ ] Fix ARIA labels and keyboard focus
- [ ] Cross-browser testing (Chrome, Firefox, Edge, Safari)
- [ ] Write integration contracts for backend
- [ ] Prepare demo video (3 minutes)
- [ ] Document component usage

**Deliverables:**
- Responsive across all devices
- Zero critical accessibility violations
- `integration-contracts.md` for backend team
- Demo video + presentation slides

---

## 🎭 Mock API & Test Data

Since backend is not ready, use **mock services** to simulate API responses.

### Mock Authentication Service

**File:** `src/main/java/com/studysync/frontend/mock/MockAuthService.java`

```java
package com.studysync.frontend.mock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class MockAuthService {
    
    public boolean authenticate(String username, String password) {
        // Accept any non-empty credentials for now
        return username != null && !username.isEmpty() 
            && password != null && !password.isEmpty();
    }
    
    public int getUserId(String username) {
        // Mock user IDs
        return username.hashCode();
    }
}
```

### Mock Document Service

**File:** `src/main/java/com/studysync/frontend/mock/MockDocumentService.java`

```java
package com.studysync.frontend.mock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Named
@ApplicationScoped
public class MockDocumentService {
    
    private Gson gson = new Gson();
    
    public List<Subject> getSubjects() {
        InputStream is = getClass().getResourceAsStream("/mock-data/subjects.json");
        return gson.fromJson(new InputStreamReader(is), 
            new TypeToken<List<Subject>>(){}.getType());
    }
    
    public List<Document> getDocuments(int subjectId) {
        InputStream is = getClass().getResourceAsStream("/mock-data/documents.json");
        List<Document> allDocs = gson.fromJson(new InputStreamReader(is), 
            new TypeToken<List<Document>>(){}.getType());
        
        // Filter by subject
        return allDocs.stream()
            .filter(doc -> doc.getSubjectId() == subjectId)
            .collect(Collectors.toList());
    }
    
    public boolean uploadDocument(String fileName, byte[] fileData) {
        // Simulate upload success
        System.out.println("Mock upload: " + fileName + " (" + fileData.length + " bytes)");
        return true;
    }
}
```

### Mock Chat Service

**File:** `src/main/resources/mock-data/chat-responses.json`

```json
{
  "responses": {
    "what is process scheduling": {
      "answer": "Process scheduling is the method by which the operating system decides which process gets to use the CPU at any given time. Common algorithms include FCFS, SJF, and Round-Robin.",
      "sources": [
        {"page": 12, "snippet": "Round-robin scheduling allocates..."}
      ]
    },
    "explain deadlock": {
      "answer": "Deadlock is a situation where a set of processes are blocked because each process is holding a resource and waiting for another resource acquired by some other process. The four necessary conditions are: mutual exclusion, hold and wait, no preemption, and circular wait.",
      "sources": [
        {"page": 45, "snippet": "Deadlock occurs when..."}
      ]
    },
    "default": {
      "answer": "I found relevant information about this topic in your document. Could you rephrase your question for more specific results?",
      "sources": []
    }
  }
}
```

**Java Service:**

```java
package com.studysync.frontend.mock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class MockChatService {
    
    public ChatResponse getChatResponse(String question, boolean vivaMode) {
        // Simulate 1-second delay (like real API)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
        
        String answer;
        if (vivaMode) {
            answer = "As an examiner, let me probe deeper: " + 
                     "Can you explain the implications of this concept in real-world systems?";
        } else {
            answer = getMockAnswer(question.toLowerCase());
        }
        
        return new ChatResponse(answer, null, new Date());
    }
    
    private String getMockAnswer(String question) {
        if (question.contains("scheduling")) {
            return "Process scheduling is the method by which...";
        } else if (question.contains("deadlock")) {
            return "Deadlock is a situation where...";
        } else {
            return "I found relevant information about this topic. Could you rephrase?";
        }
    }
}
```

### Mock Flashcard Data

**File:** `src/main/resources/mock-data/flashcards.json`

```json
[
  {
    "id": 501,
    "question": "What is a semaphore?",
    "answer": "A semaphore is a synchronization primitive used to control access to a shared resource. It maintains a counter that is decremented when a process acquires the resource and incremented when released.",
    "difficulty": "medium"
  },
  {
    "id": 502,
    "question": "Define critical section",
    "answer": "A critical section is a segment of code that accesses shared resources and must not be concurrently executed by more than one process to avoid race conditions.",
    "difficulty": "easy"
  },
  {
    "id": 503,
    "question": "Explain banker's algorithm",
    "answer": "Banker's algorithm is a deadlock avoidance algorithm that checks if granting a resource request will leave the system in a safe state before allowing the allocation.",
    "difficulty": "hard"
  }
]
```

---

## 🏗️ Frontend Architecture

### JSF Managed Bean Scoping

Choose the right scope for each bean:

| Scope | Use Case | Example |
|-------|----------|---------|
| `@RequestScoped` | Single request only | Form validation |
| `@ViewScoped` | Single page navigation | Dashboard filters |
| `@SessionScoped` | User session data | Chat history, active document |
| `@ApplicationScoped` | Shared across all users | Configuration, mock services |

**Example:**

```java
@Named("chatBean")
@SessionScoped
public class ChatBean implements Serializable {
    
    private List<ChatMessage> messages = new ArrayList<>();
    private String currentMessage;
    private int activeDocumentId;
    private boolean vivaMode = false;
    
    // Getters, setters, and methods...
}
```

### AJAX Strategy

**Rule:** Update only the components that change, not the entire page.

**Good Example (Minimal Render):**

```xml
<h:form id="chatForm">
    <h:inputText id="userMessage" value="#{chatBean.currentMessage}" />
    
    <h:commandButton value="Send" action="#{chatBean.sendMessage}">
        <f:ajax execute="userMessage" render="messagesPanel userMessage" />
    </h:commandButton>
    
    <h:panelGroup id="messagesPanel">
        <ui:repeat value="#{chatBean.messages}" var="msg">
            <div class="message #{msg.isUser ? 'user' : 'ai'}">
                #{msg.content}
            </div>
        </ui:repeat>
    </h:panelGroup>
</h:form>
```

**Bad Example (Full Page Render):**

```xml
<!-- DON'T DO THIS -->
<f:ajax execute="@form" render="@all" />
```

### Component Reusability

Create reusable components for common UI patterns:

**Example: Chat Message Component**

**File:** `src/main/webapp/components/chat-message.xhtml`

```xml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets">

<ui:composition>
    <div class="chat-message #{isUser ? 'user-message' : 'ai-message'}">
        <div class="message-avatar">
            <h:graphicImage value="#{isUser ? '/resources/images/user.png' : '/resources/images/ai.png'}" />
        </div>
        <div class="message-content">
            <h:outputText value="#{message}" escape="false" />
        </div>
        <div class="message-time">
            #{timestamp}
        </div>
    </div>
</ui:composition>
</html>
```

**Usage:**

```xml
<ui:include src="/components/chat-message.xhtml">
    <ui:param name="message" value="#{msg.content}" />
    <ui:param name="isUser" value="#{msg.isUser}" />
    <ui:param name="timestamp" value="#{msg.timestamp}" />
</ui:include>
```

---

## 👥 Team Responsibilities (Frontend)

### Member 1: UI/UX Designer & Page Structure

**Tasks:**
- Create wireframes and mockups
- Build page templates (`layout.xhtml`, `header.xhtml`, `footer.xhtml`)
- Define design system (colors, typography, spacing)
- Ensure consistent styling across pages

**Deliverables:**
- Wireframes (Figma/Balsamiq)
- `design-system.md`
- JSF template files

**Viva Talking Points:**
> "I designed a consistent layout template that all pages inherit, ensuring brand consistency and reducing code duplication."

---

### Member 2: Dashboard & Document Management

**Tasks:**
- Build `dashboard.xhtml`
- Implement subject dropdown
- Create document list component
- Integrate upload component
- Handle document selection (session state)

**Deliverables:**
- `dashboard.xhtml`
- `DashboardBean.java`
- Mock document service integration

**Viva Talking Points:**
> "The dashboard uses session-scoped beans to maintain active document state, so when users navigate to chat, the correct context is preserved."

---

### Member 3: Upload Component Specialist

**Tasks:**
- Build drag-and-drop upload area
- Implement client-side validation (file type, size)
- Create upload progress bar
- Handle file preview (optional)
- Mock file upload to server

**Deliverables:**
- `upload-component.xhtml`
- `validation.js`
- Upload progress UI

**Viva Talking Points:**
> "We validate files twice: client-side for immediate UX feedback, and again server-side for security. Only .pdf files under 20MB are accepted."

---

### Member 4: Chat Interface Developer

**Tasks:**
- Build `chat.xhtml` with message bubbles
- Implement AJAX message submission
- Add Viva Mode toggle
- Create auto-scroll functionality
- Handle loading states

**Deliverables:**
- `chat.xhtml`
- `ChatBean.java` (session-scoped)
- `chat-utils.js`
- Mock chat API integration

**Viva Talking Points:**
> "We use `<f:ajax>` to update only the chat message panel, avoiding full page reloads. This provides a smooth, SPA-like experience within JSF."

---

### Member 5: Flashcard Module Owner

**Tasks:**
- Build `flashcards.xhtml` with Bootstrap grid
- Implement CSS 3D flip animation
- Add keyboard navigation
- Create "Generate Flashcards" flow
- Load flashcards from mock data

**Deliverables:**
- `flashcards.xhtml`
- `flashcard-flip.css`
- `FlashcardBean.java`
- Mock flashcard data

**Viva Talking Points:**
> "The flip animation uses CSS `transform: rotateY()` for smooth 60fps performance. Cards are keyboard accessible via Tab and Enter keys."

---

### Member 6: Integration & QA Lead

**Tasks:**
- Session management implementation
- Cookie handling for study session persistence
- Cross-browser testing
- Accessibility audit (WCAG 2.1 AA)
- Write integration contracts for backend
- Bug fixes and optimization

**Deliverables:**
- `integration-contracts.md`
- Test report
- Accessibility checklist
- Cross-browser compatibility matrix

**Viva Talking Points:**
> "I documented all API contracts the backend must implement, including exact request/response formats. This ensures smooth integration when backend is ready."

---

## 📦 Component Specifications

### Login Page

**File:** `pages/login.xhtml`

**Features:**
- Username and password inputs
- Form validation (non-empty fields)
- Error message display
- "Remember me" checkbox (optional)
- Redirect to dashboard on success

**Managed Bean:**

```java
@Named("loginBean")
@RequestScoped
public class LoginBean {
    
    private String username;
    private String password;
    
    @Inject
    private MockAuthService authService;
    
    public String login() {
        if (authService.authenticate(username, password)) {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            session.setAttribute("userId", authService.getUserId(username));
            session.setAttribute("username", username);
            
            return "/pages/dashboard.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Invalid credentials", "Please check your username and password"));
            return null;
        }
    }
    
    // Getters and setters...
}
```

---

### Dashboard Page

**File:** `pages/dashboard.xhtml`

**Components:**
- Subject dropdown (loads subjects from mock service)
- Document list (filtered by selected subject)
- Upload component (drag-drop area)
- Actions: Open Chat, Generate Flashcards, Delete

**Key JSF Code:**

```xml
<!-- Subject selector -->
<h:selectOneMenu id="subjectSelector" value="#{dashboardBean.selectedSubjectId}">
    <f:selectItem itemLabel="Select a subject..." itemValue="#{null}" />
    <f:selectItems value="#{dashboardBean.subjects}" var="subject" 
                   itemLabel="#{subject.name}" itemValue="#{subject.id}" />
    <f:ajax listener="#{dashboardBean.onSubjectChange}" render="documentList" />
</h:selectOneMenu>

<!-- Document list -->
<h:panelGroup id="documentList">
    <ui:repeat value="#{dashboardBean.documents}" var="doc">
        <div class="document-card">
            <h5>#{doc.fileName}</h5>
            <p>Uploaded: #{doc.uploadedAt}</p>
            <span class="badge">#{doc.status}</span>
            
            <h:commandButton value="Open Chat" 
                             action="#{dashboardBean.openChat(doc.id)}" 
                             styleClass="btn btn-primary"
                             disabled="#{doc.status != 'indexed'}" />
            
            <h:commandButton value="Generate Flashcards" 
                             action="#{dashboardBean.generateFlashcards(doc.id)}"
                             styleClass="btn btn-secondary"
                             disabled="#{doc.status != 'indexed'}" />
        </div>
    </ui:repeat>
</h:panelGroup>
```

---

### Chat Page

**File:** `pages/chat.xhtml`

**Features:**
- Message history display
- User vs AI message differentiation
- Text input + Send button
- Viva Mode toggle
- Auto-scroll to latest message
- Loading indicator

**Key JSF Code:**

```xml
<div class="chat-container">
    <!-- Header -->
    <div class="chat-header">
        <h4>#{chatBean.activeDocumentName}</h4>
        <h:selectBooleanCheckbox id="vivaToggle" value="#{chatBean.vivaMode}">
            <f:ajax />
        </h:selectBooleanCheckbox>
        <label for="vivaToggle">Viva Mode</label>
    </div>
    
    <!-- Messages -->
    <h:panelGroup id="messagesPanel" styleClass="chat-messages">
        <ui:repeat value="#{chatBean.messages}" var="msg">
            <ui:include src="/components/chat-message.xhtml">
                <ui:param name="message" value="#{msg.content}" />
                <ui:param name="isUser" value="#{msg.isUser}" />
            </ui:include>
        </ui:repeat>
    </h:panelGroup>
    
    <!-- Input -->
    <h:form id="chatForm">
        <div class="chat-input">
            <h:inputText id="userMessage" 
                         value="#{chatBean.currentMessage}" 
                         placeholder="Ask a question..."
                         styleClass="form-control" />
            
            <h:commandButton id="sendBtn" 
                             value="Send" 
                             action="#{chatBean.sendMessage}"
                             styleClass="btn btn-primary">
                <f:ajax execute="userMessage" 
                        render="messagesPanel userMessage loadingIndicator" />
            </h:commandButton>
        </div>
        
        <h:panelGroup id="loadingIndicator" 
                      rendered="#{chatBean.loading}"
                      styleClass="loading-spinner">
            <i class="spinner"></i> AI is thinking...
        </h:panelGroup>
    </h:form>
</div>
```

**CSS for Auto-Scroll:**

```css
.chat-messages {
    max-height: 500px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
}

/* Auto-scroll to bottom */
.chat-messages:after {
    content: "";
    display: block;
    height: 1px;
}
```

**JavaScript Utility:**

```javascript
// src/main/webapp/resources/js/chat-utils.js

function scrollChatToBottom() {
    const chatPanel = document.querySelector('.chat-messages');
    if (chatPanel) {
        chatPanel.scrollTop = chatPanel.scrollHeight;
    }
}

// Call after AJAX update
document.addEventListener('DOMContentLoaded', scrollChatToBottom);
```

---

### Flashcard Page

**File:** `pages/flashcards.xhtml`

**Features:**
- Bootstrap grid layout (3 columns on desktop)
- Flip animation on click/keyboard
- Difficulty badges
- "Regenerate" button

**Key JSF Code:**

```xml
<div class="flashcard-grid">
    <ui:repeat value="#{flashcardBean.flashcards}" var="card">
        <div class="flashcard" 
             onclick="this.classList.toggle('flipped')"
             tabindex="0"
             onkeydown="if(event.key==='Enter' || event.key===' ') this.classList.toggle('flipped')">
            
            <div class="flashcard-inner">
                <!-- Front -->
                <div class="flashcard-front">
                    <div class="difficulty-badge #{card.difficulty}">
                        #{card.difficulty}
                    </div>
                    <p class="question">#{card.question}</p>
                </div>
                
                <!-- Back -->
                <div class="flashcard-back">
                    <p class="answer">#{card.answer}</p>
                </div>
            </div>
        </div>
    </ui:repeat>
</div>

<h:commandButton value="Regenerate Flashcards" 
                 action="#{flashcardBean.regenerate}"
                 styleClass="btn btn-warning"
                 onclick="return confirm('This will delete existing flashcards. Continue?')">
    <f:ajax render="flashcard-grid" />
</h:commandButton>
```

**CSS for Flip Animation:**

```css
/* src/main/webapp/resources/css/flashcard-flip.css */

.flashcard {
    width: 100%;
    height: 300px;
    perspective: 1000px;
    cursor: pointer;
    margin-bottom: 20px;
}

.flashcard-inner {
    position: relative;
    width: 100%;
    height: 100%;
    transition: transform 0.6s;
    transform-style: preserve-3d;
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
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: white;
}

.flashcard-back {
    transform: rotateY(180deg);
    background: #f8f9fa;
}

.question,
.answer {
    font-size: 18px;
    text-align: center;
}

.difficulty-badge {
    position: absolute;
    top: 10px;
    right: 10px;
    padding: 5px 10px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: bold;
}

.difficulty-badge.easy { background: #d4edda; color: #155724; }
.difficulty-badge.medium { background: #fff3cd; color: #856404; }
.difficulty-badge.hard { background: #f8d7da; color: #721c24; }

/* Responsive: 1 column on mobile */
@media (max-width: 767px) {
    .flashcard-grid {
        grid-template-columns: 1fr;
    }
}

/* 2 columns on tablet */
@media (min-width: 768px) and (max-width: 1199px) {
    .flashcard-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
    }
}

/* 3 columns on desktop */
@media (min-width: 1200px) {
    .flashcard-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 20px;
    }
}
```

---

## 🧪 Testing Guidelines

### Manual Testing Checklist

**Before Each Demo:**
- [ ] Login works with mock credentials
- [ ] Dashboard loads subjects and documents
- [ ] Upload shows progress bar (simulate with setTimeout)
- [ ] Chat messages appear without page reload
- [ ] Viva mode toggle changes response style
- [ ] Flashcards flip smoothly on click
- [ ] Flashcards respond to keyboard (Tab, Enter, Space)
- [ ] Session persists after browser refresh
- [ ] Logout clears session

**Cross-Browser Testing:**
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Edge (latest)
- [ ] Safari (if Mac available)

**Responsive Design:**
Test at these breakpoints:
- [ ] 320px (iPhone SE)
- [ ] 375px (iPhone 12)
- [ ] 768px (iPad Portrait)
- [ ] 1024px (iPad Landscape)
- [ ] 1920px (Desktop)

### Accessibility Testing

**Automated Scan:**
```bash
# Install axe DevTools browser extension
# Visit each page and run scan
# Fix all CRITICAL and SERIOUS violations
```

**Manual Keyboard Navigation:**
- [ ] Can tab through all interactive elements
- [ ] Focus indicators are visible
- [ ] Can submit forms with Enter key
- [ ] Can flip flashcards with keyboard
- [ ] No keyboard traps

**Screen Reader Testing (Optional but Recommended):**
- [ ] Install NVDA (Windows) or VoiceOver (Mac)
- [ ] Navigate through pages
- [ ] Verify form labels are announced
- [ ] Check ARIA live regions for chat updates

### JavaScript Validation Testing

**Test Scenarios:**

```javascript
// validation.js tests

// 1. PDF file type validation
function testFileValidation() {
    console.log("Testing file validation...");
    
    // Valid PDF
    assert(validateFile({name: "test.pdf", size: 5000000}), "PDF should pass");
    
    // Invalid extension
    assert(!validateFile({name: "test.docx", size: 5000000}), "DOCX should fail");
    
    // Too large
    assert(!validateFile({name: "test.pdf", size: 25000000}), "Large file should fail");
    
    console.log("File validation tests passed ✓");
}

// 2. Chat message validation
function testChatValidation() {
    console.log("Testing chat validation...");
    
    // Valid message
    assert(validateMessage("What is deadlock?"), "Valid message should pass");
    
    // Empty message
    assert(!validateMessage(""), "Empty message should fail");
    
    // Too long (>500 chars)
    assert(!validateMessage("a".repeat(501)), "Long message should fail");
    
    console.log("Chat validation tests passed ✓");
}

function assert(condition, message) {
    if (!condition) {
        throw new Error("Assertion failed: " + message);
    }
}
```

---

## 🔌 Backend Integration Preparation

### Integration Contracts Document

Create `docs/integration-contracts.md`:

```markdown
# Backend API Integration Contracts

This document specifies the exact API contracts that backend must implement.

## Authentication

### POST /api/auth/login

**Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response 200:**
```json
{
  "success": true,
  "userId": 123,
  "username": "student1",
  "redirect": "/dashboard.xhtml"
}
```

**Response 401:**
```json
{
  "success": false,
  "error": "Invalid credentials"
}
```

---

## Documents

### GET /api/subjects/{subjectId}/documents

**Response 200:**
```json
[
  {
    "id": 101,
    "fileName": "OS_Lecture_01.pdf",
    "uploadedAt": "2026-01-20T10:30:00Z",
    "status": "indexed | indexing | failed",
    "pageCount": 25
  }
]
```

---

## Chat

### POST /api/chat

**Request:**
```json
{
  "documentId": 101,
  "message": "What is process scheduling?",
  "vivaMode": false
}
```

**Response 200:**
```json
{
  "answer": "Process scheduling is...",
  "sources": [
    {"page": 12, "snippet": "Round-robin..."}
  ],
  "timestamp": "2026-01-24T14:22:00Z"
}
```

**Response 400 (No Document):**
```json
{
  "error": "No document selected or document not indexed"
}
```

**Response 500 (AI Error):**
```json
{
  "error": "AI service unavailable. Please try again."
}
```

---

## Flashcards

### GET /api/documents/{id}/flashcards

**Response 200:**
```json
[
  {
    "id": 501,
    "question": "What is a semaphore?",
    "answer": "A semaphore is...",
    "difficulty": "easy | medium | hard"
  }
]
```

---

## Expected Behavior

1. **Upload Flow:**
   - Frontend submits multipart/form-data
   - Backend saves file, extracts text, triggers RAG indexing
   - Returns 200 with status "indexing"
   - After indexing completes, status changes to "indexed"

2. **Chat Flow:**
   - Frontend sends question + documentId
   - Backend retrieves relevant chunks from Vector DB
   - Sends to LLM with context
   - Returns answer within 5 seconds (p95 latency)

3. **Flashcard Generation:**
   - Frontend triggers generation
   - Backend sends document context to LLM
   - LLM returns JSON array of Q&A pairs
   - Backend parses and saves to database
   - Returns flashcards array

## Error Handling

All endpoints should return:
- **200** for success
- **400** for client errors (validation, missing params)
- **401** for authentication failures
- **500** for server errors (AI service down, DB error)

Error responses should always include:
```json
{ "error": "Human-readable error message" }
```
```

### Switching from Mock to Real API

When backend is ready:

**Step 1:** Replace mock services with REST client

```java
// Before (Mock)
@Inject
private MockChatService chatService;

// After (Real API)
@Inject
private ChatAPIClient chatApiClient;
```

**Step 2:** Update managed beans to call real endpoints

```java
public void sendMessage() {
    loading = true;
    
    // Call real API
    ChatResponse response = chatApiClient.sendMessage(
        activeDocumentId, 
        currentMessage, 
        vivaMode
    );
    
    messages.add(new ChatMessage(currentMessage, true));
    messages.add(new ChatMessage(response.getAnswer(), false));
    
    currentMessage = "";
    loading = false;
}
```

**Step 3:** Remove mock JSON files

```bash
rm -rf src/main/resources/mock-data/
```

**Step 4:** Test integration

- Verify all API endpoints return expected format
- Handle new error cases (network timeout, 500 errors)
- Update error messages for production

---

## 🎓 Viva Preparation

### Key Talking Points

**Q: Why did you choose JSF instead of React or Angular?**
> "JavaServer Faces was mandated by faculty requirements to demonstrate understanding of Java EE web frameworks. JSF provides stateful component management, which is ideal for maintaining chat history and session context without client-side state management complexity."

**Q: How does your frontend communicate with the backend?**
> "We defined clear API contracts in JSON format. During development, we used mock services to simulate backend responses. When backend is ready, we'll swap mock services with REST API clients without changing any UI code."

**Q: How do you ensure the UI updates without full page reloads?**
> "We use JSF's `<f:ajax>` tag to execute partial page updates. For example, when sending a chat message, we only re-render the messages panel and input field, not the entire page. This provides a smooth, single-page application experience."

**Q: How did you implement the flashcard flip animation?**
> "The flip is pure CSS using `transform: rotateY(180deg)` with `preserve-3d` for the 3D effect. We toggle a 'flipped' class via JavaScript on click or keyboard press. This achieves 60fps animation with no JavaScript animation libraries."

**Q: How do you handle users with disabilities?**
> "We follow WCAG 2.1 AA standards. All interactive elements are keyboard accessible, we use semantic HTML, provide ARIA labels for icons, and ensure color contrast meets minimum ratios. We tested with axe DevTools and fixed all critical violations."

**Q: What happens if a user uploads a non-PDF file?**
> "We validate client-side first using JavaScript to check the file extension. If it's not a .pdf, we show an error immediately without attempting upload. The backend will also validate server-side for security, but client-side validation provides instant UX feedback."

**Q: How does Viva Mode differ from normal chat?**
> "Viva Mode is a boolean flag we send with each chat request. The backend uses this to switch the AI's system prompt from 'helpful assistant' to 'university examiner.' The frontend UI just toggles the flag and shows a visual indicator—all prompt engineering happens on the backend."

**Q: How do you maintain session state?**
> "We use session-scoped managed beans (`@SessionScoped`) to store the active document ID, chat history, and user preferences. This data persists across page navigations within the same browser session. We also use cookies to store the last-viewed subject ID for auto-restore on re-login."

---

### Demo Script (3 Minutes)

**[0:00-0:30] Introduction & Login**
- Open browser to login page
- "Study-Sync AI helps students interact with course materials through AI chat and flashcards"
- Enter mock credentials: `student1` / `password`
- Click Login → redirects to dashboard

**[0:30-1:15] Dashboard & Upload**
- "This is the dashboard. Let me select Operating Systems subject"
- Select subject → document list updates (AJAX, no reload)
- "Now I'll upload a PDF. Watch the validation..."
- Try to drag a .docx file → "See the error: only PDFs accepted"
- Drag a .pdf file → progress bar appears
- "In production, this triggers text extraction and RAG indexing"
- Document appears in list with 'indexed' status

**[1:15-2:00] Chat Interface**
- Click "Open Chat" on uploaded document
- "The chat interface is context-aware to this specific PDF"
- Type question: "What is process scheduling?"
- Click Send → message appears instantly, loading spinner shows
- AI response appears (from mock data) → no page reload
- "Now let me toggle Viva Mode..."
- Toggle switch → visual indicator appears
- Ask same question → response style changes to examiner tone

**[2:00-2:45] Flashcards**
- Navigate to dashboard, click "Generate Flashcards"
- Loading screen: "Analyzing document..."
- Flashcard grid appears (3 columns on desktop)
- Click a card → smooth 3D flip reveals answer
- "Watch keyboard navigation..."
- Press Tab → focus moves to next card
- Press Enter → card flips

**[2:45-3:00] Session Persistence**
- Refresh browser (F5)
- "Notice the subject and document are still selected"
- "Chat history persists too—this is session management"
- "Thank you! Questions?"

---

## 🐛 Troubleshooting

### Common Frontend Issues

**Problem: JSF page shows blank/white screen**
```
Solution:
1. Check browser console for JavaScript errors
2. Verify Faces Servlet is mapped in web.xml
3. Check Tomcat logs: tail -f $TOMCAT_HOME/logs/catalina.out
4. Ensure all XHTML files are in src/main/webapp/
5. Try accessing with .xhtml extension: /pages/login.xhtml
```

**Problem: AJAX doesn't update components**
```
Solution:
1. Verify component IDs match in execute and render attributes
2. Check if managed bean is in correct scope (use @SessionScoped for chat)
3. Look for JavaScript errors in browser console
4. Add <f:ajax event="..." execute="@form" render="componentId" />
5. Ensure component has a valid ID (no colons or special chars)
```

**Problem: Flashcard flip animation is jerky**
```
Solution:
1. Check if browser supports CSS transforms (IE11 doesn't)
2. Add will-change: transform; to .flashcard-inner
3. Use Chrome DevTools Performance tab to profile
4. Reduce other animations running simultaneously
5. Test in incognito mode (extensions can interfere)
```

**Problem: Upload progress bar doesn't show**
```
Solution:
1. Check if JavaScript file is loaded: view-source and search for validation.js
2. Verify file path: /resources/js/validation.js
3. Look for console errors
4. Test with setTimeout to simulate delay:
   setTimeout(() => showProgress(50), 1000);
```

**Problem: Session timeout too fast**
```
Solution:
1. Edit web.xml: <session-timeout>1440</session-timeout> (24 hours in minutes)
2. Restart Tomcat
3. Clear browser cookies
4. Test by waiting 30 minutes and refreshing
```

**Problem: Mock data not loading**
```
Solution:
1. Verify JSON files are in src/main/resources/mock-data/
2. Check file names match exactly (case-sensitive)
3. Validate JSON syntax: jsonlint.com
4. Check if Gson dependency is in pom.xml
5. Look for FileNotFoundException in logs
```

**Problem: Managed bean not found**
```
Solution:
1. Ensure class has @Named annotation
2. Check scope annotation (@RequestScoped, @SessionScoped, etc.)
3. Verify bean name matches EL expression: #{loginBean.method}
4. Rebuild project: mvn clean install
5. Redeploy WAR file
```

---

## 📝 Git Commit Message Conventions

Use semantic commit messages:

```bash
# Feature additions
git commit -m "feat: add flashcard flip animation"

# Bug fixes
git commit -m "fix: resolve AJAX render issue in chat"

# Documentation
git commit -m "docs: update integration contracts"

# Styling/CSS
git commit -m "style: improve responsive layout for mobile"

# Refactoring
git commit -m "refactor: simplify DashboardBean logic"

# Testing
git commit -m "test: add validation test cases"

# Build/dependency updates
git commit -m "chore: update JSF version to 2.3.14"
```

---

## 🔐 Security Best Practices (Frontend)

**Input Sanitization:**
```java
// Always escape user input in JSF
<h:outputText value="#{message.content}" escape="true" />

// For HTML content (use with caution)
<h:outputText value="#{message.content}" escape="false" />
```

**XSS Prevention:**
```javascript
// Bad: innerHTML with user input
element.innerHTML = userInput; // NEVER DO THIS

// Good: Use textContent
element.textContent = userInput;
```

**File Upload Validation:**
```javascript
function validateFile(file) {
    // Client-side validation (UX only, not security)
    const allowedTypes = ['application/pdf'];
    const maxSize = 20 * 1024 * 1024; // 20MB
    
    if (!allowedTypes.includes(file.type)) {
        showError("Only PDF files are allowed");
        return false;
    }
    
    if (file.size > maxSize) {
        showError("File must be under 20MB");
        return false;
    }
    
    return true;
}
```

**Never Store Sensitive Data:**
```javascript
// BAD: Don't do this
localStorage.setItem('password', userPassword);
sessionStorage.setItem('apiKey', secretKey);

// GOOD: Let backend handle sensitive data
// Frontend should only store non-sensitive UI state
```

---

## 📞 Support & Resources

**Team Communication:**
- Slack: `#study-sync-frontend`
- Email: frontend-team@youruniversity.edu
- Weekly Meetings: Fridays 3 PM (Room 204)

**External Resources:**
- **JSF Documentation**: https://eclipse-ee4j.github.io/faces/
- **Bootstrap 5 Docs**: https://getbootstrap.com/docs/5.0/
- **WCAG Guidelines**: https://www.w3.org/WAI/WCAG21/quickref/
- **MDN Web Docs**: https://developer.mozilla.org/

**Tutorials:**
- JSF Tutorial: https://www.tutorialspoint.com/jsf/
- Bootstrap Grid System: https://getbootstrap.com/docs/5.0/layout/grid/
- CSS Animations: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations

---

## 📋 Quick Reference

### File Naming Conventions
- Pages: `lowercase-with-dashes.xhtml` (e.g., `chat.xhtml`)
- Components: `component-name.xhtml` (e.g., `upload-component.xhtml`)
- Managed Beans: `PascalCase.java` (e.g., `ChatBean.java`)
- CSS files: `lowercase-with-dashes.css` (e.g., `flashcard-flip.css`)
- JavaScript: `camelCase.js` (e.g., `chatUtils.js`)

### Color Palette (Default - Update with your design system)
```css
:root {
    --primary: #007bff;
    --secondary: #6c757d;
    --success: #28a745;
    --danger: #dc3545;
    --warning: #ffc107;
    --info: #17a2b8;
    --light: #f8f9fa;
    --dark: #343a40;
}
```

### Typography Scale
```css
h1 { font-size: 2.5rem; }   /* 40px */
h2 { font-size: 2rem; }     /* 32px */
h3 { font-size: 1.75rem; }  /* 28px */
h4 { font-size: 1.5rem; }   /* 24px */
h5 { font-size: 1.25rem; }  /* 20px */
body { font-size: 1rem; }   /* 16px */
small { font-size: 0.875rem; } /* 14px */
```

### Spacing Scale (Bootstrap 5)
- `.m-1` = 0.25rem (4px)
- `.m-2` = 0.5rem (8px)
- `.m-3` = 1rem (16px)
- `.m-4` = 1.5rem (24px)
- `.m-5` = 3rem (48px)

---

## 📜 License

This project is for **academic purposes only**.  
Not for commercial use.

---

**Last Updated:** January 24, 2026  
**Maintained By:** Study-Sync AI Frontend Team  
**Contact:** frontend-lead@youruniversity.edu