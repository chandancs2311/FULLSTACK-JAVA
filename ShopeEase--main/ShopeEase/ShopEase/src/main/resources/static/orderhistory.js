const token = sessionStorage.getItem("token"); // or localStorage if that's where you store

async function loadOrderHistory() {
  if (!token) {
    alert("Login required.");
    window.location.href = "login.html";
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/api/user/orders/my-orders", {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) throw new Error("Fetch failed");

    const data = await response.json();
    if (!Array.isArray(data)) throw new Error("Invalid data");

    const orderHistoryDiv = document.getElementById("orderHistory");
    orderHistoryDiv.innerHTML = "";

    data.forEach(order => {
      const orderCard = document.createElement("div");
      orderCard.className = "order-card";

      const itemsHtml = order.items.map(item => `
        <div class="order-item">
          <img src="images/products/img${item.productId}.jpg" class="product-img">
          <div>
            <p><strong>${item.productName}</strong></p>
            <p>Qty: ${item.quantity}</p>
            <p>Price: ₹${item.price}</p>
            <p>Total: ₹${item.totalPrice}</p>
          </div>
        </div>
      `).join("");

      orderCard.innerHTML = `
        <h3>Order #${order.orderId}</h3>
        <p><strong>Date:</strong> ${new Date(order.orderDate).toLocaleString()}</p>
        <p><strong>Address:</strong> ${order.shippingAddress}</p>
        <p><strong>Status:</strong> ${order.status}</p>
        <p><strong>Total:</strong> ₹${order.totalAmount}</p>
        <div class="order-items">${itemsHtml}</div>
      `;

      orderHistoryDiv.appendChild(orderCard);
    });

  } catch (err) {
    console.error("Error:", err);
    document.getElementById("orderHistory").innerHTML = "<p>Failed to load orders.</p>";
  }
}

window.onload = loadOrderHistory;
