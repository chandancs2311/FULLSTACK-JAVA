const token = sessionStorage.getItem("token");

fetch("http://localhost:8080/api/user/orders/place", {
    method: "POST",
    headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({
        orderDate: new Date().toISOString(),  // or leave null if set inside service
        totalAmount: 0,                        // or your calculated total
        status: "PENDING",                    // or your default status
        shippingAddress: "User address"       // make sure this is present
    })
})
.then(res => res.json())
.then(data => {
    console.log("Order placed successfully:", data);
})
.catch(err => {
    console.error("Error placing order:", err);
});
