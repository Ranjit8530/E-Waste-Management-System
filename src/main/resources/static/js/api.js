async function apiCall(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    };

    if (body) options.body = JSON.stringify(body);

    const response = await fetch(url, options);
    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(data.message || JSON.stringify(data) || 'Something went wrong');
    }
    return data;
}

function showMessage(el, message, isError = false) {
    el.innerHTML = `<div class="message ${isError ? 'error' : 'success'}">${message}</div>`;
}
