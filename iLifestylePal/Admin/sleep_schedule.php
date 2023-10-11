<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Sleep Schedule</title>

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
                    <h3>Sleep Schedule</h3>
                    <div style="overflow-x: auto;">
                        <table id="table_sleepSchedule" class="display" style="width:100%; padding-top: 5px;">
                            <thead style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>User ID</th>
                                    <th>Date</th>
                                    <th>Bedtime</th>
                                    <th>Wake Up</th>
                                    <th>Duration</th>
                                </tr>
                            </thead>
                            <tbody id="tbody_sleepSchedule"></tbody>
                            <tfoot style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>User ID</th>
                                    <th>Date</th>
                                    <th>Bedtime</th>
                                    <th>Wake Up</th>
                                    <th>Duration</th>
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

        var sleep_no = 0;
        var tbody_sleepSchedule = document.getElementById("tbody_sleepSchedule");
        
        function GetAllUserSleepSchedule() {
            const sleepScheduleRef = ref(db, "Sleep Schedule");
            get(sleepScheduleRef).then((snapshot) => { 
                const sleep = [];
                snapshot.forEach(childSnapshot => {
                    const data = childSnapshot.val(); // Get the data of the sleep schedule
                    sleep.push(data);
                });
                const sortedSleep = sleep.sort((a, b) => b.start_timestamp - a.start_timestamp);
                DisplayToSleepScheduleTable(sortedSleep);
                $('#table_sleepSchedule').DataTable(); 
            });
        }

        function DisplayToSleepScheduleTable(sleep) {
            sleep_no = 0;
            tbody_sleepSchedule.innerHTML = "";
            sleep.forEach(element => {
                DisplayItemTable(element.sleep_id, element.sleep_uid, element.sleep_year, element.sleep_month, element.sleep_day, element.sleep_weekday, element.sleep_duration, element.start_timestamp, element.end_timestamp );
            });
        }

        function DisplayItemTable(id, uid, y, m, d, w, duration, start, end) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");
            let td6 = document.createElement("td");

            td1.innerHTML = ++sleep_no;         
            td2.innerHTML = uid;
            td3.innerHTML = m + " " + d + " ( " + w + " ) " + y;   
            td4.innerHTML = formatTime(start);
            td5.innerHTML = formatTime(end);
            td6.innerHTML = formatDuration(duration);

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
            tr.appendChild(td6);
            tbody_sleepSchedule.appendChild(tr); 
        }

        function formatTime(timestamp) {
            const date = new Date(timestamp);
            let hours = date.getHours();
            let minutes = date.getMinutes();
            let period = "AM";

            if (hours >= 12) {
                period = "PM";
                if (hours > 12) {
                    hours -= 12;
                }
            }

            if (hours === 0) {
                hours = 12;
            }

            if (hours <10) {
                hours = "0" + hours;
            }

            if (minutes < 10) {
                minutes = "0" + minutes;
            }

            return hours + " : " + minutes + " " + period;
        }

        function formatDuration(duration) {
            const hours = Math.floor(duration / 60);
            const minutes = duration % 60;
            let durationString = "";

            if (hours > 0) {
                durationString += hours + " hour";
                if (hours > 1) {
                    durationString += "s";
                }
            }

            if (minutes > 0) {
                if (durationString !== "") {
                    durationString += " ";
                }
                durationString += minutes + " minute";
                if (minutes > 1) {
                    durationString += "s";
                }
            }

            return durationString;
        }

        window.onload = function() {
            GetAllUserSleepSchedule();
        };
    </script>
</body>
</html>