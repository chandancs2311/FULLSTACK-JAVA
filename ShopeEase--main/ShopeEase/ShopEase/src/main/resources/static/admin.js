async function loadAllUsers() {
  try {
    const res = await apiRequest('GET', '/admin/profile');

    // If response has error key or no users
    if (!res.allUsers || !Array.isArray(res.allUsers)) {
      alert("No users found or access denied.");
      return;
    }

    const users = res.allUsers;

    const container = document.getElementById('userList');
    container.innerHTML = users.map(user => `
      <li><strong>${user.name}</strong> - ${user.email}</li>
    `).join('');

  } catch (error) {
    console.error("Failed to load users:", error);
    alert("Error loading users. You may not be authorized.");
    // Optionally redirect to login if token is missing
    if (!sessionStorage.getItem('token')) {
      window.location.href = 'login.html';
    }
  }
}
