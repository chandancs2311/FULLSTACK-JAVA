async function loadAllInvoices() {
  const token = sessionStorage.getItem("token");

  if (!token) {
    document.getElementById("adminInvoices").innerHTML = "Admin token missing. Please login.";
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/api/user/orders/all", {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      const errText = await response.text();
      document.getElementById("adminInvoices").innerHTML = "Error: " + errText;
      return;
    }

    const allInvoices = await response.json();
    let html = "";

    allInvoices.forEach((invoice, index) => {
      html += `
        <div class="invoice-box">
          <div class="invoice-header">
            <p><strong>Invoice #${index + 1}</strong></p>
            <p><strong>User Name:</strong> ${invoice.userName}</p>
            <p><strong>Email:</strong> ${invoice.email}</p>
            <p><strong>Shipping Address:</strong> ${invoice.shippingAddress}</p>
          </div>
          <table>
            <thead>
              <tr>
                <th>Product</th>
                <th>Qty</th>
                <th>Price (₹)</th>

              </tr>
            </thead>
            <tbody>
      `;

      invoice.items.forEach(item => {
        html += `
          <tr>
            <td>${item.productName}</td>
            <td>${item.quantity}</td>
            <td>${item.price}</td>

          </tr>
        `;
      });

      html += `
            </tbody>
          </table>
          <p class="total">Grand Total: ₹${invoice.totalAmount}</p>
        </div>
      `;
    });

    document.getElementById("adminInvoices").innerHTML = html;

  } catch (err) {
    console.error(err);
    document.getElementById("adminInvoices").innerHTML = "Something went wrong loading invoices.";
  }
}
