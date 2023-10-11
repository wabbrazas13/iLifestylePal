<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Foods</title>

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
			width: 80%;
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
                    <button class="btn btn-success" onclick="openAddFoodModal()"><i class="fa-solid fa-plus"></i> Add Food</button>
                    <br><br>
                    <h3>Foods List</h3>
                    <div style="overflow-x: auto;">
                    <table id="tbl_foods" class="display" style="width:100%; padding-top: 5px;">
                        <thead style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Image</th>
                                <th>Name</th>
                                <th>Category</th>
                                <th>Ingredients</th>
                                <th>Calories</th>
                                <th>Status</th>
                                <th>Edit</th>
                                <th>Delete</th>
                            </tr>
                        </thead>
                        <tbody id="tbody_foods"></tbody>
                        <tfoot style="background-color: #3D8361; color: white;">
                            <tr>
                                <th>#</th>
                                <th>Image</th>
                                <th>Name</th>
                                <th>Category</th>
                                <th>Ingredients</th>
                                <th>Calories</th>
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

    <div id="AddFoodModal" class="modal">
		<div class="modal-content">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h3 style="margin: 0;">Add Food</h3>
                <button onclick="closeAddFoodModal()" title="Close" style="border: none; background: none; padding: 0;">
                    <i class="fa-solid fa-xmark" style="color: #da0b16; font-size: 30px;"></i>
                </button>
            </div>
            <div style="display: flex; justify-content: center; align-items: center; background-color: black; padding: 1px;">
                <img id="food_image" src="https://firebasestorage.googleapis.com/v0/b/ilifestylepal.appspot.com/o/Food%2Fdefault.png?alt=media&token=ad5920df-a5dd-4ab2-b784-84be2ea46789" alt="" style="object-fit: contain; width: 100%; height: 200px;">
            </div><br>
            <input type="file" id="choose_file" accept="image/*"> 
			<label style="margin-top: 15px;">Food Name:</label>
            <input id="food_name" type="text" class="form-control"> 
			<label style="margin-top: 10px;">Food Category:</label>
            <select id="food_category" class="form-control">
                <option value="Breakfast">Breakfast</option>
                <option value="Lunch">Lunch</option>
                <option value="Dinner">Dinner</option>
                <option value="Snack">Snack</option>
            </select>
            <label style="margin-top: 10px;">Food Calories:</label>
            <input id="food_calories" type="number" class="form-control">
            <button id="btn_selectIngredients" style="margin-top: 20px; margin-bottom: 5px;" class="btn btn-success">Select Ingredients</button>
            <textarea id="food_ingredients" class="form-control" rows="3"></textarea>
            <div class="row">
                <div class="col-md-6">
                    <button style="margin-top: 30px; width: 100%;" class="btn btn-danger" onclick="closeAddFoodModal()">Close</button>
                </div>
                <div class="col-md-6">
                    <button id="btn_addFood" style="margin-top: 30px; width: 100%;" class="btn btn-success">Add Food</button>       
                </div>
            </div>
		</div>
	</div>

    <div id="AddIngredientsModal" class="modal">
		<div class="modal-content">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h4 style="margin: 0;">Select Ingredients</h4>
                <button onclick="closeAddIngredientsModal()" title="Close" style="border: none; background: none; padding: 0;">
                    <i class="fa-solid fa-xmark" style="color: #da0b16; font-size: 30px;"></i>
                </button>
            </div>
            <div id="ingredient-list" style="overflow-y: auto; margin-top: 15px; max-height: 500px;"></div>
		</div>
	</div>

    <script>
        function openAddFoodModal() {
			document.getElementById("AddFoodModal").style.display = "block";
		}
        function closeAddFoodModal() {
			document.getElementById("AddFoodModal").style.display = "none";
		}
        function closeAddIngredientsModal() {
			document.getElementById("AddIngredientsModal").style.display = "none";
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
        import { getStorage, ref as storageRef, uploadBytes, getDownloadURL } 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-storage.js";

        const db = getDatabase();
        const storage = getStorage();

        var food_no = 0;
        var tbody_foods = document.getElementById("tbody_foods");
        let selectedIngredients = [];

        function GetAllFoodsData() {
            const FoodsRef = ref(db, "Maintenance/Food/Meal");
            get(FoodsRef).then((snapshot) => {
                var foods = [];
                snapshot.forEach((childSnapshot) => {
                    const status = childSnapshot.child("status").val();
                    if (status === "confirmed") {
                        foods.push(childSnapshot.val());
                    }
                });
                DisplayToFoodsTable(foods);
                $('#tbl_foods').DataTable(); 
            });
        }

        function DisplayToFoodsTable(foods) {
            food_no = 0;
            tbody_foods.innerHTML = "";
            foods.forEach(element => {
                DisplayItemToFoodsTable(element.foodname, element.calorie, element.url, element.category, element.Ingredients, element.status);
            });
        }

        function DisplayItemToFoodsTable(foodname, calorie, url, category, ingredients, status) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");
            let td6 = document.createElement("td");
            let td7 = document.createElement("td");
            let td8 = document.createElement("td");
            let td9 = document.createElement("td");

            td1.innerHTML = ++food_no;
            td2.innerHTML = '<img style="height: 50px; width: 50px;" src="' + url + '" alt="">';            
            td3.innerHTML = foodname;
            td4.innerHTML = category;
            // Fetch ingredients and combine their keys separated by a comma
            let ingredientsKeys = Object.keys(ingredients);
            let ingredientsList = ingredientsKeys.join(", ");
            td5.innerHTML = ingredientsList;
            td6.innerHTML = calorie + " cal";
            td7.innerHTML = status;

            td8.innerHTML = '<button id="editFood" type="button" class="btn btn-info"><i class="fa-solid fa-pen-to-square"></i></button>';
            td9.innerHTML = '<button id="deleteFood" type="button" class="btn btn-danger"><i class="fa-sharp fa-solid fa-trash-can"></i></button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
            tr.appendChild(td6);
            tr.appendChild(td7);
            tr.appendChild(td8);
            tr.appendChild(td9);
            tbody_foods.appendChild(tr); 
        }

        window.onload = GetAllFoodsData;

        let btn_chooseFile = document.getElementById("choose_file");
        let foodImage = document.getElementById("food_image");

        btn_chooseFile.onchange = () => {
            let reader = new FileReader();
            reader.readAsDataURL(btn_chooseFile.files[0]);
            reader.onload = () => {
                foodImage.setAttribute("src",reader.result);
            }
        }

        const btn_addFood = document.getElementById('btn_addFood');
        btn_addFood.addEventListener('click', () => {
            let foodname = document.getElementById('food_name').value;
            let category = document.getElementById('food_category').value;
            let calories = document.getElementById('food_calories').value;

            if (foodname !== '' && foodname !== null && 
                category !== '' && category !== null && 
                calories !== '' && calories !== null) 
            {
                if (selectedIngredients.length === 0) {
                    // The selectedIngredients array is empty
                    alert("Please select the ingredients of the food.");
                } else {
                    // The selectedIngredients array is not empty
                    CheckIfFoodExist();
                }
            } else {
                alert("Please fill up the necessary input.");
            } 
        });

        function CheckIfFoodExist() {
            let foodname = document.getElementById('food_name').value;
            const foodNameLowercase = foodname.toLowerCase(); // Convert the foodname to lowercase
            const FoodsRef = ref(db, "Maintenance/Food/Meal");
            get(FoodsRef).then((snapshot) => {
                if (snapshot.exists() && Object.keys(snapshot.val()).some(key => key.toLowerCase() === foodNameLowercase)) {
                    alert("Food already exists!");
                } else {
                    UploadFoodImage();
                }
            }).catch((error) => {
                console.log("Error checking if food exists:", error);
            });
        }

        function UploadFoodImage() {
            let foodname = document.getElementById('food_name').value;
            const file = btn_chooseFile.files[0];
            const FoodsStorageRef = storageRef(getStorage(), "Food/"+ foodname +".jpg");

            if (file) {
                // File is not null or undefined, do something with it
                uploadBytes(FoodsStorageRef, file).then((snapshot) => {
                    getDownloadURL(FoodsStorageRef).then((url) => {
                        console.log('Image download URL:', url);
                        // Do something with the download URL, like add it to a database
                        saveFoodInfo(url);
                    }).catch((error) => {
                        console.log('Error getting download URL:', error);
                    });
                }).catch((error) => {
                    alert("Failed to upload image.");
                });
            } else {
                // File is null or undefined, handle the error
                var imgElement = document.getElementById("food_image");
                var imgSrc = imgElement.src;
                var imageSource = imgSrc;
                saveFoodInfo(imageSource);
            }
        }

        function saveFoodInfo(url) {
            let food = document.getElementById('food_name').value;
            let cat = document.getElementById('food_category').value;
            let cal = document.getElementById('food_calories').value;

            const FoodsRef = ref(db, "Maintenance/Food/Meal/"+food);
            const dataToInsert = {
                foodname: food,
                category: cat,
                calorie: cal,
                status: "confirmed",
                url: url
            };
            set(FoodsRef, dataToInsert)
            .then(() => {
                saveIngredients();
                alert("Food Added Successfully.");
            })
            .catch((error) => {
                console.log("Error inserting data: ", error);
            });
        }

        function saveIngredients() {
            let food = document.getElementById('food_name').value;
            const IngredientsRef = ref(db, "Maintenance/Food/Meal/" + food + "/Ingredients");
            selectedIngredients.forEach((ingredient) => {
                set(child(IngredientsRef, ingredient), true)
                .then(() => {
                    console.log("Ingredients Added Successfully.");
                })
                .catch((error) => {
                    console.log("Error inserting data: ", error);
                });
            });
            PageRefresh();
        }

        const btn_selectIngredients = document.getElementById('btn_selectIngredients');
        btn_selectIngredients.addEventListener('click', () => {
            const ingredientList = document.getElementById('ingredient-list');
            ingredientList.innerHTML = "";
            const food_ingredients = document.getElementById('food_ingredients');
            const IngredientsRef = ref(db, "Maintenance/Food/Ingredients");
            get(IngredientsRef).then((snapshot) => {
                snapshot.forEach((childSnapshot) => {
                    const ingredient = childSnapshot.child("value").val();
                    const status = childSnapshot.child("status").val();
                    if (status === "approved") {
                        const checkbox = document.createElement('input');
                        checkbox.type = 'checkbox';
                        checkbox.name = ingredient;
                        checkbox.id = ingredient;
                        checkbox.classList.add('checkbox-input');

                        const label = document.createElement('label');
                        label.htmlFor = ingredient;
                        label.appendChild(document.createTextNode(ingredient));
                        label.classList.add('checkbox-label');

                        if (selectedIngredients.includes(ingredient)) {
                            checkbox.checked = true;
                        }

                        checkbox.addEventListener('change', () => {
                            if (checkbox.checked) {
                                selectedIngredients.push(ingredient);
                            } else {
                                selectedIngredients = selectedIngredients.filter(item => item !== ingredient);
                            }
                            food_ingredients.value = selectedIngredients.join(', ');
                        });

                        ingredientList.appendChild(checkbox);
                        ingredientList.appendChild(label);
                        ingredientList.appendChild(document.createElement('br'));
                    }
                });
            });
            document.getElementById("AddIngredientsModal").style.display = "block";
        });

        $('#tbody_foods').on('click', 'button#editFood', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#tbl_foods').DataTable().row(parentRow).index();
            const cellData1 = $('#tbl_foods').DataTable().cell(rowIndex, 2).data();
            const cellData2 = $('#tbl_foods').DataTable().cell(rowIndex, 3).data();
            
            document.getElementById("myModal").style.display = "block";
            document.getElementById('food_name').value = cellData1;
            document.getElementById('food_calories').value = cellData2;

            const FoodsRef = ref(db, "Maintenance/Food Journal/"+cellData1);
            onValue(FoodsRef,(snapshot)=>{
                var url = snapshot.child("url").val();
                console.log(url);
                document.getElementById('food_url').value = url;   
                document.getElementById('fi').src = url;  
            })
        });

        $('#tbody_foods').on('click', 'button#deleteFood', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#tbl_foods').DataTable().row(parentRow).index();
            const foodname = $('#tbl_foods').DataTable().cell(rowIndex, 2).data();
            
            var confirmResult = confirm("Do you want to proceed in deleting this food?");
            if (confirmResult) {
                const FoodsRef = ref(db, "Maintenance/Food/Meal/" + foodname);
                remove(FoodsRef)
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
            window.location.href = 'foods_list.php';
        }
    </script>
</body>
</html>