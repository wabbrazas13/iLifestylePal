<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Activities</title>

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
		.modal {
			display: none;
			position: fixed;
			z-index: 1;
			left: 0;
			top: 0;
			width: 100%;
			height: 100%;
			overflow: auto;
			background-color: rgba(0,0,0,0.4);
		}
		.modal-content {
			background-color: white;
			margin: 15% auto;
			padding: 20px;
			border: 1px solid #888;
			width: 80%;
			max-width: 600px;
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
                    <button onclick="openModal()" class="btn btn-success"><i class="fa-solid fa-plus"></i> Add Activity</button>
                    <br><br>
                    <h3>Activities List</h3>
                    <div style="overflow-x: auto;">
                    <table id="tbl_activities" class="display" style="width:100%; padding-top: 5px;">
                        <thead style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Activity Name</th>
                                <th>MET (Metabolic Equivalent of Task)</th>
                                <th>Edit</th>
                                <th>Delete</th>
                            </tr>
                        </thead>
                        <tbody id="tbody_activities"></tbody>
                        <tfoot style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Activity Name</th>
                                <th>MET (Metabolic Equivalent of Task)</th>
                                <th>Edit</th>
                                <th>Delete</th>
                            </tr>
                        </tfoot>
                    </table>
                    </div>
                </div>
            </div> 
        </div>
    </div>

    <div id="myModal" class="modal">
		<div class="modal-content">
			<h2 id="mh">Add Activity</h2>
			<label>Activity Name:</label>
            <input id="act_name" type="text" class="form-control">
            <label style="margin-top: 10px;">MET (Metabolic Equivalent of Task):</label>
            <input id="act_met" type="number" class="form-control">
            <div class="row">
                <div class="col-md-6">
                    <button style="margin-top: 30px; width: 100%;" class="btn btn-danger" onclick="closeModal()">Close</button>
                </div>
                <div class="col-md-6">
                    <button id="saveActivity" style="margin-top: 30px; width: 100%;" class="btn btn-success">Save</button>       
                </div>
            </div>
		</div>
	</div>

    <script>
		function openModal() {
			document.getElementById("myModal").style.display = "block";
            document.getElementById('mh').innerHTML = "Add Activity";
            document.getElementById('act_name').value = "";
            document.getElementById('act_met').value = "";
            document.getElementById('saveActivity').innerHTML = "Save";
		}
		
		function closeModal() {
			document.getElementById("myModal").style.display = "none";
		}
	</script>

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

        import { getDatabase, ref, onValue, remove, set, get } 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        var act_no = 0;
        var tbody_activities = document.getElementById("tbody_activities");

        function GetAllActivitiesData() {
            const ActivitiesRef = ref(db, "Maintenance/Daily Activity/Activities");
            get(ActivitiesRef).then((snapshot) => { 
                var activities = [];
                snapshot.forEach(childSnapshot => {
                    activities.push(childSnapshot.val());
                });
                DisplayToActivitiesTable(activities);
                $('#tbl_activities').DataTable();
            });
        }

        function DisplayToActivitiesTable(act) {
            act_no = 0;
            tbody_activities.innerHTML = "";
            act.forEach(element => {
                DisplayItemToActivitiesTable(element.activityName, element.met);
            });
        }

        function DisplayItemToActivitiesTable(act_name, met) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");

            td1.innerHTML = ++act_no;
            td2.innerHTML = act_name;            
            td3.innerHTML = met;

            td4.innerHTML = '<button id="editActivity" type="button" class="btn btn-info"><i class="fa-solid fa-pen-to-square"></i></button>';
            td5.innerHTML = '<button id="deleteActivity" type="button" class="btn btn-danger"><i class="fa-sharp fa-solid fa-trash-can"></i></button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
            tbody_activities.appendChild(tr); 
        }

        window.onload = GetAllActivitiesData;

        //When Save Activity is clicked
        const saveButton = document.getElementById('saveActivity');
        saveButton.addEventListener('click', () => {
            let actName = document.getElementById('act_name').value.trim();
            let actMET = document.getElementById('act_met').value.trim();

            if (actName !== '' && actMET !== '') {
                const ActivitiesRef = ref(db, "Maintenance/Daily Activity/Activities/"+actName);
                const dataToInsert = {
                    activityName: actName,
                    met: actMET
                };
                set(ActivitiesRef, dataToInsert)
                .then(() => {
                    alert("Activity Saved.");
                    PageRefresh();
                })
                .catch((error) => {
                    alert("Error adding data: ", error);
                });
                document.getElementById('act_name').value = "";
                document.getElementById('act_met').value = "";
                document.getElementById("myModal").style.display = "none";
            } else {
                alert("Please fill up the necessary input.");
            } 
        });

        $('#tbody_activities').on('click', 'button#editActivity', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#tbl_activities').DataTable().row(parentRow).index();
            const cellData1 = $('#tbl_activities').DataTable().cell(rowIndex, 1).data();
            const cellData2 = $('#tbl_activities').DataTable().cell(rowIndex, 2).data();
            
            document.getElementById("myModal").style.display = "block";
            document.getElementById('act_name').value = cellData1;
            document.getElementById('act_met').value = cellData2;
            document.getElementById('mh').innerHTML = "Edit Activity";
            document.getElementById('saveActivity').innerHTML = "Update";
        });

        $('#tbody_activities').on('click', 'button#deleteActivity', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#tbl_activities').DataTable().row(parentRow).index();
            const cellData = $('#tbl_activities').DataTable().cell(rowIndex, 1).data();
            
            var confirmResult = confirm("Do you want to proceed in deleting this activity?");
            if (confirmResult) {
                const ActivitiesRef = ref(db, "Maintenance/Daily Activity/Activities/"+cellData);
                remove(ActivitiesRef)
                .then(() => {
                    console.log("Data deleted successfully.");
                    PageRefresh();
                })
                .catch((error) => {
                    console.error("Error deleting node:", error);
                });
            } else {
                // user clicked "Cancel" (false)
                // Do something else here, or nothing
            }
        });

        function PageRefresh() {
            window.location.href = 'activities_list.php';
        }
    </script>
</body>
</html>