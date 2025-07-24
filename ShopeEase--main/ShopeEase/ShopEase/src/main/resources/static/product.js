const API_BASE = "http://localhost:8080/api/user";
const CART_API = `${API_BASE}/cart`;

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

// ---------------------- ADD TO CART ----------------------
document.addEventListener("DOMContentLoaded", () => {
  const auth = requireAuth();
  if (!auth) return;

  const { token, userId } = auth;

  // Bind all "Add to Cart" buttons
  document.querySelectorAll("[id^='addToCartBtn-']").forEach((button) => {
    button.addEventListener("click", async () => {
      const btnId = button.id;                  // e.g. addToCartBtn-6
      const productId = parseInt(btnId.split("-")[1], 10); // 6
      const qtyInput = document.getElementById(`quantity-${productId}`);
      const quantity = qtyInput ? parseInt(qtyInput.value, 10) : 1;

      const payload = {
        userId,        // backend still checks this against the token
        productId,
        quantity
      };

      try {
        const res = await fetch(`${CART_API}/add`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          const text = await res.text();
          throw new Error(`Add to cart failed: ${res.status} - ${text}`);
        }

        const data = await res.json();
        alert(data.message || "Item added to cart!");
        console.log("Add to cart response:", data);
      } catch (err) {
        console.error(err);
        alert("Could not add to cart. Check console.");
      }
    });
  });

  // Optional: logout handler
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      sessionStorage.clear();
      window.location.href = "login.html";
    });
  }
});

// ---------------------- VIEW CART (use on cart.html) ----------------------
async function loadCart() {
  const auth = requireAuth();
  if (!auth) return;
  const { token } = auth;

  try {
    const res = await fetch(`${CART_API}/items`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!res.ok) throw new Error(`Load cart failed: ${res.status}`);

    const items = await res.json();
    console.log("Cart items:", items);

    // TODO: render items into your cart.html
    // renderCart(items);

  } catch (err) {
    console.error(err);
    alert("Failed to load your cart.");
  }
}

// Export if needed
window.loadCart = loadCart;
