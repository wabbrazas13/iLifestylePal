<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Lifestyle Post</title>

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
        #btn_post,
        #btn_report,
        #btn_archive
        {
            min-width: 150px;
            margin-top: 5px;
        }
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
			max-width: 700px;
            min-height: 550px;
		}
        #tbl_post_information {
            width: 100%;
            max-width: 100%;
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
                    <button id="btn_post" class="btn btn-secondary"><i class="fa-solid fa-list"></i> Post</button>
                    <button id="btn_report" class="btn btn-success"><i class="fa-solid fa-circle-exclamation"></i> Report</button>
                    <button id="btn_archive" class="btn btn-success"><i class="fa-solid fa-folder-open"></i> Archive</button>
                    <br><br>
                    <h3>Post List</h3><br>
                    <div style="overflow-x: auto;">
                        <table id="tbl_lifestyle_posts" class="display" style="width:100%; padding-top: 5px;">
                            <thead style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>User ID</th>
                                    <th>Post ID</th>
                                    <th>Type</th>
                                    <th>Privacy</th>
                                    <th>DateTime</th>
                                    <th>View</th>
                                </tr>
                            </thead>
                            <tbody id="tbody_lifestyle_posts"></tbody>
                            <tfoot style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>User ID</th>
                                    <th>Post ID</th>
                                    <th>Type</th>
                                    <th>Privacy</th>
                                    <th>DateTime</th>
                                    <th>View</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div> 
        </div>
    </div>

    <div id="postsModal" class="modal">
		<div class="modal-content">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h3 style="margin: 0;">Post Details</h3>
                <button onclick="closePostsModal()" title="Close" style="border: none; background: none; padding: 0;">
                    <i class="fa-solid fa-xmark" style="color: #da0b16; font-size: 30px;"></i>
                </button>
            </div>
            <br>
            <div style="display: flex; justify-content: center; align-items: center; background-color: black;">
                <img id="post_image" src="" alt="" style="object-fit: contain; width: 100%; height: 300px;">
                <video id="post_video" width="100%" height="300px" controls>
                    <source id="mp4_source" src="" type="video/mp4">
                    <source id="ogg_source" src="" type="video/ogg">
                    Your browser does not support the video tag.
                </video>
            </div><br>
            <div style="overflow-x: auto;">
                <table id="tbl_post_information" class="table table-bordered">
                    <tbody>
                        <tr>
                            <td>User ID</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Post ID</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Timestamp</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Date</td>
                            <td></td>
                        </tr>     
                        <tr>
                            <td>Time</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Type</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Privacy</td>
                            <td></td>
                        </tr>   
                        <tr>
                            <td>URL</td>
                            <td style="color: blue; text-decoration: underline;"><a id="post_url" href="" target="_blank"></a></td>
                        </tr> 
                        <tr>
                            <td>Description</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Likes</td>
                            <td></td>
                        </tr>  
                        <tr>
                            <td>Comments</td>
                            <td></td>
                        </tr> 
                    <tbody>
                </table>
            </div>
		</div>
	</div>

    <script>
		function closePostsModal() {
			document.getElementById("postsModal").style.display = "none";
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

        import { getDatabase, ref, onValue, get} 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        var post_no = 0;
        var tbody_lifestyle_posts = document.getElementById("tbody_lifestyle_posts");

        function GetAllPosts() {
            const PostsRef = ref(db, "Posts");
            get(PostsRef).then((snapshot) => { 
                const posts = [];
                snapshot.forEach((childSnapshot) => {
                    const post_id = childSnapshot.key; // Get the key of the post
                    const data = childSnapshot.val(); // Get the data of the post
                    const post = { post_id, ...data }; // Create an object that contains both the key and data
                    posts.push(post); // Push the object to the posts array
                });
                const sortedPosts = posts.sort((a, b) => b.post_timestamp - a.post_timestamp);
                DisplayToTable(sortedPosts);
                $('#tbl_lifestyle_posts').DataTable(); 
            });
        }

        function DisplayToTable(posts) {
            post_no = 0;
            tbody_lifestyle_posts.innerHTML = "";
            posts.forEach(element => {
                DisplayItemToTable(element.post_uid, element.post_id, element.post_date, element.post_time, element.post_type, element.post_privacy);
            });
        }

        function DisplayItemToTable(post_uid, post_id, post_date, post_time, post_type, post_privacy) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");
            let td6 = document.createElement("td");
            let td7 = document.createElement("td");

            td1.innerHTML = ++post_no;
            td2.innerHTML = post_uid;
            td3.innerHTML = post_id;
            td4.innerHTML = post_type.charAt(0).toUpperCase() + post_type.slice(1);            
            td5.innerHTML = post_privacy;
            td6.innerHTML = post_date + " " + post_time;
            td7.innerHTML = '<button id="btn_view" type="button" class="btn btn-info"> <i class="fa-sharp fa-solid fa-eye"></i> </button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
            tr.appendChild(td6);
            tr.appendChild(td7);
            tbody_lifestyle_posts.appendChild(tr); 
        }

        $('#tbody_lifestyle_posts').on('click', 'button#btn_view', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#tbl_lifestyle_posts').DataTable().row(parentRow).index();
            const post_id = $('#tbl_lifestyle_posts').DataTable().cell(rowIndex, 2).data();
            const post_type = $('#tbl_lifestyle_posts').DataTable().cell(rowIndex, 3).data();
            
            document.getElementById("postsModal").style.display = "block";
            var table = document.getElementById("tbl_post_information");
            var post_url_link = document.getElementById("post_url");
            
            if (post_type == "Image") {
                document.getElementById("post_image").style.display = "block";
                document.getElementById("post_video").style.display = "none";
            } else {
                document.getElementById("post_image").style.display = "none";
                document.getElementById("post_video").style.display = "block";
            }

            const PostRef = ref(db, "Posts/" + post_id);
            onValue(PostRef,(snapshot)=>{
                var post_date = snapshot.child("post_date").val(); 
                var post_time = snapshot.child("post_time").val();   
                var post_description = snapshot.child("post_description").val(); 
                var post_privacy = snapshot.child("post_privacy").val(); 
                var post_timestamp = snapshot.child("post_timestamp").val(); 
                var post_uid = snapshot.child("post_uid").val(); 
                var post_url = snapshot.child("post_url").val();

                table.rows[0].cells[1].innerHTML = post_uid;
                table.rows[1].cells[1].innerHTML = post_id;
                table.rows[2].cells[1].innerHTML = post_timestamp;
                table.rows[3].cells[1].innerHTML = post_date;
                table.rows[4].cells[1].innerHTML = post_time;
                table.rows[5].cells[1].innerHTML = post_type;
                table.rows[6].cells[1].innerHTML = post_privacy;
                post_url_link.innerHTML = post_url;
                post_url_link.href= post_url;
                table.rows[8].cells[1].innerHTML = post_description;

                if (post_type == "Image") {
                    document.getElementById('post_image').src = post_url; 
                } else {
                    const post_video = document.getElementById("post_video");
                    const mp4_source = document.getElementById("mp4_source");
                    const ogg_source = document.getElementById("ogg_source");
                    mp4_source.src = post_url;
                    ogg_source.src = post_url;
                    post_video.load(); // Reload the video to update the source
                }
            })

            const LikesRef = ref(db, "Posts/" + post_id + "/Likes");
            onValue(LikesRef, (snapshot) => {
                if (snapshot.exists()) {
                    const likes = snapshot.val();
                    const count = Object.keys(likes).length;
                    table.rows[9].cells[1].innerHTML = count + " likes";
                } else {
                    table.rows[9].cells[1].innerHTML = "0 likes";
                }
            });

            const CommentsRef = ref(db, "Posts/" + post_id + "/Comments");
            onValue(CommentsRef, (snapshot) => {
                if (snapshot.exists()) {
                    const comments = snapshot.val();
                    const count = Object.keys(comments).length;
                    table.rows[10].cells[1].innerHTML = count + " comments";
                } else {
                    table.rows[10].cells[1].innerHTML = "0 comment";
                }
            });
        });

        window.onload = function() {
            GetAllPosts();
        };

        const btn_posts = document.getElementById("btn_post");
        btn_posts.addEventListener("click", () => {
            window.location.href = 'lifestyle_post.php';
        });

        const btn_reports = document.getElementById("btn_report");
        btn_reports.addEventListener("click", () => {
            window.location.href = 'post_report.php';
        });

        const btn_archive = document.getElementById("btn_archive");
        btn_archive.addEventListener("click", () => {
            window.location.href = 'post_archive.php';
        });
    </script>
</body>
</html>