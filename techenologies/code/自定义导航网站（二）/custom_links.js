// Load custom links when the page loads
loadCustomLinks();
// Function to load custom links from localStorage
function loadCustomLinks() {
    const customSavedCustomPages = JSON.parse(localStorage.getItem('savedCustomPages'));
    const customLinksList = document.getElementById('customLinksList');

    if (customSavedCustomPages && customSavedCustomPages.length > 0) {
        customLinksList.innerHTML = '';
        customSavedCustomPages.forEach(page => {
            const listItem = document.createElement('li');

            const nameElement = document.createElement('span');
            nameElement.textContent = page.name;
            nameElement.style.cursor = 'pointer';
            nameElement.onclick = function (event) {
                event.stopPropagation();
                openCustomPage(page.link);
            };

            const input = document.createElement('input');
            input.onkeydown = function (event) {
                if (event.key === 'Enter') {
                    openCustomPage(page.link + input.value);
                }
            }

            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.onclick = function (event) {
                event.stopPropagation();
                deleteCustomPage(page);
            };

            listItem.appendChild(nameElement);
            listItem.appendChild(input);
            listItem.appendChild(deleteButton);
            customLinksList.appendChild(listItem);
        });
    } else {
        customLinksList.innerHTML = '<li>No saved pages</li>';
    }
}

// Function to delete a page
function deleteCustomPage(page) {
    const savedCustomPages = JSON.parse(localStorage.getItem('savedCustomPages'));
    const updatedPages = savedCustomPages.filter(p => p.name !== page.name);
    localStorage.setItem('savedCustomPages', JSON.stringify(updatedPages));
    loadCustomLinks();
}

// Function to open a page in a new tab
function openCustomPage(link) {
    window.open(link, '_blank');
}

// Function to handle form submission
document.getElementById('addCustomPageForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const customPageName = document.getElementById('customPageName').value;
    const pagePrefix = document.getElementById('pagePrefix').value;

    if (customPageName && pagePrefix) {
        const savedCustomPages = JSON.parse(localStorage.getItem('savedCustomPages')) || [];
        savedCustomPages.push({ name: customPageName, link: pagePrefix });
        localStorage.setItem('savedCustomPages', JSON.stringify(savedCustomPages));

        loadCustomLinks();

        // Reset form fields
        document.getElementById('customPageName').value = '';
        document.getElementById('pagePrefix').value = '';
    } else {
        console.error('Page name and link are required.');
    }
});

// Function to clear all pages
function clearAllCustomPages() {
    localStorage.removeItem('savedCustomPages');
    loadCustomLinks();
}

// Function to export all pages to a file
function exportAllCustomPages() {
    const savedCustomPages = JSON.parse(localStorage.getItem('savedCustomPages'));

    if (savedCustomPages && savedCustomPages.length > 0) {
        const savedCustomPagesJSON = JSON.stringify(savedCustomPages, null, 4);
        const blob = new Blob([savedCustomPagesJSON], { type: 'application/json' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'savedCustomPages.json';
        a.click();
    } else {
        console.error('No saved pages found.');
    }
}

// Function to handle file import
function importCustomPagesFile() {
    const customFileInput = document.getElementById('customFileInput');
    const file = customFileInput.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (event) {
            const importedPages = JSON.parse(event.target.result);
            if (Array.isArray(importedPages)) {
                const savedCustomPages = JSON.parse(localStorage.getItem('savedCustomPages')) || [];
                const updatedPages = savedCustomPages.concat(importedPages);
                localStorage.setItem('savedCustomPages', JSON.stringify(updatedPages));
                loadCustomLinks();
                // Clear file input value
                customFileInput.value = '';
            } else {
                console.error('Invalid JSON format.');
            }
        };
        reader.readAsText(file);
    } else {
        console.error('No file selected.');
    }
}