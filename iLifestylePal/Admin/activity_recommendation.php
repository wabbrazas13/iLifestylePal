<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Recommendation</title>

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
			margin: 2% auto;
			padding: 20px;
			border: 1px solid #888;
			width: 100%;
			max-width: 600px;
		}
        .checkbox-label {
            margin-bottom: 10px;
        }
        .checkbox-input {
            margin-right: 20px;
            margin-left: 10px;
            transform: scale(1.5);
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
                    <button id="btn_addRecommendation" class="btn btn-success"><i class="fa-solid fa-plus"></i> Recommendation</button>
                    <br><br>
                    <h3>Activity Recommendation</h3>
                    <div style="overflow-x: auto;">
                    <table id="table_recommendation" class="display" style="width:100%; padding-top: 5px;">
                        <thead style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Health Condition</th>
                                <th>Recommended</th>
                                <th>Edit</th>
                            </tr>
                        </thead>
                        <tbody id="tbody_recommendation"></tbody>
                        <tfoot style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Health Condition</th>
                                <th>Recommended</th>
                                <th>Edit</th>
                            </tr>
                        </tfoot>
                    </table>
                    </div>
                </div>
            </div> 
        </div>
    </div>

    <div id="addRecommendation" class="modal">
		<div class="modal-content">
			<h2>Add Recommendation</h2>
			<label style="margin-top: 5px;">Health Condition:</label>
            <select id="hc" class="form-control"></select>
            <button id="btn_selectRecommended" style="margin-top: 20px; margin-bottom: 5px;" class="btn btn-success">Select Recommended Activity</button>
            <textarea id="act_recommended" class="form-control" rows="3" readonly></textarea>
            <div class="row">
                <div class="col-md-6">
                    <button style="margin-top: 30px; width: 100%;" class="btn btn-danger" onclick="closeModal()">Close</button>
                </div>
                <div id="div_save_hc" class="col-md-6">
                    <button id="save_recommendation" style="margin-top: 30px; width: 100%;" class="btn btn-success">Save</button>       
                </div>
            </div>
		</div>
	</div>

    <div id="ActListModal" class="modal">
		<div class="modal-content">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h4 style="margin: 0;">Select Activities</h4>
                <button onclick="closeFoodListModal()" title="Close" style="border: none; background: none; padding: 0;">
                    <i class="fa-solid fa-xmark" style="color: #da0b16; font-size: 30px;"></i>
                </button>
            </div>
            <div id="act-list" style="overflow-y: auto; margin-top: 15px; max-height: 500px;"></div>
		</div>
	</div>

    <script>
		function closeModal() {
			document.getElementById("addRecommendation").style.display = "none";
		}
        function closeFoodListModal() {
			document.getElementById("ActListModal").style.display = "none";
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

        import { getDatabase, ref, onValue, remove, set, get, child } 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        var rec_no = 0;
        var tbody_recommendation = document.getElementById("tbody_recommendation");
        let selectedRecommended = [];

        function GetAllData() {
            const recRef = ref(db, "Maintenance/Daily Activity/Recommendation");
            get(recRef).then((snapshot) => {
                const data = [];
                snapshot.forEach((childSnapshot) => {
                    const healthCondition = childSnapshot.key;
                    const recommendedKeys = childSnapshot.exists() ? Object.keys(childSnapshot.val()) : [];
                    const recommended = recommendedKeys.length > 0 ? "<ul><li>" + recommendedKeys.join("</li><li>") + "</li></ul>" : "<ul><li>Not specified</li></ul>";   
                    data.push([healthCondition, recommended]);
                });
                DisplayToTable(data);
                $("#table_recommendation").DataTable();
            });
        }

        function DisplayToTable(recommendation) {
            rec_no = 0;
            tbody_recommendation.innerHTML = "";
            recommendation.forEach(element => {
                DisplayItemToTable(element[0], element[1]);
            });
        }

        function DisplayItemToTable(hc, rec) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");

            td1.innerHTML = ++rec_no;
            td2.innerHTML = hc;
            td3.innerHTML = rec;
            td4.innerHTML = '<button id="btn_edit" type="button" class="btn btn-info"><i class="fa-solid fa-pen-to-square"></i></button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tbody_recommendation.appendChild(tr); 
        }

        window.addEventListener("load", GetAllData);

        function GetHealthConditions() {
            const hcRef = ref(db, "Maintenance/My Profile/Health Conditions");
            get(hcRef).then((snapshot) => {
                let html = ""; // initialize an empty string to store the generated HTML
                snapshot.forEach((childSnapshot) => {
                    const hcKey = childSnapshot.key; // get the key of each child snapshot
                    html += `<option value="${hcKey}">${hcKey}</option>`; // add an option element to the HTML string for each child key
                });
                document.getElementById("hc").innerHTML = html; // set the innerHTML property of the select element to the generated HTML
            });
        }

        window.addEventListener("load", GetHealthConditions);

        // module script
        function handleHealthConditionChange() {
            const selectedOptionIndex = document.getElementById("hc").selectedIndex;
            const selectedOptionValue = document.getElementById("hc").options[selectedOptionIndex].value;
            // Do something with the selected option value
            FetchActivityRecommendation();
        }

        window.addEventListener('load', () => {
            const selectElement = document.getElementById("hc");
            selectElement.addEventListener('change', handleHealthConditionChange);
        });

        const btn_addRecommendation = document.getElementById('btn_addRecommendation');
        btn_addRecommendation.addEventListener('click', () => {
            document.getElementById("addRecommendation").style.display = "block";
            FetchActivityRecommendation();
            //FetchRestrictedFoods();
        });

        function FetchActivityRecommendation() {
            let healthCondition = document.getElementById('hc').value;
            const act_recommended = document.getElementById('act_recommended');
            const recRef = ref(db, "Maintenance/Daily Activity/Recommendation/" + healthCondition);
            get(recRef).then((snapshot) => {
                selectedRecommended = [];

                // Check if the 'Recommended' child exists
                if (snapshot.exists()) {
                // Iterate over the child nodes of 'Recommended'
                    snapshot.forEach(recommendedChildSnapshot => {
                        // Add the key of the child node to the 'selectedRecommended' array
                        selectedRecommended.push(recommendedChildSnapshot.key);
                    });
                    act_recommended.value = selectedRecommended.join(', ');
                } else {
                    act_recommended.value = "Not Specified";
                }
            });
        }

        const btn_selectRecommended = document.getElementById('btn_selectRecommended');
        btn_selectRecommended.addEventListener('click', () => {
            const actList = document.getElementById('act-list');
            actList.innerHTML = "";
            const act_recommended = document.getElementById('act_recommended');
            const actsRef = ref(db, "Maintenance/Daily Activity/Activities");
            get(actsRef).then((snapshot) => {
                snapshot.forEach((childSnapshot) => {
                    const activityName = childSnapshot.child("activityName").val();

                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.name = activityName;
                    checkbox.id = activityName;
                    checkbox.classList.add('checkbox-input');

                    const label = document.createElement('label');
                    label.htmlFor = activityName;
                    label.appendChild(document.createTextNode(activityName));
                    label.classList.add('checkbox-label');

                    // check the checkbox if it was previously selected
                    if (selectedRecommended.includes(activityName)) {
                        checkbox.checked = true;
                    }

                    checkbox.addEventListener('change', () => {
                        if (checkbox.checked) {
                            selectedRecommended.push(activityName);
                        } else {
                            selectedRecommended = selectedRecommended.filter(item => item !== activityName);
                        }
                        act_recommended.value = selectedRecommended.join(', ');
                    });

                    actList.appendChild(checkbox);
                    actList.appendChild(label);
                    actList.appendChild(document.createElement('br'));
                });
            });
            document.getElementById("ActListModal").style.display = "block";
        });

        $('#tbody_recommendation').on('click', 'button#btn_edit', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#table_recommendation').DataTable().row(parentRow).index();
            const hc = $('#table_recommendation').DataTable().cell(rowIndex, 1).data();
            
            document.getElementById('hc').value = hc;   
            FetchActivityRecommendation();
            document.getElementById("addRecommendation").style.display = "block";
        });

        const save_recommendation = document.getElementById('save_recommendation');
        save_recommendation.addEventListener('click', () => {
            let HealthCondition = document.getElementById('hc').value;

            var confirmResult = confirm("Do you want to proceed?");
            if (confirmResult) {
                const recommendationRef = ref(db, "Maintenance/Daily Activity/Recommendation/" + HealthCondition);
                remove(recommendationRef)
                .then(() => {
                    console.log("All child nodes removed successfully.");
                })
                .catch((error) => {
                    console.log("Error removing child nodes: ", error);
                });

                const recommendedRef = ref(db, "Maintenance/Daily Activity/Recommendation/" + HealthCondition);
                selectedRecommended.forEach((foodname) => {
                    set(child(recommendedRef, foodname), true)
                    .then(() => {
                        console.log("Recommended Food/s Added Successfully.");
                    })
                    .catch((error) => {
                        console.log("Error inserting data: ", error);
                    });
                });
                PageRefresh();
            } else {
                // user clicked "Cancel" (false)
                // Do something else here, or nothing
            }
        });

        function PageRefresh() {
            window.location.href = 'activity_recommendation.php';
        }
    </script>
</body>
</html>