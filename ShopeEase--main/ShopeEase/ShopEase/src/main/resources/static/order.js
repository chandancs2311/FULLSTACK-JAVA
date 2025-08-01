async function loadUserInvoice() {
  const token = sessionStorage.getItem("token");
  const userId = sessionStorage.getItem("userId");

  if (!token || !userId) {
    document.getElementById("invoiceContent").innerHTML = "Missing token or user ID.";
    return;
  }

  try {
    const response = await fetch(`http://localhost:8080/api/user/orders/invoice/user/${userId}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      const error = await response.text();
      document.getElementById("invoiceContent").innerHTML = "Error: " + error;
      return;
    }

    const invoice = await response.json();

    // Get only the latest order (assumes the last item is the latest)
    const latestOrder = invoice.orders[invoice.orders.length - 1];

    let html = `
      <div class="invoice-details">
        <p><strong>User ID:</strong> ${invoice.userId}</p>
        <p><strong>Customer:</strong> ${invoice.userName}</p>
        <p><strong>Grand Total:</strong> ₹${invoice.grandTotal}</p>
      </div>
      <div class="order-box">
        <h3>Order #${latestOrder.orderId}</h3>
        <p><strong>Date:</strong> ${latestOrder.orderDate}</p>
        <p><strong>Shipping Address:</strong> ${latestOrder.shippingAddress}</p>
        <p><strong>Status:</strong> ${latestOrder.status}</p>
        <table>
          <thead>
            <tr>
              <th>Product</th>
              <th>Qty</th>
              <th>Price (₹)</th>
              <th>Total (₹)</th>
            </tr>
          </thead>
          <tbody>
    `;

    latestOrder.items.forEach(item => {
      html += `
        <tr>
          <td>${item.productName}</td>
          <td>${item.quantity}</td>
          <td>${item.price}</td>
          <td>${item.totalPrice}</td>
        </tr>
      `;
    });

    html += `
          </tbody>
        </table>
        <p style="text-align:right;"><strong>Order Total:</strong> ₹${latestOrder.totalAmount}</p>
      </div>
    `;

    document.getElementById("invoiceContent").innerHTML = html;

  } catch (err) {
    console.error(err);
    document.getElementById("invoiceContent").innerHTML = "Something went wrong.";
  }
}
function downloadPDF() {
  const invoiceElement = document.getElementById("invoiceContent");
  const opt = {
    margin: 0.5,
    filename: 'ShopEase_Invoice.pdf',
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 2 },
    jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
  };

  html2pdf().set(opt).from(invoiceElement).save();
}
