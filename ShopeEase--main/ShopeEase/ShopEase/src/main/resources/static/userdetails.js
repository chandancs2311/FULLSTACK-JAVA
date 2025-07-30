document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes("userdetails.html")) {
    loadAllUsers();
  }
});

async function apiRequest(method, path) {
  const token = sessionStorage.getItem("token");
  const res = await fetch(`http://localhost:8080/api/user${path}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    }
  });
  if (!res.ok) throw new Error("API request failed");
  return res.json();
}

async function loadAllUsers() {
  try {
    const res = await apiRequest('GET', '/admin/profile');

    const container = document.getElementById('userList');
    container.innerHTML = '';

    res.allUsers.forEach(user => {
      const userBox = document.createElement('div');
      userBox.className = 'user-box';

      userBox.innerHTML = `
        <h3>${user.userName} (ID: ${user.userId})</h3>
        <p><strong>Email:</strong> ${user.email}</p>

        <div class="cart-section">
          <h4>Cart Items</h4>
          ${user.cartItems.length > 0 ? `
            <table class="data-table">
              <tr><th>Product</th><th>Qty</th><th>Price/Unit</th><th>Total</th></tr>
              ${user.cartItems.map(item => `
                <tr>
                  <td>${item.productName}</td>
                  <td>${item.quantity}</td>
                  <td>${item.pricePerUnit ?? 'N/A'}</td>
                  <td>${item.totalItemPrice ?? 'N/A'}</td>
                </tr>
              `).join('')}
            </table>
          ` : `<p>No items in cart.</p>`}
        </div>

        <div class="order-section">
          <h4>Order Items</h4>
          ${user.orderItems.length > 0 ? `
            <table class="data-table">
              <tr><th>Product</th><th>Qty</th><th>Price</th><th>Total</th></tr>
              ${user.orderItems.map(order => `
                <tr>
                  <td>${order.productName}</td>
                  <td>${order.quantity}</td>
                  <td>${order.price}</td>
                  <td>${order.totalPrice}</td>
                </tr>
              `).join('')}
            </table>
          ` : `<p>No orders found.</p>`}
        </div>
      `;

      container.appendChild(userBox);
    });

  } catch (error) {
    console.error("Error loading users:", error);
    alert("Failed to fetch user data. Login again.");
    window.location.href = "login.html";
  }
}
