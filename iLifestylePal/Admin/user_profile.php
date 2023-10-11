<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - User Profile</title>

    <!-- DataTable -->
    <script src="https://code.jquery.com/jquery-3.5.1.js"></script>
    <script src="https://cdn.datatables.net/1.13.1/js/jquery.dataTables.min.js"></script>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.1/css/jquery.dataTables.min.css">
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" integrity="sha384-w76AqPfDkMBDXo30jS1Sgez6pr3x5MlQ1ZAGC+nuZB+EYdgRZgiwxhTBTkF7CXvN" crossorigin="anonymous"></script>
    <!-- Fontawesome CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <!-- Custom CSS -->
    <link rel="stylesheet" href="styles/sidenav.css">
    <link rel="stylesheet" href="styles/header.css">
    <link rel="icon" type="image/x-icon" href="images/logo.png">
    <!-- Check Login Credentials -->
    <script src="auth.js"></script>

    <style>
        #btn_user_profile,
        #btn_user_post,
        #btn_daily_activity,
        #btn_food_journal,
        #btn_sleep_schedule,
        #btn_step_tracker,
        #btn_friend_list,
        #btn_friend_request
        {
            min-width: 150px;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="wrapper">
        <!-- Side Navigation -->
        <?php include 'sidenav.php'; ?>

        <div id="content">
            <div class="card">
                <div class="card-body">
                    <div class="header">
                        <img src="images/logo-copy.png" alt="...">
                        <h1>iLifestylePal</h1>
                    </div>
                </div>
            </div>
            <div style="margin-top: 10px;" class="card">
                <div class="card-body">
                    <button id="btn_user_profile" class="btn btn-secondary">User Profile</button>
                    <button id="btn_user_post" class="btn btn-success">User Post</button>
                    <button id="btn_daily_activity" class="btn btn-success">Daily Activity</button>
                    <button id="btn_food_journal" class="btn btn-success">Food Journal</button>
                    <button id="btn_sleep_schedule" class="btn btn-success">Sleep Schedule</button>
                    <button id="btn_step_tracker" class="btn btn-success">Step Tracker</button>
                    <button id="btn_friend_list" class="btn btn-success">Friend List</button>
                    <button id="btn_friend_request" class="btn btn-success">Friend Request</button>
                    <br><br>
                    <h3>User Profile</h3>
                    <table id="tbl_user_profile" class="table table-bordered">
                        <tbody>
                            <tr>
                                <td>Picture</td>
                                <td>
                                    <img id="user_pic" src="" alt="" style="width: auto; height: 180px;">
                                </td>
                            </tr> 
                            <tr>
                                <td>User ID</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td>Email</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>First Name</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Last Name</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Gender</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Birthdate</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Age</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Height</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Weight</td>
                                <td></td>
                            </tr>  
                            <tr>
                                <td>BMI</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Health Conditions</td>
                                <td></td>
                            </tr> 
                            <tr>
                                <td>Allergies</td>
                                <td></td>
                            </tr> 
                        <tbody>
                    </table>
                </div>
            </div> 
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

        import { getDatabase, ref, onValue, remove, set } 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        const urlParams = new URLSearchParams(window.location.search);
        const uid = urlParams.get('uid');

        function populateTable() {
            const userRef = ref(db, "Users/" + uid);
            onValue(userRef, (snapshot) => {
                const pic_url = snapshot.child("pic_url").val();
                const email = snapshot.child("email").val();
                const firstname = snapshot.child("firstname").val();
                const lastname = snapshot.child("lastname").val();
                const gender = snapshot.child("gender").val();
                const birthdate = snapshot.child("birthdate").val();
                const age = snapshot.child("age").val();
                const height = snapshot.child("height").val();
                const weight = snapshot.child("weight").val();
                const bmi = snapshot.child("bmi").val();

                var table = document.getElementById("tbl_user_profile");
                table.rows[1].cells[1].innerHTML = uid;
                table.rows[2].cells[1].innerHTML = email;
                table.rows[3].cells[1].innerHTML = firstname;
                table.rows[4].cells[1].innerHTML = lastname;
                table.rows[5].cells[1].innerHTML = gender;
                table.rows[6].cells[1].innerHTML = birthdate;
                table.rows[7].cells[1].innerHTML = age + " years old";
                table.rows[8].cells[1].innerHTML = height + " cm/s";
                table.rows[9].cells[1].innerHTML = weight + " kg/s";
                table.rows[10].cells[1].innerHTML = bmi;

                const user_pic = document.getElementById("user_pic");
                user_pic.src = pic_url;

                if (pic_url === null) {
                    table.rows[0].cells[1].innerHTML = "Not Available";
                }

                // Add click event listener to open modal
                user_pic.addEventListener("click", function() {
                    window.open(pic_url, "_blank");
                });
            });

            const healthConditionsRef = ref(db, "Users/" + uid + "/Health Conditions");
            onValue(healthConditionsRef, (snapshot) => {
                if (snapshot.exists()) {
                    let healthConditions = [];
                    snapshot.forEach((childSnapshot) => {
                        healthConditions.push(childSnapshot.key);
                    });
                    if (healthConditions.length > 0) {
                        var table = document.getElementById("tbl_user_profile");
                        table.rows[11].cells[1].innerHTML = healthConditions.join(", ");
                    } else {
                        var table = document.getElementById("tbl_user_profile");
                        table.rows[11].cells[1].innerHTML = "None";
                    }
                } else {
                    var table = document.getElementById("tbl_user_profile");
                    table.rows[11].cells[1].innerHTML = "None";
                }
            });

            const allergiesRef = ref(db, "Users/" + uid + "/Allergies");
            onValue(allergiesRef, (snapshot) => {
                if (snapshot.exists()) {
                    let allergies = [];
                    snapshot.forEach((childSnapshot) => {
                        allergies.push(childSnapshot.key);
                    });
                    if (allergies.length > 0) {
                        var table = document.getElementById("tbl_user_profile");
                        table.rows[12].cells[1].innerHTML = allergies.join(", ");
                    } else {
                        var table = document.getElementById("tbl_user_profile");
                        table.rows[12].cells[1].innerHTML = "None";
                    }
                } else {
                    var table = document.getElementById("tbl_user_profile");
                    table.rows[12].cells[1].innerHTML = "None";
                }
            });
        }

        window.onload = function() {
            populateTable();
        };

        const btn_user_profile = document.getElementById("btn_user_profile");
        btn_user_profile.addEventListener("click", () => {
            window.location.href = 'user_profile.php?uid=' + uid;
        });

        const btn_user_post = document.getElementById("btn_user_post");
        btn_user_post.addEventListener("click", () => {
            window.location.href = 'user_post.php?uid=' + uid;
        });

        const btn_daily_activity = document.getElementById("btn_daily_activity");
        btn_daily_activity.addEventListener("click", () => {
            window.location.href = 'user_daily_activity.php?uid=' + uid;
        });

        const btn_food_journal = document.getElementById("btn_food_journal");
        btn_food_journal.addEventListener("click", () => {
            window.location.href = 'user_food_journal.php?uid=' + uid;
        });

        const btn_sleep_schedule = document.getElementById("btn_sleep_schedule");
        btn_sleep_schedule.addEventListener("click", () => {
            window.location.href = 'user_sleep_schedule.php?uid=' + uid;
        });

        const btn_step_tracker = document.getElementById("btn_step_tracker");
        btn_step_tracker.addEventListener("click", () => {
            window.location.href = 'user_step_tracker.php?uid=' + uid;
        });

        const btn_friend_list = document.getElementById("btn_friend_list");
        btn_friend_list.addEventListener("click", () => {
            window.location.href = 'user_friend_list.php?uid=' + uid;
        });

        const btn_friend_request = document.getElementById("btn_friend_request");
        btn_friend_request.addEventListener("click", () => {
            window.location.href = 'user_friend_request.php?uid=' + uid;
        });

    </script>
</body>
</html>