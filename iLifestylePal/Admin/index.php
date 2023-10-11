<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Login</title>

    <link rel="icon" type="image/x-icon" href="images/logo.png">
    <style>
        body {
            background-image: url("images/bg_1.jpg");
            background-size: cover;
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        
        .container {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        
        .login-form {
            background-color: #fff;
            border-radius: 4px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            width: 300px;
        }
        
        .login-form input[type="text"],
        .login-form input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        
        .login-form button {
            background-color: #4CAF50;
            color: #fff;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
        }
        
        .login-form button:hover {
            background-color: #45a049;
        }

        .login-form button:hover {
            background-color: #45a049;
        }

        .logo {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }

        .logo img {
            width: 50px;
            height: 50px;
            margin-right: 10px;
        }

        .logo span {
            font-size: 24px;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="background"></div>
    <div class="container">
        <div class="login-form">
            <div class="logo">
                <img src="images/logo.png" alt="Logo">
                <span>iLifestylePal</span>
            </div>
            <input type="text" id="username" placeholder="Username" required>
            <input type="password" id="password" placeholder="Password" required>
            <button type="button" id="login">Login</button>
        </div>
    </div>

    <script type="module">
        // Import the functions you need from the SDKs you need
        import { initializeApp } from "https://www.gstatic.com/firebasejs/9.16.0/firebase-app.js";
        import { getAnalytics } from "https://www.gstatic.com/firebasejs/9.16.0/firebase-analytics.js";
        // TODO: Add SDKs for Firebase products that you want to use
        // https://firebase.google.com/docs/web/setup#available-libraries

        // Your web app's Firebase configuration
        // For Firebase JS SDK v7.20.0 and later, measurementId is optional
        const firebaseConfig = {
            apiKey: "AIzaSyAW4sc008PndTi9tyGh_TS2Z5g0ShXwTsM",
            authDomain: "ilifestylepal.firebaseapp.com",
            databaseURL: "https://ilifestylepal-default-rtdb.asia-southeast1.firebasedatabase.app",
            projectId: "ilifestylepal",
            storageBucket: "ilifestylepal.appspot.com",
            messagingSenderId: "268679202640",
            appId: "1:268679202640:web:96a149642b545a80ad0e55",
            measurementId: "G-P7G71V8719"
        };

        // Initialize Firebase
        const app = initializeApp(firebaseConfig);
        const analytics = getAnalytics(app);

        import { getDatabase, ref, onValue, remove, set, get, update } 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        document.getElementById('login').addEventListener('click', () => {
            let username = document.getElementById('username').value.trim().toLowerCase();
            let password = document.getElementById('password').value.trim();

            if (username === '') {
                alert("Please provide the username!");
            } else if (password === '') {
                alert("Please provide the password!");
            } else {
                const adminRef = ref(db, 'Admin');

                get(adminRef).then((snapshot) => {
                    let isAuthenticated = false; // Flag to track authentication

                    snapshot.forEach((childSnapshot) => {
                        const admin_id = childSnapshot.child("admin_id").val().trim().toLowerCase(); // Convert admin_id to lowercase
                        const admin_password = childSnapshot.child("admin_password").val().trim();

                        if (username === admin_id && password === admin_password) {
                            isAuthenticated = true;
                        }
                    });

                    if (isAuthenticated) {
                        alert("Login Successful!");
                        localStorage.setItem('isLoggedIn', true);
                        window.location.href = 'users_information.php';
                    } else {
                        alert("Invalid account or password does not match!");
                    }
                });
            }
        });
    </script>
</body>
</html>