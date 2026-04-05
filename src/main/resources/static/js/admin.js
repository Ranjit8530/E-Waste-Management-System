const msgEl = document.getElementById('msg');

async function loadAnalytics() {
    const a = await apiCall('/admin/analytics');
    document.getElementById('analytics').innerHTML = `Total: ${a.totalRequests} | Pending: ${a.pendingRequests} | Completed: ${a.completedRequests}`;
}

async function loadRequests() {
    const status = document.getElementById('statusFilter').value;
    const keyword = document.getElementById('keyword').value;
    const query = new URLSearchParams({ page: '0', size: '20' });
    if (status) query.append('status', status);
    if (keyword) query.append('keyword', keyword);

    const data = await apiCall('/admin/requests?' + query.toString());
    document.getElementById('requestsBody').innerHTML = data.content.map(r => `
        <tr>
            <td>${r.id}</td><td>${r.userName}<br><small>${r.userEmail}</small></td><td>${r.location}</td>
            <td><span class="badge ${r.status}">${r.status}</span></td>
            <td>
                <select onchange="updateStatus(${r.id}, this.value)">
                    <option value="">Change</option><option value="APPROVED">Approve</option><option value="REJECTED">Reject</option><option value="COMPLETED">Completed</option>
                </select>
            </td>
        </tr>
    `).join('');
}

async function updateStatus(id, status) {
    if (!status) return;
    await apiCall(`/admin/request/${id}/status`, 'PUT', { status, assignToMe: true });
    showMessage(msgEl, `Updated request #${id}`);
    loadRequests();
    loadAnalytics();
}

document.getElementById('filterBtn').addEventListener('click', loadRequests);
document.getElementById('logoutBtn').addEventListener('click', async () => {
    await apiCall('/auth/logout', 'POST');
    window.location.href = '/index.html';
});

loadAnalytics().then(loadRequests).catch(e => {
    showMessage(msgEl, e.message, true);
    setTimeout(() => window.location.href = '/index.html', 1200);
});
