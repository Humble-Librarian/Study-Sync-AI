// UI Utilities - Toast Notifications & Loading States
// Per Design Doc Section 4.7

// Toast Container
function createToastContainer() {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    return container;
}

// Show Toast Notification
function showToast(message, type = 'info', duration = 5000) {
    const container = createToastContainer();

    const icons = {
        success: '✓',
        error: '✕',
        warning: '⚠',
        info: 'ℹ'
    };

    const titles = {
        success: 'Success',
        error: 'Error',
        warning: 'Warning',
        info: 'Info'
    };

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'polite');
    toast.innerHTML = `
        <div class="toast-icon">${icons[type] || icons.info}</div>
        <div class="toast-content">
            <div class="toast-title">${titles[type] || titles.info}</div>
            <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close" onclick="this.parentElement.remove()" aria-label="Close notification">×</button>
    `;

    container.appendChild(toast);

    // Auto-remove after duration
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease-in forwards';
        setTimeout(() => toast.remove(), 300);
    }, duration);

    return toast;
}

// Loading Spinner
function showLoading(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        el.classList.add('loading');
        el.setAttribute('aria-busy', 'true');
    }
}

function hideLoading(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        el.classList.remove('loading');
        el.setAttribute('aria-busy', 'false');
    }
}

// Focus Management for Accessibility
function trapFocus(element) {
    const focusableElements = element.querySelectorAll(
        'a[href], button, textarea, input, select, [tabindex]:not([tabindex="-1"])'
    );
    const firstFocusable = focusableElements[0];
    const lastFocusable = focusableElements[focusableElements.length - 1];

    element.addEventListener('keydown', function (e) {
        if (e.key === 'Tab') {
            if (e.shiftKey && document.activeElement === firstFocusable) {
                e.preventDefault();
                lastFocusable.focus();
            } else if (!e.shiftKey && document.activeElement === lastFocusable) {
                e.preventDefault();
                firstFocusable.focus();
            }
        }
    });
}

// Announce to Screen Readers
function announceToScreenReader(message) {
    const announcer = document.getElementById('sr-announcer') || createAnnouncer();
    announcer.textContent = message;
}

function createAnnouncer() {
    const announcer = document.createElement('div');
    announcer.id = 'sr-announcer';
    announcer.className = 'sr-only';
    announcer.setAttribute('aria-live', 'polite');
    announcer.setAttribute('aria-atomic', 'true');
    document.body.appendChild(announcer);
    return announcer;
}
