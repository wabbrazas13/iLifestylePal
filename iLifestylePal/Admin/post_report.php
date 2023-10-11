<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iLifestylePal - Post Report</title>

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
                    <button id="btn_post" class="btn btn-success"><i class="fa-solid fa-list"></i> Post</button>
                    <button id="btn_report" class="btn btn-secondary"><i class="fa-solid fa-circle-exclamation"></i> Report</button>
                    <button id="btn_archive" class="btn btn-success"><i class="fa-solid fa-folder-open"></i> Archive</button>
                    <br><br>
                    <h3>Post Report</h3><br>
                    <div style="overflow-x: auto;">
                        <table id="table_reports" class="display" style="width:100%; padding-top: 5px;">
                            <thead style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>Post ID</th>
                                    <th>Reported By</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>View</th>
                                </tr>
                            </thead>
                            <tbody id="tbody_reports"></tbody>
                            <tfoot style="background-color: #3D8361; color: white;">
                                <tr>
                                    <th>#</th>
                                    <th>Post ID</th>
                                    <th>Reported By</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>View</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div> 
        </div>
    </div>

    <div id="reportsModal" class="modal">
		<div class="modal-content">
            <input id="post_id" type="hidden" class="form-control">
            <input id="post_uid" type="hidden" class="form-control">
            <input id="report_uid" type="hidden" class="form-control">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h3 style="margin: 0;">Report Details</h3>
                <button onclick="closeModal()" title="Close" style="border: none; background: none; padding: 0;">
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
                <table id="table_report_details" class="table table-bordered">
                    <tbody>
                        <tr>
                            <td>URL</td>
                            <td style="color: blue; text-decoration: underline;"><a id="post_url" href="" target="_blank"></a></td>
                        </tr> 
                        <tr>
                            <td>Post ID</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Posted By</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Post Description</td>
                            <td></td>
                        </tr>     
                        <tr>
                            <td>Reported By</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Report Description</td>
                            <td></td>
                        </tr> 
                        <tr>
                            <td>Date & Time</td>
                            <td></td>
                        </tr>
                    <tbody>
                </table>
            </div>
            <div class="row">
                <div class="col-md-6">
                    <button onclick="closeModal()" style="margin-top: 30px; width: 100%;" class="btn btn-danger">Close Window</button>       
                </div>
                <div class="col-md-6">
                    <button id="del_post" style="margin-top: 30px; width: 100%;" class="btn btn-success">Archive Post</button>       
                </div>
            </div>
		</div>
	</div>

    <script>
		function closeModal() {
			document.getElementById("reportsModal").style.display = "none";
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

        import { getDatabase, ref, onValue, remove, set, push, get, serverTimestamp} 
        from "https://www.gstatic.com/firebasejs/9.16.0/firebase-database.js";

        const db = getDatabase();

        var rep_no = 0;
        var tbody_reports = document.getElementById("tbody_reports");

        function GetAllReports() {
            const reportsRef = ref(db, "Reports/Posts");
            get(reportsRef).then((snapshot)=>{ 
                const reports = [];
                snapshot.forEach((childSnapshot) => {
                    childSnapshot.forEach((grandchildSnapshot) => {
                        reports.push(grandchildSnapshot.val());
                    });
                });
                const sortedReports = reports.sort((a, b) => b.report_timestamp - a.report_timestamp);
                DisplayToTable(sortedReports);
                $('#table_reports').DataTable(); 
            });
        }

        function DisplayToTable(reports) {
            rep_no = 0;
            tbody_reports.innerHTML = "";
            reports.forEach(element => {
                DisplayItemToTable(element.post_uid, element.post_id, element.report_date, element.report_time, element.report_description, element.report_timestamp, element.report_uid);
            });
        }

        function DisplayItemToTable(post_uid, post_id, report_date, report_time, report_description, report_timestamp, report_uid) {
            let tr = document.createElement("tr");
            let td1 = document.createElement("td");
            let td2 = document.createElement("td");
            let td3 = document.createElement("td");
            let td4 = document.createElement("td");
            let td5 = document.createElement("td");
            let td6 = document.createElement("td");

            td1.innerHTML = ++rep_no;
            td2.innerHTML = post_id;
            td3.innerHTML = report_uid;
            td4.innerHTML = report_date;            
            td5.innerHTML = report_time;
            td6.innerHTML = '<button id="btn_view" type="button" class="btn btn-info"> <i class="fa-sharp fa-solid fa-eye"></i> </button>';

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            tr.appendChild(td4);
            tr.appendChild(td5);
            tr.appendChild(td6);
            tbody_reports.appendChild(tr); 
        }

        window.onload = function() {
            GetAllReports();
        };

        $('#tbody_reports').on('click', 'button#btn_view', (event) => {
            const parentRow = $(event.currentTarget).closest('tr');
            const rowIndex = $('#table_reports').DataTable().row(parentRow).index();
            const post_id = $('#table_reports').DataTable().cell(rowIndex, 1).data();
            const report_uid = $('#table_reports').DataTable().cell(rowIndex, 2).data();

            document.getElementById("reportsModal").style.display = "block";
            var table = document.getElementById("table_report_details");
            var post_url_link = document.getElementById("post_url");

            const reportRef = ref(db, "Reports/Posts/" + post_id + "/" + report_uid);
            get(reportRef).then((snapshot)=>{ 
                var report_description = snapshot.child("report_description").val(); 
                var report_date = snapshot.child("report_date").val();
                var report_time = snapshot.child("report_time").val();

                table.rows[4].cells[1].innerHTML = report_uid;
                table.rows[5].cells[1].innerHTML = report_description;
                table.rows[6].cells[1].innerHTML = report_date + "  " + report_time;
            });

            const PostRef = ref(db, "Posts/" + post_id);
            get(PostRef).then((snapshot)=>{ 
                var post_description = snapshot.child("post_description").val(); 
                var post_type = snapshot.child("post_type").val();
                var post_uid = snapshot.child("post_uid").val(); 
                var post_url = snapshot.child("post_url").val();

                table.rows[1].cells[1].innerHTML = post_id;
                table.rows[2].cells[1].innerHTML = post_uid;
                table.rows[3].cells[1].innerHTML = post_description;
                post_url_link.innerHTML = post_url;
                post_url_link.href = post_url;
                document.getElementById('post_uid').value = post_uid;

                if (post_type == "image") {
                    document.getElementById('post_image').src = post_url;
                    document.getElementById("post_image").style.display = "block";
                    document.getElementById("post_video").style.display = "none";
                } else {
                    document.getElementById("post_image").style.display = "none";
                    document.getElementById("post_video").style.display = "block";
                    const post_video = document.getElementById("post_video");
                    const mp4_source = document.getElementById("mp4_source");
                    const ogg_source = document.getElementById("ogg_source");
                    mp4_source.src = post_url;
                    ogg_source.src = post_url;
                    post_video.load(); // Reload the video to update the source
                }
            });
            document.getElementById('post_id').value = post_id;
            document.getElementById('report_uid').value = report_uid;
        });

        const btn_del_post = document.getElementById("del_post");
        btn_del_post.addEventListener("click", () => {
            let post_id = document.getElementById('post_id').value;
            let report_uid = document.getElementById('report_uid').value;

            var confirmResult = confirm("Archiving this post will hide it from the user. Do you want to continue?");
            if (confirmResult) {
                ArchivePost();
            } else {
                // user clicked "Cancel" (false)
                // Do something else here, or nothing
            }
        });

        function ArchivePost() {
            let post_id = document.getElementById('post_id').value;
            const postRef = ref(db, "Posts/" + post_id);
            get(postRef).then((snapshot) => { 
                const data = snapshot.val();
                const post_uid = data.post_uid;
                const post_url = data.post_url;
                const post_date = data.post_date;
                const post_description = data.post_description;
                const post_timestamp = data.post_timestamp;
                const post_privacy = data.post_privacy;
                const post_type = data.post_type;
                const post_time = data.post_time;

                const archiveRef = push(ref(db, "Reports/Archive"));
                const dataToInsert = {
                    post_uid: post_uid,
                    post_url: post_url,
                    post_description: post_description,
                    post_timestamp: post_timestamp,
                    post_date: post_date,
                    post_time: post_time,
                    post_privacy: post_privacy,
                    post_type: post_type
                };
                set(archiveRef, dataToInsert)
                .then(() => {
                    console.log("Archived Added.");
                    DeletePost();
                })
                .catch((error) => {
                    console.error("Archived Error: ", error);
                });
            });
        }

        function DeletePost() {
            let post_id = document.getElementById('post_id').value;

            const postRef = ref(db, "Posts/" + post_id);
            remove(postRef).then(() => {
                console.log("Post Deleted.");
            })
            .catch((error) => {
                console.error("Post Deletion Error:", error);
            });
            
            const reportRef = ref(db, "Reports/Posts/" + post_id);
            remove(reportRef).then(() => {
                console.log("Post Reports Deleted.");
            })
            .catch((error) => {
                console.error("Post Reports Deletion Error:", error);
            });
            AddNotification();
        }

        function AddNotification() {
            let post_id = document.getElementById('post_id').value;
            let post_uid = document.getElementById('post_uid').value;
            const timestamp = serverTimestamp();

            const date = new Date(); // Replace this with your date
            const options = { month: 'long', day: '2-digit', year: 'numeric' };
            const formattedDate = date.toLocaleDateString('en-US', options); // "March 01, 2023"
            const time = new Date(); // Replace this with your date
            const options2 = { hour: 'numeric', minute: 'numeric', hour12: true };
            const formattedTime = time.toLocaleTimeString('en-US', options2); // "09:40 AM"

            const notifRef = push(ref(db, "Notifications"));
            // Set the data at the random key location
            const dataToInsert = {
                notif_id: notifRef.key,
                notif_description: "We removed something you posted because it doesn't follow the iLifestylePal Community Standards.",
                notif_timestamp: timestamp,
                notif_uid: post_uid,
                notif_date: formattedDate,
                notif_time: formattedTime,
                notif_category: "Report",
                notif_status: "unread"
            };
            set(notifRef, dataToInsert)
            .then(() => {
                console.log("Notification Added.");
                PageRefresh();
            })
            .catch((error) => {
                console.error("Notification Error:", error);
            });
        }

        function PageRefresh() {
            window.location.href = 'post_report.php';
        }

        const btn_post = document.getElementById("btn_post");
        btn_post.addEventListener("click", () => {
            window.location.href = 'lifestyle_post.php';
        });

        const btn_report = document.getElementById("btn_report");
        btn_report.addEventListener("click", () => {
            window.location.href = 'post_report.php';
        });

        const btn_archive = document.getElementById("btn_archive");
        btn_archive.addEventListener("click", () => {
            window.location.href = 'post_archive.php';
        });
    </script>
</body>
</html>