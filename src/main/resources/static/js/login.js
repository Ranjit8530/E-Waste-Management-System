document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    const msg = document.getElementById('msg');

    try {
        const data = await apiCall('/auth/login', 'POST', {
            email: form.email.value,
            password: form.password.value
        });

        showMessage(msg, data.message);
        setTimeout(() => {
            if (data.role === 'SUPER_ADMIN') window.location.href = '/super-admin.html';
            else if (data.role === 'LOCAL_ADMIN') window.location.href = '/admin.html';
            else window.location.href = '/dashboard.html';
        }, 500);
    } catch (error) {
        showMessage(msg, error.message, true);
    }
});
