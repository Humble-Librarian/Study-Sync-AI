// Upload validation functions
function validateFile(input) {
    const file = input.files[0];
    if (!file) return false;

    const maxSize = 20 * 1024 * 1024; // 20MB
    const allowedTypes = ['application/pdf'];

    // Check type
    if (!allowedTypes.includes(file.type)) {
        showError('Only PDF files are allowed.');
        input.value = '';
        return false;
    }

    // Check size
    if (file.size > maxSize) {
        showError('File size must be under 20MB.');
        input.value = '';
        return false;
    }

    // Show file info
    updateFileInfo(file.name, formatSize(file.size));
    return true;
}

function formatSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
}

function updateFileInfo(name, size) {
    const nameEl = document.getElementById('fileName');
    const sizeEl = document.getElementById('fileSize');
    const infoEl = document.querySelector('.file-info');
    const uploadBtn = document.getElementById('uploadBtn');

    if (nameEl) nameEl.textContent = name;
    if (sizeEl) sizeEl.textContent = size;
    if (infoEl) infoEl.style.display = 'block';
    if (uploadBtn) uploadBtn.style.display = 'inline-block';
}

function showError(message) {
    alert(message); // Can be replaced with toast later
}

// Drag and Drop handlers
function handleDragOver(event) {
    event.preventDefault();
    event.currentTarget.classList.add('drag-over');
}

function handleDragLeave(event) {
    event.currentTarget.classList.remove('drag-over');
}

function handleDrop(event) {
    event.preventDefault();
    event.currentTarget.classList.remove('drag-over');

    const files = event.dataTransfer.files;
    if (files.length > 0) {
        const input = document.querySelector('input[type="file"]');
        if (input) {
            input.files = files;
            validateFile(input);
        }
    }
}

// Progress bar simulation
function simulateProgress() {
    const progressBar = document.getElementById('progressBar');
    const progressContainer = document.querySelector('.progress-container');

    if (progressContainer) progressContainer.style.display = 'block';

    let width = 0;
    const interval = setInterval(function () {
        if (width >= 100) {
            clearInterval(interval);
        } else {
            width += 10;
            if (progressBar) progressBar.style.width = width + '%';
        }
    }, 200);
}
