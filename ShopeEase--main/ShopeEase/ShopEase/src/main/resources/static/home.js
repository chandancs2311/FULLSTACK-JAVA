// ===== Load categories dynamically =====

// Category Buttons with Images
const categories = [
    { name: 'Grocery', icon: 'images/grocery.jpg' },
    { name: 'Mobiles', icon: 'images/mobiles.jpg' },
    { name: 'Fashion', icon: 'images/login2.jpg' },
    { name: 'Electronics', icon: 'images/electronics.jpg' },
    { name: 'Home & Furniture', icon: 'images/home&furnit.jpg' },
    { name: 'Appliances', icon: 'images/appliances.jpg' },
    { name: 'Beauty,Toys & More', icon: 'images/toys.jpg' },
    { name: 'Two Wheelers', icon: 'images/bike.jpg' }
];

const categoryRow = document.getElementById('categoryRow');

categories.forEach(cat => {
    const btn = document.createElement('button');
    btn.className = 'category-btn';
    btn.onclick = () => {
        window.location.href = `product.html?category=${cat.name.toLowerCase()}`;
    };

    const img = document.createElement('img');
    img.src = cat.icon;
    img.alt = cat.name;

    const text = document.createElement('span');
    text.innerText = cat.name;

    btn.appendChild(img);
    btn.appendChild(text);
    categoryRow.appendChild(btn);
});

// Image Slider - 2 Second Interval
let currentIndex = 0;
const slider = document.getElementById('imageSlider');
const images = slider.getElementsByTagName('img');

setInterval(() => {
    for (let i = 0; i < images.length; i++) {
        images[i].style.display = 'none';
    }
    images[currentIndex].style.display = 'block';
    currentIndex = (currentIndex + 1) % images.length;
}, 2000);

// ===== Logout Feature =====
document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault(); // Prevent default link behavior
            // Clear token and session info
            sessionStorage.removeItem("token");
            sessionStorage.removeItem("userRole");
            sessionStorage.removeItem("selectedCategory");

            // Redirect to login
            window.location.href = "login.html";
        });
    }
});
