async function loadAllCarts() {
  const token = sessionStorage.getItem("token");

  if (!token) {
    alert("Please login as admin.");
    window.location.href = "login.html";
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/api/admin/cart/all", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error("Failed to fetch all carts.");
    }

    const carts = await response.json();
    const container = document.getElementById("allCarts");
    container.innerHTML = "";

    if (carts.length === 0) {
      container.innerHTML = "<p>No carts found.</p>";
      return;
    }

    carts.forEach(cart => {
      const cartDiv = document.createElement("div");
      cartDiv.className = "cart-box";

      const itemsHTML = cart.items.map(item => `
        <li>${item.productName} - ${item.quantity} x ₹${item.price}</li>
      `).join("");

      cartDiv.innerHTML = `
        <h3>User ID: ${cart.userId} | Name: ${cart.userName}</h3>
        <ul>${itemsHTML}</ul>
        <hr/>
      `;

      container.appendChild(cartDiv);
    });

  } catch (error) {
    console.error("Error loading all carts:", error);
    document.getElementById("allCarts").innerHTML =
      "<p class='error'>Unable to load cart data. Try again later.</p>";
  }
}
