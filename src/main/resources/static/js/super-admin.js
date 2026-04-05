const msgEl = document.getElementById('msg');

async function loadAnalytics() {
    const a = await apiCall('/superadmin/analytics');
    document.getElementById('analytics').innerHTML = `Total: ${a.totalRequests} | Pending: ${a.pendingRequests} | Completed: ${a.completedRequests}`;
}

async function loadAdmins() {
    const data = await apiCall('/superadmin/admins?status=PENDING&page=0&size=20');
    document.getElementById('adminsBody').innerHTML = data.content.map(a => `
      <tr>
        <td>${a.id}</td><td>${a.name}</td><td>${a.email}</td><td>${a.status}</td><td>${a.location || ''}</td>
        <td>
          <button style="width:auto;" onclick="approveAdmin(${a.id})">Approve</button>
          <button style="width:auto;" class="danger" onclick="rejectAdmin(${a.id})">Reject</button>
        </td>
      </tr>
    `).join('');
}

async function approveAdmin(id) {
    const location = prompt('Assign location for this local admin:');
    if (!location) return;
    await apiCall(`/superadmin/admin/${id}/approve`, 'PUT', { location });
    showMessage(msgEl, `Admin ${id} approved`);
    loadAdmins();
}

async function rejectAdmin(id) {
    await apiCall(`/superadmin/admin/${id}/reject`, 'PUT');
    showMessage(msgEl, `Admin ${id} rejected`);
    loadAdmins();
}

async function loadRequests() {
    const status = document.getElementById('statusFilter').value;
    const location = document.getElementById('locationFilter').value;
    const query = new URLSearchParams({ page: '0', size: '20' });
    if (status) query.append('status', status);
    if (location) query.append('location', location);
    const data = await apiCall('/superadmin/all-requests?' + query.toString());

    document.getElementById('requestsBody').innerHTML = data.content.map(r => `
      <tr>
        <td>${r.id}</td><td>${r.userName}</td><td>${r.location}</td><td><span class="badge ${r.status}">${r.status}</span></td>
        <td>
            <select onchange="overrideStatus(${r.id}, this.value)">
                <option value="">Override</option><option value="APPROVED">Approve</option><option value="REJECTED">Reject</option><option value="COMPLETED">Complete</option>
            </select>
        </td>
      </tr>
    `).join('');
}

async function overrideStatus(id, status) {
    if (!status) return;
    await apiCall(`/superadmin/request/${id}/status`, 'PUT', { status });
    showMessage(msgEl, `Request #${id} overridden to ${status}`);
    loadRequests();
    loadAnalytics();
}

document.getElementById('filterBtn').addEventListener('click', loadRequests);
document.getElementById('logoutBtn').addEventListener('click', async () => {
    await apiCall('/auth/logout', 'POST');
    window.location.href = '/index.html';
});

loadAnalytics().then(loadAdmins).then(loadRequests).catch(e => {
    showMessage(msgEl, e.message, true);
    setTimeout(() => window.location.href = '/index.html', 1200);
});
