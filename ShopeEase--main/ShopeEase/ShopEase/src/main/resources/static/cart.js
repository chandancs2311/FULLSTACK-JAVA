const API_BASE = "http://localhost:8080/api/user";
const CART_BASE = `${API_BASE}/cart`;

function requireAuth() {
  const token = sessionStorage.getItem("token");
  const userId = sessionStorage.getItem("userId");
  if (!token || !userId) {
    alert("Please login first.");
    window.location.href = "login.html";
    return null;
  }
  return { token, userId: parseInt(userId, 10) };
}

/**
 * Load the cart from backend and render it.
 * Call this on cart.html: <body onload="loadCart()"> or DOMContentLoaded.
 */
async function loadCart() {
  const auth = requireAuth();
  if (!auth) return;
  const { token, userId } = auth;

  try {
    const res = await fetch(`${CART_BASE}/items`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(`GET /items failed: ${res.status} - ${text}`);
    }

    const data = await res.json();
    // If your service returns plain List<CartItem>, data is the array.
    // If it returns { data: [...] }, use data.data. We'll detect both:
    const items = Array.isArray(data) ? data : (data.data || []);

    renderCart(items, userId);

  } catch (err) {
    console.error("loadCart error:", err);
    alert("Failed to load your cart.");
  }
}

/**
 * Renders the cart items into the DOM and shows the total price.
 */
function renderCart(items, userId) {
  const container = document.getElementById("cartItems");
  const totalEl = document.getElementById("grandTotal");

  if (!container) {
    console.warn("#cartItems not found in DOM");
    return;
  }

  container.innerHTML = "";

  if (!items || items.length === 0) {
    container.innerHTML = "<p>Your cart is empty.</p>";
    if (totalEl) totalEl.textContent = "₹0.00";
    return;
  }

  let grandTotal = 0;
  let cartIdFromFirst = null;

  items.forEach(item => {
    // Normalize fields (DTO vs Entity)
    const productId = item.productId ?? item.product?.id;
    const name = item.productName ?? item.product?.name ?? "Product";
    const price = Number(item.price ?? item.product?.price ?? 0);
    const qty = Number(item.quantity ?? 0);
    const lineTotal = Number(item.totalPrice ?? (price * qty));
    const img = item.imageUrl || `images/products/img${productId || 1}.jpg`;

    // (Optional) capture cartId to use on "Clear cart"
    if (!cartIdFromFirst) {
      cartIdFromFirst = item.cartId ?? item.cart?.id ?? null;
    }

    grandTotal += lineTotal;

    const card = document.createElement("div");
    card.className = "cart-card";
    card.innerHTML = `
      <img src="${img}" alt="${name}" class="cart-img" />
      <div class="cart-details">
        <h3>${name}</h3>
        <p>Price: ₹${price.toFixed(2)}</p>
        <p>Qty: ${qty}</p>
        <p><strong>Total: ₹${lineTotal.toFixed(2)}</strong></p>
        <button class="delete-btn" onclick="removeFromCart(${userId}, ${productId})">Remove</button>
      </div>
    `;

    container.appendChild(card);
  });

  if (totalEl) {
    totalEl.textContent = `₹${grandTotal.toFixed(2)}`;
  }

  // Store cartId (if found) so Clear Cart can use it later without asking user
  if (cartIdFromFirst) {
    sessionStorage.setItem("cartId", cartIdFromFirst);
  }
}

/**
 * Remove a single product from the user's cart.
 * CartController expects: DELETE /remove with body: { userId, productId }
 */
async function removeFromCart(userId, productId) {
  const auth = requireAuth();
  if (!auth) return;
  const { token } = auth;

  try {
    const res = await fetch(`${CART_BASE}/remove`, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ userId, productId })
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(`DELETE /remove failed: ${res.status} - ${text}`);
    }

    alert("Item removed from cart");
    loadCart();
  } catch (err) {
    console.error("removeFromCart error:", err);
    alert("Failed to remove item.");
  }
}

/**
 * Clear the whole cart.
 * Your controller: DELETE /clear/{userId}/{cartId}
 * We'll read cartId from sessionStorage (set in renderCart) or you can pass it manually.
 */
async function clearCart() {
  const auth = requireAuth();
  if (!auth) return;
  const { token, userId } = auth;

  const cartId = sessionStorage.getItem("cartId");
  if (!cartId) {
    alert("CartId not found. Load cart first or pass cartId manually.");
    return;
  }

  try {
    const res = await fetch(`${CART_BASE}/clear/${userId}/${cartId}`, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(`DELETE /clear failed: ${res.status} - ${text}`);
    }

    alert("Cart cleared successfully");
    loadCart();
  } catch (err) {
    console.error("clearCart error:", err);
    alert("Failed to clear cart.");
  }
}

// Expose for inline onclick handlers in HTML
window.loadCart = loadCart;
window.removeFromCart = removeFromCart;
window.clearCart = clearCart;
