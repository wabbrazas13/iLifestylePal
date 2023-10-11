<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Ingredients</title>

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
                    <button onclick="openModal()" class="btn btn-success"><i class="fa-solid fa-plus"></i> Add Ingredient</button>
                    <button id="btn_approved" class="btn btn-info"><i class="fa-solid fa-thumbs-up"></i> Approved</button>
                    <button id="btn_pending" class="btn btn-success"><i class="fa-solid fa-clock"></i> Pending</button>
                    <br><br>
                    <h3>Approved Ingredients</h3>
                    <div style="overflow-x: auto;">
                    <table id="table_fa" class="display" style="width:100%; padding-top: 5px;">
                        <thead style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Ingredient</th>
                                <th>Status</th>
                                <th>Edit</th>
                                <th>Delete</th>
                            </tr>
                        </thead>
                        <tbody id="tbody_fa"></tbody>
                        <tfoot style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Ingredient</th>
                                <th>Status</th>
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
			<h2>Add Ingredient</h2>
			<label>Ingredient:</label>
            <input id="fa_name" type="text" class="form-control">
            <div class="row">
                <div class="col-md-6">
                    <button style="margin-top: 30px; width: 100%;" class="btn btn-danger" onclick="closeModal()">Close</button>
                </div>
                <div class="col-md-6">
                    <button id="save_fa" style="margin-top: 30px; width: 100%;" class="btn btn-success">Save</button>       
                </div>
            </div>
		</div>
	</div>

    <div id="myModal2" class="modal">
		<div class="modal-content">
			<h2>Edit Ingredient</h2>
			<label>Ingredient:</label>
            <input id="new_fa" type="text" class="form-control">
            <input id="prev_fa" type="hidden" class="form-control">
            <div class="row">
                <div class="col-md-6">
                    <button style="margin-top: 30px; width: 100%;" class="btn btn-danger" onclick="closeModal()">Close</button>
                </div>
                <div class="col-md-6">
                    <button id="update_fa" style="margin-top: 30px; width: 100%;" class="btn btn-success">Update</button>       
                </div>
            </div>
		</div>
	</div>

    <script>
		function openModal() {
			document.getElementById("myModal").style.display = "block";
		}
		function closeModal() {
			document.getElementById("myModal").style.display = "none";
            document.getElementById("myModal2").style.display = "none";
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

        var fa_no = 0;
        var tbody_fa = document.getElementById("tbody_fa");

        function GetAllData() {
            const faRef = ref(db, "Maintenance/Food/Ingredients");
            get(faRef).then((snapshot) => {
                var fa = [];
                snapshot.forEach((childSnapshot) => {
                    const status = childSnapshot.child("status").val();
                    if (status === "approved") {
                        fa.push(childSnapshot.val());
                    }
                });
                DisplayToTable(fa);
                $("#table_fa").DataTable();
            });
        }

        function DisplayToTable(fa) {
            fa_no = 0;
            tbody_fa.innerHTML = "";
            fa.forEach(element => {
                DisplayItemToTable(element.value, element.status);
            });
        }

        function DisplayItemToTable(value, status) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");

            td1.innerHTML = ++fa_no;
            td2.innerHTML = value;
            td3.innerHTML = status;

            td4.innerHTML = '<button id="btn_edit" type="button" class="btn btn-info"><i class="fa-solid fa-pen-to-square"></i></button>';
            td5.innerHTML = '<button id="btn_delete" type="button" class="btn btn-danger"><i class="fa-sharp fa-solid fa-trash-can"></i></button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
            tbody_fa.appendChild(tr); 
        }

        window.addEventListener("load", GetAllData);

        //When Save Button is clicked
        const save_fa = document.getElementById('save_fa');
        save_fa.addEventListener('click', () => {
            let fa_name = document.getElementById('fa_name').value.trim();

            if (fa_name !== '') {
                const faRef = ref(db, "Maintenance/Food/Ingredients/" + fa_name);
                const dataToInsert = {
                    value: fa_name,
                    status: 'approved'
                };
                set(faRef, dataToInsert)
                .then(() => {
                    alert("Ingredient Added.");
                    PageRefresh();
                })
                .catch((error) => {
                    alert("Error inserting data: ", error);
                });
            } else {
                alert("Please fill up the necessary input.");
            } 
        });

        $('#tbody_fa').on('click', 'button#btn_edit', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#table_fa').DataTable().row(parentRow).index();
            const prev_fa = $('#table_fa').DataTable().cell(rowIndex, 1).data();
            
            document.getElementById("myModal2").style.display = "block";
            document.getElementById('prev_fa').value = prev_fa;
            document.getElementById('new_fa').value = prev_fa;
        });

        $('#tbody_fa').on('click', 'button#btn_delete', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#table_fa').DataTable().row(parentRow).index();
            const fa_name = $('#table_fa').DataTable().cell(rowIndex, 1).data();
            
            var confirmResult = confirm("Do you want to proceed in deleting this ingredient?");
            if (confirmResult) {
                DeleteIngredient(fa_name);
                PageRefresh();
            } else {
                // user clicked "Cancel" (false)
                // Do something else here, or nothing
            }
        });

        const update_fa = document.getElementById('update_fa');
        update_fa.addEventListener('click', () => {
            let new_fa = document.getElementById('new_fa').value.trim();
            let prev_fa = document.getElementById('prev_fa').value.trim();

            if (new_fa !== prev_fa) {
                if (new_fa !== '') {
                    const faRef = ref(db, "Maintenance/Food/Ingredients/" + new_fa);
                    const dataToInsert = {
                        value: new_fa,
                        status: 'approved'
                    };
                    const prevRef = ref(db, "Maintenance/Food/Ingredients/" + prev_fa);
                    remove(prevRef).then(() => {
                        console.log("Ingredient Deleted.");
                        set(faRef, dataToInsert).then(() => {
                            alert("Ingredient Updated.");
                            PageRefresh();
                        }).catch((error) => {
                            alert("Error inserting data: ", error);
                        });
                    }).catch((error) => {
                        console.error("Error deleting data:", error);
                    });
                } else {
                    alert("Please fill up the necessary input.");
                }
            } else {
                alert("Please change something to update.");
            }
        });

        function DeleteIngredient(fa) {
            const faRef = ref(db, "Maintenance/Food/Ingredients/" + fa);
            remove(faRef)
            .then(() => {
                console.log("Ingredient Deleted.");
            })
            .catch((error) => {
                console.error("Error deleting node:", error);
            });
        }

        function PageRefresh() {
            window.location.href = 'ingredients_list.php';
        }

        const btn_pending = document.getElementById("btn_pending");
        btn_pending.addEventListener("click", () => {
            window.location.href = 'ingredients_pending.php';
        });

    </script>
</body>
</html>