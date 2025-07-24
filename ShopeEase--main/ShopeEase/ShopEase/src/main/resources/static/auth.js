const API_BASE = 'http://localhost:8080/api/user'; // Change if different

//  LOGIN FUNCTION
async function loginUser(event) {
  event.preventDefault();

  const username = document.querySelector('.username').value;
  const password = document.querySelector('.password').value;

  const payload = {
    email: username,   // assuming email as username
    password: password
  };

  try {
    const res = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const errMsg = await res.text();
      console.error("Login failed:", errMsg);
      alert("Login failed: " + errMsg);
      return;
    }

    const data = await res.json();
    const token = data.token;
    const role = data.role;
    const userId = data.userId;

    sessionStorage.setItem('token', token);
    sessionStorage.setItem('role', role);
    sessionStorage.setItem('userId', userId);

    if (data.role === 'ROLE_ADMIN') {
      window.location.href = 'admin.html';
    } else {
      window.location.href = 'home.html';
    }

  } catch (err) {
    console.error(err);
    alert("Server error.");
  }
}

//  REGISTER FUNCTION
async function registerUser(event) {
  event.preventDefault();

  const password = document.querySelector('.password').value;
  const name     = document.querySelector('.name').value;
  const email    = document.querySelector('.email').value;
  const role     = document.querySelector('.role').value;

  const payload = {
    name: name,
    email: email,
    password: password,
    role: role.toLowerCase()
  };

  try {
    const res = await fetch(`${API_BASE}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const msg = await res.text();

    if (res.ok) {
      alert(msg);
      window.location.href = 'login.html';
    } else {
      alert("Registration failed: " + msg);
    }

  } catch (err) {
    console.error(err);
    alert("Server error.");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const productListContainer = document.getElementById("product-list");
  const category = sessionStorage.getItem("selectedCategory");

  const url = category
    ? `http://localhost:8080/api/products/category/${category}`
    : `http://localhost:8080/api/products`;

  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error("Network response was not ok");
      return res.json();
    })
    .then(data => {
      productListContainer.innerHTML = "";
      data.forEach(product => {
        const card = document.createElement("div");
        card.className = "product-card";
        card.innerHTML = `
          <img src="${product.imageUrl || 'images/placeholder.png'}" alt="${product.name}">
          <h3>${product.name}</h3>
          <p>${product.description}</p>
          <div class="price">₹${product.price}</div>
          <button>Add to Cart</button>
        `;
        productListContainer.appendChild(card);
      });
    })
    .catch(err => console.error("Fetch error:", err));
});
