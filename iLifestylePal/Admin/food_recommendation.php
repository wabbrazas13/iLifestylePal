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
                    <h3>Food Recommendation</h3>
                    <div style="overflow-x: auto;">
                    <table id="table_recommendation" class="display" style="width:100%; padding-top: 5px;">
                        <thead style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Health Condition</th>
                                <th>Recommended</th>
                                <th>Restricted</th>
                                <th>Edit</th>
                            </tr>
                        </thead>
                        <tbody id="tbody_recommendation"></tbody>
                        <tfoot style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Health Condition</th>
                                <th>Recommended</th>
                                <th>Restricted</th>
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
            <button id="btn_selectRecommended" style="margin-top: 20px; margin-bottom: 5px;" class="btn btn-success">Select Recommended Foods</button>
            <textarea id="food_recommended" class="form-control" rows="3" readonly></textarea>
            <button id="btn_selectRestricted" style="margin-top: 20px; margin-bottom: 5px;" class="btn btn-success">Select Restricted Foods</button>
            <textarea id="food_restricted" class="form-control" rows="3" readonly></textarea>
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

    <div id="FoodListModal" class="modal">
		<div class="modal-content">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h4 style="margin: 0;">Select Foods</h4>
                <button onclick="closeFoodListModal()" title="Close" style="border: none; background: none; padding: 0;">
                    <i class="fa-solid fa-xmark" style="color: #da0b16; font-size: 30px;"></i>
                </button>
            </div>
            <div id="food-list" style="overflow-y: auto; margin-top: 15px; max-height: 500px;"></div>
		</div>
	</div>

    <script>
		function closeModal() {
			document.getElementById("addRecommendation").style.display = "none";
		}
        function closeFoodListModal() {
			document.getElementById("FoodListModal").style.display = "none";
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
        let selectedRestricted = [];

        function GetAllData() {
            const recRef = ref(db, "Maintenance/Food/Recommendation");
            get(recRef).then((snapshot) => {
                const data = [];
                snapshot.forEach((childSnapshot) => {
                    const healthCondition = childSnapshot.key;
                    const recommendedKeys = childSnapshot.child("Recommended").exists() ? Object.keys(childSnapshot.child("Recommended").val()) : [];
                    const recommended = recommendedKeys.length > 0 ? "<ul><li>" + recommendedKeys.join("</li><li>") + "</li></ul>" : "<ul><li>Not specified</li></ul>";   
                    const restrictedKeys = childSnapshot.child("Restricted").exists() ? Object.keys(childSnapshot.child("Restricted").val()) : [];
                    const restricted = restrictedKeys.length > 0 ? "<ul><li>" + restrictedKeys.join("</li><li>") + "</li></ul>" : "<ul><li>Not specified</li></ul>";                  
                    data.push([healthCondition, recommended, restricted]);
                });
                DisplayToTable(data);
                $("#table_recommendation").DataTable();
            });
        }

        function DisplayToTable(recommendation) {
            rec_no = 0;
            tbody_recommendation.innerHTML = "";
            recommendation.forEach(element => {
                DisplayItemToTable(element[0], element[1], element[2]);
            });
        }

        function DisplayItemToTable(hc, rec, res) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");

            td1.innerHTML = ++rec_no;
            td2.innerHTML = hc;
            td3.innerHTML = rec;
            td4.innerHTML = res;

            td5.innerHTML = '<button id="btn_edit" type="button" class="btn btn-info"><i class="fa-solid fa-pen-to-square"></i></button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
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
            FetchFoodRecommendation();
        }

        window.addEventListener('load', () => {
            const selectElement = document.getElementById("hc");
            selectElement.addEventListener('change', handleHealthConditionChange);
        });

        const btn_addRecommendation = document.getElementById('btn_addRecommendation');
        btn_addRecommendation.addEventListener('click', () => {
            document.getElementById("addRecommendation").style.display = "block";
            FetchFoodRecommendation();
            //FetchRestrictedFoods();
        });

        function FetchFoodRecommendation() {
            let healthCondition = document.getElementById('hc').value;
            const food_recommended = document.getElementById('food_recommended');
            const food_restricted = document.getElementById('food_restricted');
            const recRef = ref(db, "Maintenance/Food/Recommendation/" + healthCondition);
            get(recRef).then((snapshot) => {
                selectedRecommended = [];
                selectedRestricted = [];

                // Check if the 'Recommended' child exists
                if (snapshot.child('Recommended').exists()) {
                // Iterate over the child nodes of 'Recommended'
                    snapshot.child('Recommended').forEach(recommendedChildSnapshot => {
                        // Add the key of the child node to the 'selectedRecommended' array
                        selectedRecommended.push(recommendedChildSnapshot.key);
                    });
                    food_recommended.value = selectedRecommended.join(', ');
                } else {
                    food_recommended.value = "Not Specified";
                }

                // Check if the 'Restricted' child exists
                if (snapshot.child('Restricted').exists()) {
                // Iterate over the child nodes of 'Restricted'
                    snapshot.child('Restricted').forEach(restrictedChildSnapshot => {
                        // Add the key of the child node to the 'selectedRestricted' array
                        selectedRestricted.push(restrictedChildSnapshot.key);
                    });
                    food_restricted.value = selectedRestricted.join(', ');
                } else {
                    food_restricted.value = "Not Specified";
                }
            });
        }

        const btn_selectRecommended = document.getElementById('btn_selectRecommended');
        btn_selectRecommended.addEventListener('click', () => {
            const foodList = document.getElementById('food-list');
            foodList.innerHTML = "";
            const food_recommended = document.getElementById('food_recommended');
            const foodsRef = ref(db, "Maintenance/Food/Meal");
            get(foodsRef).then((snapshot) => {
                snapshot.forEach((childSnapshot) => {
                    const foodname = childSnapshot.child("foodname").val();
                    const status = childSnapshot.child("status").val();
                    if (status === "confirmed") {
                        const checkbox = document.createElement('input');
                        checkbox.type = 'checkbox';
                        checkbox.name = foodname;
                        checkbox.id = foodname;
                        checkbox.classList.add('checkbox-input');

                        const label = document.createElement('label');
                        label.htmlFor = foodname;
                        label.appendChild(document.createTextNode(foodname));
                        label.classList.add('checkbox-label');

                        // check the checkbox if it was previously selected
                        if (selectedRecommended.includes(foodname)) {
                            checkbox.checked = true;
                        }

                        checkbox.addEventListener('change', () => {
                            if (checkbox.checked) {
                                selectedRecommended.push(foodname);
                            } else {
                                selectedRecommended = selectedRecommended.filter(item => item !== foodname);
                            }
                            food_recommended.value = selectedRecommended.join(', ');
                        });

                        foodList.appendChild(checkbox);
                        foodList.appendChild(label);
                        foodList.appendChild(document.createElement('br'));
                    }
                });
            });
            document.getElementById("FoodListModal").style.display = "block";
        });

        const btn_selectRestricted = document.getElementById('btn_selectRestricted');
        btn_selectRestricted.addEventListener('click', () => {
            const foodList = document.getElementById('food-list');
            foodList.innerHTML = "";
            const food_restricted = document.getElementById('food_restricted');
            const foodsRef = ref(db, "Maintenance/Food/Meal");
            get(foodsRef).then((snapshot) => {
                snapshot.forEach((childSnapshot) => {
                    const foodname = childSnapshot.child("foodname").val();
                    const status = childSnapshot.child("status").val();
                    if (status === "confirmed") {
                        const checkbox = document.createElement('input');
                        checkbox.type = 'checkbox';
                        checkbox.name = foodname;
                        checkbox.id = foodname;
                        checkbox.classList.add('checkbox-input');

                        const label = document.createElement('label');
                        label.htmlFor = foodname;
                        label.appendChild(document.createTextNode(foodname));
                        label.classList.add('checkbox-label');

                        // check the checkbox if it was previously selected
                        if (selectedRestricted.includes(foodname)) {
                            checkbox.checked = true;
                        }

                        checkbox.addEventListener('change', () => {
                            if (checkbox.checked) {
                                selectedRestricted.push(foodname);
                            } else {
                                selectedRestricted = selectedRestricted.filter(item => item !== foodname);
                            }
                            food_restricted.value = selectedRestricted.join(', ');
                        });

                        foodList.appendChild(checkbox);
                        foodList.appendChild(label);
                        foodList.appendChild(document.createElement('br'));
                    }
                });
            });
            document.getElementById("FoodListModal").style.display = "block";
        });

        $('#tbody_recommendation').on('click', 'button#btn_edit', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#table_recommendation').DataTable().row(parentRow).index();
            const hc = $('#table_recommendation').DataTable().cell(rowIndex, 1).data();
            
            document.getElementById('hc').value = hc;   
            FetchFoodRecommendation();
            document.getElementById("addRecommendation").style.display = "block";
        });

        const save_recommendation = document.getElementById('save_recommendation');
        save_recommendation.addEventListener('click', () => {
            let HealthCondition = document.getElementById('hc').value;

            var confirmResult = confirm("Do you want to proceed?");
            if (confirmResult) {
                const recommendationRef = ref(db, "Maintenance/Food/Recommendation/" + HealthCondition);
                remove(recommendationRef)
                .then(() => {
                    console.log("All child nodes removed successfully.");
                })
                .catch((error) => {
                    console.log("Error removing child nodes: ", error);
                });

                const recommededRef = ref(db, "Maintenance/Food/Recommendation/" + HealthCondition + "/Recommended");
                selectedRecommended.forEach((foodname) => {
                    set(child(recommededRef, foodname), true)
                    .then(() => {
                        console.log("Recommended Food/s Added Successfully.");
                    })
                    .catch((error) => {
                        console.log("Error inserting data: ", error);
                    });
                });
                const restrictedRef = ref(db, "Maintenance/Food/Recommendation/" + HealthCondition + "/Restricted");
                selectedRestricted.forEach((foodname) => {
                    set(child(restrictedRef, foodname), true)
                    .then(() => {
                        console.log("Restricted Food/s Added Successfully.");
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
            window.location.href = 'food_recommendation.php';
        }
    </script>
</body>
</html>