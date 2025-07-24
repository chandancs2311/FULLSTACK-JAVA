async function loadCart() {
  const userId = sessionStorage.getItem("userId");
  const cartId = sessionStorage.getItem("cartId");

  if (!userId || !cartId) {
    alert("Cart information missing. Please log in again.");
    window.location.href = "login.html";
    return;
  }

  const items = await apiRequest('GET', `/user/cart/items/${userId}/${cartId}`);
  const container = document.getElementById('cartItems');
  container.innerHTML = items.map(item => `
    <div>
      <p>${item.productName} - ${item.quantity} x ₹${item.price}</p>
    </div>
  `).join('');
}
