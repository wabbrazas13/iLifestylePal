<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Daily Activity</title>

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
                    <button class="btn btn-success"><a href="activities.php">List of Activities</a></button>
                    <br><br>
                    <h3>Daily Activity</h3>
                    <div style="overflow-x: auto;">
                        <table id="tbl_daily_activities" class="display" style="width:100%; padding-top: 5px;">
                            <thead style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>User ID</th>
                                    <th>Activity</th>
                                    <th>Date</th>
                                    <th>Duration</th>
                                    <th>Calorie Burned</th>
                                </tr>
                            </thead>
                            <tbody id="tbody_daily_activities"></tbody>
                            <tfoot style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>User ID</th>
                                    <th>Activity</th>
                                    <th>Date</th>
                                    <th>Duration</th>
                                    <th>Calorie Burned</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
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

        import { getDatabase, ref, get } 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        var act_no = 0;
        var tbody_daily_activities = document.getElementById("tbody_daily_activities");
    
        function GetDailyActivities() {
            const dailyActRef = ref(db, "Daily Activity");
            get(dailyActRef).then((snapshot) => { 
                const activities = [];
                snapshot.forEach((childSnapshot) => {
                    const act_key = childSnapshot.key; // Get the key of the post
                    const data = childSnapshot.val(); // Get the data of the post
                    const activity = { act_key, ...data }; // Create an object that contains both the key and data
                    activities.push(activity); // Push the object to the posts array
                });
                DisplayToTable(activities);
                $('#tbl_daily_activities').DataTable();  
            });
        }

        function DisplayToTable(activities) {
            act_no = 0;
            tbody_daily_activities.innerHTML = "";
            activities.forEach(element => {
                DisplayItemToTable(element.currentUserID, element.activityName, element.burnedCalorie, element.date, element.duration);
            });
        }

        function DisplayItemToTable(currentUserID, activityName, burnedCalorie, date, duration) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");
            let td6 = document.createElement("td");

            td1.innerHTML = ++act_no;
            td2.innerHTML = currentUserID;
            td3.innerHTML = activityName;
            td4.innerHTML = date;            
            td5.innerHTML = duration + " minutes";
            td6.innerHTML = burnedCalorie + " kcal";

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
            tr.appendChild(td6);
            tbody_daily_activities.appendChild(tr); 
        }

        window.onload = function() {
            GetDailyActivities();
        };
    </script>
</body>
</html>