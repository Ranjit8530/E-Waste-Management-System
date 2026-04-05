const roleSelect = document.getElementById('role');
const locationWrap = document.getElementById('locationWrap');
const locationInput = document.getElementById('location');

roleSelect.addEventListener('change', () => {
    const isLocalAdmin = roleSelect.value === 'LOCAL_ADMIN';
    locationWrap.style.display = isLocalAdmin ? 'block' : 'none';
    locationInput.required = isLocalAdmin;
});

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    const msg = document.getElementById('msg');

    if (form.password.value.length < 8) {
        showMessage(msg, 'Password must be at least 8 characters', true);
        return;
    }

    try {
        const payload = {
            name: form.name.value,
            email: form.email.value,
            password: form.password.value,
            phone: form.phone.value,
            address: form.address.value,
            role: form.role.value
        };
        if (form.role.value === 'LOCAL_ADMIN') payload.location = form.location.value;

        const data = await apiCall('/auth/register', 'POST', payload);
        showMessage(msg, data.message + '. You can login once active.');
        form.reset();
        locationWrap.style.display = 'none';
    } catch (error) {
        showMessage(msg, error.message, true);
    }
});
