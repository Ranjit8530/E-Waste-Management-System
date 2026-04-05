const msgEl = document.getElementById('msg');

async function loadRequests(page = 0) {
    try {
        const data = await apiCall(`/user/requests?page=${page}&size=10`);
        document.getElementById('requestsBody').innerHTML = data.content.map(r => `
            <tr><td>${r.id}</td><td>${r.deviceType}</td><td>${r.location}</td><td>${r.pickupDate}</td><td><span class="badge ${r.status}">${r.status}</span></td></tr>
        `).join('');
    } catch (error) {
        showMessage(msgEl, error.message, true);
        setTimeout(() => window.location.href = '/index.html', 1000);
    }
}

document.getElementById('requestForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;

    try {
        await apiCall('/requests', 'POST', {
            deviceType: form.deviceType.value,
            quantity: Number(form.quantity.value),
            description: form.description.value,
            pickupAddress: form.pickupAddress.value,
            location: form.location.value,
            pickupDate: form.pickupDate.value
        });
        showMessage(msgEl, 'Pickup request submitted successfully');
        form.reset();
        loadRequests();
    } catch (error) {
        showMessage(msgEl, error.message, true);
    }
});

document.getElementById('logoutBtn').addEventListener('click', async () => {
    await apiCall('/auth/logout', 'POST');
    window.location.href = '/index.html';
});

loadRequests();
