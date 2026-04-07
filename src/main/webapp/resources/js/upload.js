function byIdOrSuffix(id) {
    return document.getElementById(id) || document.querySelector(`[id$="${id}"]`);
}

function triggerFilePicker() {
    const input = byIdOrSuffix('fileInput');
    if (input) {
        input.click();
    } else {
        showError('File input not found.');
    }
}

function onFileSelected(input) {
    const isValid = validateFile(input);
    const uploadBtn = byIdOrSuffix('uploadBtn');
    const card = byIdOrSuffix('selectedFileCard');

    if (!uploadBtn) {
        return;
    }

    if (isValid) {
        uploadBtn.style.display = 'inline-flex';
        if (card) {
            card.style.display = 'block';
        }
    } else {
        uploadBtn.style.display = 'none';
        if (card) {
            card.style.display = 'none';
        }
    }
}

function validateFile(input) {
    const file = input && input.files ? input.files[0] : null;
    if (!file) {
        return false;
    }

    const maxSize = 20 * 1024 * 1024;
    const name = (file.name || '').toLowerCase();
    const type = (file.type || '').toLowerCase();
    const isPdf = name.endsWith('.pdf') || type === 'application/pdf';

    if (!isPdf) {
        showError('Only PDF files are allowed.');
        input.value = '';
        return false;
    }

    if (file.size > maxSize) {
        showError('File size must be under 20MB.');
        input.value = '';
        return false;
    }

    updateFileInfo(file.name, formatSize(file.size));
    return true;
}

function formatSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
}

function updateFileInfo(name, size) {
    const nameEl = byIdOrSuffix('fileName');
    const sizeEl = byIdOrSuffix('fileSize');

    if (nameEl) nameEl.textContent = name;
    if (sizeEl) sizeEl.textContent = size;
}

function showError(message) {
    alert(message);
}
