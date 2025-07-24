const API_BASE = 'http://localhost:8080/api/user';

// GET JWT token from sessionStorage
function getToken() {
  return sessionStorage.getItem('token');
}

// LOGIN FUNCTION
async function loginUser(event) {
  event.preventDefault();

  const email = document.querySelector('.username').value;
  const password = document.querySelector('.password').value;

  const payload = { email, password };

  try {
    const res = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      alert("Login failed!");
      return;
    }

    const data = await res.json();
    const token = data.token;
    const role = data.role;

    sessionStorage.setItem('token', token);
    sessionStorage.setItem('role', role);

    // Redirect based on role
    if (role === 'ROLE_ADMIN') {
      window.location.href = 'admin.html';
    } else if (role === 'ROLE_USER') {
      window.location.href = 'home.html';
    } else {
      alert("Invalid role.");
    }

  } catch (err) {
    console.error(err);
    alert("Server error during login.");
  }
}

// REGISTER FUNCTION
async function registerUser(event) {
  event.preventDefault();

  const name = document.querySelector('.name').value;
  const email = document.querySelector('.email').value;
  const password = document.querySelector('.password').value;
  const role = document.querySelector('.role').value.toLowerCase();

  const payload = { name, email, password, role };

  try {
    const res = await fetch(`${API_BASE}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      alert("Registered successfully! Please login.");
      window.location.href = 'login.html';
    } else {
      const msg = await res.text();
      alert("Registration failed: " + msg);
    }

  } catch (err) {
    console.error(err);
    alert("Server error during registration.");
  }
}

// ---------------------- 🛒 CART CRUD FUNCTIONS ----------------------

// 1. Create Cart for userId
async function createCart(userId) {
  const token = getToken();
  const res = await fetch(`${API_BASE}/cart/create/${userId}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (res.ok) {
    alert('Cart created successfully');
  } else {
    alert('Failed to create cart');
  }
}

// 2. Add product to cart
async function addToCart(cartId, productId, quantity) {
  const token = getToken();

  const payload = {
    cartId,
    productId,
    quantity
  };

  const res = await fetch(`${API_BASE}/orders/add-item`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    alert('Item added to cart');
  } else {
    alert('Failed to add item');
  }
}

// 3. Remove product from cart
async function removeFromCart(cartId, productId) {
  const token = getToken();

  const url = `${API_BASE}/orders/remove-item/${cartId}/${productId}`;
  const res = await fetch(url, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (res.ok) {
    alert('Item removed from cart');
  } else {
    alert('Failed to remove item');
  }
}

// 4. Get cart by userId
async function getCart(userId) {
  const token = getToken();
  const res = await fetch(`${API_BASE}/cart/user/${userId}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (res.ok) {
    return await res.json(); // returns cart object with items
  } else {
    alert('Failed to fetch cart');
    return null;
  }
}
