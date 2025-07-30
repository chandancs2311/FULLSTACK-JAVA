document.addEventListener('DOMContentLoaded', () => {
    const profileForm = document.getElementById('profileForm');
    const editButton = document.createElement('button');
    editButton.type = 'button';
    editButton.innerText = 'Edit Profile';
    editButton.classList.add('btn', 'edit-btn');
    profileForm.appendChild(editButton);

    const profileImage = document.getElementById('profileImage');
    const uploadImage = document.getElementById('uploadImage');

    let isEditing = false;
    let userRole = null;

    // Fetch profile data
    async function fetchProfile() {
        try {
            const token = localStorage.getItem('jwtToken');
            const res = await fetch('/api/user/profile', {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!res.ok) {
                throw new Error('Failed to fetch profile');
            }

            const user = await res.json();
            document.getElementById('fullName').value = user.fullName;
            document.getElementById('email').value = user.email;
            document.getElementById('role').value = user.role;
            document.getElementById('phone').value = user.phone || '';
            document.getElementById('address').value = user.address || '';
            if (user.photoUrl) profileImage.src = user.photoUrl;
            userRole = user.role;
        } catch (error) {
            console.error('Error fetching profile:', error);
        }
    }

    // Enable/Disable editing
    function toggleEditMode() {
        isEditing = !isEditing;
        profileForm.querySelectorAll('input, textarea').forEach(input => {
            if (input.id !== 'email' && input.id !== 'role' && input.id !== 'fullName') {
                input.disabled = !isEditing;
            }
        });
        editButton.innerText = isEditing ? 'Save Changes' : 'Edit Profile';

        if (!isEditing) {
            saveProfile();
        }
    }

    // Save updated profile
    async function saveProfile() {
        try {
            const token = localStorage.getItem('jwtToken');
            const updatedData = {
                phone: document.getElementById('phone').value,
                address: document.getElementById('address').value
            };

            const res = await fetch('/api/user/profile/update', {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedData)
            });

            if (!res.ok) {
                throw new Error('Failed to update profile');
            }

            alert('Profile updated successfully!');
        } catch (error) {
            console.error('Error updating profile:', error);
        }
    }

    // Profile image preview
    uploadImage.addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                profileImage.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });

    editButton.addEventListener('click', toggleEditMode);

    fetchProfile();
});
