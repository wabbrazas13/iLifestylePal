// Check if the user is authenticated
    
function checkAuth() {
    // You can use any method to check authentication status, such as checking for a valid session or token
    // For simplicity, we'll use a basic check using localStorage

    // Assuming you set a flag 'isLoggedIn' in localStorage when the user logs in
    const isLoggedIn = localStorage.getItem('isLoggedIn');

    if (!isLoggedIn) {
        // User is not authenticated, redirect to the login page
        window.location.href = 'index.php';
    }
}

// Call the checkAuth function when the page loads
document.addEventListener('DOMContentLoaded', checkAuth);