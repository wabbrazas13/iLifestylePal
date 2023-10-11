<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Health Condition</title>

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
                    <button onclick="openModal()" class="btn btn-success"><i class="fa-solid fa-plus"></i> Health Condition</button>
                    <a href="food_recommendation.php"><button class="btn btn-success">Recommend Food</button></a>
                    <a href="activity_recommendation.php"><button class="btn btn-success">Recommend Activity</button></a>
                    <br><br>
                    <h3>Health Condition</h3>
                    <br>
                    <div style="overflow-x: auto;">
                    <table id="table_hc" class="display" style="width:100%; padding-top: 5px;">
                        <thead style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Health Condition</th>
                                <th>Edit</th>
                                <th>Delete</th>
                            </tr>
                        </thead>
                        <tbody id="tbody_hc"></tbody>
                        <tfoot style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Health Condition</th>
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
			<h2 id="mh">Add Health Condition</h2>
			<label>Health Condition:</label>
            <input id="hc_name" type="text" class="form-control">
            <input id="hc_name2" type="hidden" class="form-control">
            <div class="row">
                <div class="col-md-6">
                    <button style="margin-top: 30px; width: 100%;" class="btn btn-danger" onclick="closeModal()">Close</button>
                </div>
                <div id="div_save_hc" class="col-md-6">
                    <button id="save_hc" style="margin-top: 30px; width: 100%;" class="btn btn-success">Save</button>       
                </div>
                <div id="div_update_hc" class="col-md-6">
                    <button id="update_hc" style="margin-top: 30px; width: 100%;" class="btn btn-success">Update</button>       
                </div>
            </div>
		</div>
	</div>

    <script>
		function openModal() {
			document.getElementById("myModal").style.display = "block";
            document.getElementById('mh').innerHTML = "Add Health Condition";
            document.getElementById('hc_name').value = "";
            document.getElementById('hc_name2').value = "";
            document.getElementById("div_save_hc").style.display = "block";
            document.getElementById("div_update_hc").style.display = "none";
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

        import { getDatabase, ref, onValue, remove, set, get} 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        var hc_no = 0;
        var tbody_hc = document.getElementById("tbody_hc");

        function GetAllData() {
            const hcRef = ref(db, "Maintenance/My Profile/Health Conditions");
            get(hcRef).then((snapshot) => { 
                var hc = [];
                snapshot.forEach(childSnapshot => {
                    hc.push(childSnapshot.val());
                });
                DisplayToTable(hc);
                $('#table_hc').DataTable();    
            });
        }

        function DisplayToTable(hc) {
            hc_no = 0;
            tbody_hc.innerHTML = "";
            hc.forEach(element => {
                DisplayItemToTable(element.value);
            });
        }

        function DisplayItemToTable(value) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");

            td1.innerHTML = ++hc_no;
            td2.innerHTML = value;

            td3.innerHTML = '<button id="btn_edit" type="button" class="btn btn-info"><i class="fa-solid fa-pen-to-square"></i></button>';
            td4.innerHTML = '<button id="btn_delete" type="button" class="btn btn-danger"><i class="fa-sharp fa-solid fa-trash-can"></i></button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tbody_hc.appendChild(tr); 
        }

        window.onload = function() {
            GetAllData();
        };

        //When Save Activity is clicked
        const save_hc = document.getElementById('save_hc');
        save_hc.addEventListener('click', () => {
            let hc_name = document.getElementById('hc_name').value.trim();

            if (hc_name !== '') {
                const hcRef = ref(db, "Maintenance/My Profile/Health Conditions/" + hc_name);
                const dataToInsert = {
                    value: hc_name
                };
                set(hcRef, dataToInsert)
                .then(() => {
                    alert("Health Condition Added.");
                    PageRefresh();
                })
                .catch((error) => {
                    alert("Error inserting data: ", error);
                });
            } else {
                alert("Please fill up the necessary input.");
            } 
        });

        const update_hc = document.getElementById('update_hc');
        update_hc.addEventListener('click', () => {
            let hc_name = document.getElementById('hc_name').value.trim();
            let hc_name2 = document.getElementById('hc_name2').value.trim();

            if (hc_name !== '') {
                const hcRef = ref(db, "Maintenance/My Profile/Health Conditions/" + hc_name);
                const dataToInsert = {
                    value: hc_name
                };
                set(hcRef, dataToInsert)
                .then(() => {
                    console.log("Health Condition Added.");
                    if (hc_name !== hc_name2) {
                        const hcRef = ref(db, "Maintenance/My Profile/Health Conditions/" + hc_name2);
                        remove(hcRef)
                        .then(() => {
                            console.log("Health Condition deleted.");
                            alert("Health Condition Updated.")
                            PageRefresh();
                        })
                        .catch((error) => {
                            console.error("Error deleting node:", error);
                        });
                    }
                })
                .catch((error) => {
                    alert("Error adding data: ", error);
                });
            } else {
                alert("Please fill up the necessary input.");
            }
        });

        $('#tbody_hc').on('click', 'button#btn_edit', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#table_hc').DataTable().row(parentRow).index();
            const hc_name = $('#table_hc').DataTable().cell(rowIndex, 1).data();
            
            document.getElementById("myModal").style.display = "block";
            document.getElementById('hc_name').value = hc_name;
            document.getElementById('hc_name2').value = hc_name;
            document.getElementById('mh').innerHTML = "Edit Health Condition";
            document.getElementById("div_save_hc").style.display = "none";
            document.getElementById("div_update_hc").style.display = "block";
        });

        $('#tbody_hc').on('click', 'button#btn_delete', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#table_hc').DataTable().row(parentRow).index();
            const hc_name = $('#table_hc').DataTable().cell(rowIndex, 1).data();
            
            var confirmResult = confirm("Do you want to proceed in deleting this health condition?");
            if (confirmResult) {
                const hcRef = ref(db, "Maintenance/My Profile/Health Conditions/" + hc_name);
                remove(hcRef)
                .then(() => {
                    alert("Health Condition Deleted.");
                    PageRefresh();
                })
                .catch((error) => {
                    console.error("Error deleting data:", error);
                });
            } else {
                // user clicked "Cancel" (false)
                // Do something else here, or nothing
            }
        });

        function PageRefresh() {
            window.location.href = 'health_condition.php';
        }

    </script>
</body>
</html>