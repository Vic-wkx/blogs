// Load links when the page loads
loadLinks();
// Function to load links from localStorage
function loadLinks() {
    const savedPages = JSON.parse(localStorage.getItem('savedPages'));
    const linksList = document.getElementById('linksList');

    if (savedPages && savedPages.length > 0) {
        linksList.innerHTML = '';
        savedPages.forEach(page => {
            const listItem = document.createElement('li');

            const nameElement = document.createElement('span');
            nameElement.textContent = page.name;
            nameElement.style.cursor = 'pointer';
            nameElement.onclick = function (event) {
                event.stopPropagation();
                openPage(page.link);
            };

            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.onclick = function (event) {
                event.stopPropagation();
                deletePage(page);
            };

            listItem.appendChild(nameElement);
            listItem.appendChild(deleteButton);
            linksList.appendChild(listItem);
        });
    } else {
        linksList.innerHTML = '<li>No saved pages</li>';
    }
}

// Function to delete a page
function deletePage(page) {
    const savedPages = JSON.parse(localStorage.getItem('savedPages'));
    const updatedPages = savedPages.filter(p => p.name !== page.name);
    localStorage.setItem('savedPages', JSON.stringify(updatedPages));
    loadLinks();
}

// Function to open a page in a new tab
function openPage(link) {
    window.open(link, '_blank');
}

// Function to handle form submission
document.getElementById('addPageForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const pageName = document.getElementById('pageName').value;
    const pageLink = document.getElementById('pageLink').value;

    if (pageName && pageLink) {
        const savedPages = JSON.parse(localStorage.getItem('savedPages')) || [];
        savedPages.push({ name: pageName, link: pageLink });
        localStorage.setItem('savedPages', JSON.stringify(savedPages));

        loadLinks();

        // Reset form fields
        document.getElementById('pageName').value = '';
        document.getElementById('pageLink').value = '';
    } else {
        console.error('Page name and link are required.');
    }
});

// Function to clear all pages
function clearAll() {
    localStorage.removeItem('savedPages');
    loadLinks();
}

// Function to export all pages to a file
function exportAll() {
    const savedPages = JSON.parse(localStorage.getItem('savedPages'));

    if (savedPages && savedPages.length > 0) {
        const savedPagesJSON = JSON.stringify(savedPages, null, 4);
        const blob = new Blob([savedPagesJSON], { type: 'application/json' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'savedPages.json';
        a.click();
    } else {
        console.error('No saved pages found.');
    }
}

// Function to handle file import
function importFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (event) {
            const importedPages = JSON.parse(event.target.result);
            if (Array.isArray(importedPages)) {
                const savedPages = JSON.parse(localStorage.getItem('savedPages')) || [];
                const updatedPages = savedPages.concat(importedPages);
                localStorage.setItem('savedPages', JSON.stringify(updatedPages));
                loadLinks();
                // Clear file input value
                fileInput.value = '';
            } else {
                console.error('Invalid JSON format.');
            }
        };
        reader.readAsText(file);
    } else {
        console.error('No file selected.');
    }
}