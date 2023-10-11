<nav id="sidebar">
    <div class="sidebar-header">
        <div style="margin-left: auto; margin-right: auto; width: fit-content;">
            <img src="images/admin.png" alt="Admin Profile">
        </div>
        <div style="margin-left: auto; margin-right: auto; width: fit-content;">
            <h1>ADMIN</h1>
        </div>
    </div>
    <ul class="components">
        <!-- <li class="<?php if (basename($_SERVER['PHP_SELF']) === 'home.php') {echo 'active';} ?>">
            <a href="home.php"><i class="fa-solid fa-house-chimney"></i>Admin Dashboard</a>
        </li> -->
        <li class="<?php if (basename($_SERVER['PHP_SELF']) === 'users_information.php') {echo 'active';} ?>">
            <a href="users_information.php"><i class="fa-solid fa-users"></i>Users Information</a>
        </li>
        <li class="<?php if (basename($_SERVER['PHP_SELF']) === 'lifestyle_post.php') {echo 'active';} ?>">
            <a href="lifestyle_post.php"><i class="fa-solid fa-rectangle-list"></i>Lifestyle Post</a>
        </li>
        <li class="<?php if (basename($_SERVER['PHP_SELF']) === 'daily_activity.php') {echo 'active';} ?>">
            <a href="daily_activity.php"><i class="fa-regular fa-calendar-days"></i>Daily Activity</a>
        </li>
        <li class="<?php if (basename($_SERVER['PHP_SELF']) === 'food_journal.php') {echo 'active';} ?>">
            <a href="food_journal.php"><i class="fa-solid fa-utensils"></i>Food Journal</a>
        </li>
        <li class="<?php if (basename($_SERVER['PHP_SELF']) === 'sleep_schedule.php') {echo 'active';} ?>">
            <a href="sleep_schedule.php"><i class="fa-solid fa-bed"></i>Sleep Schedule</a>
        </li>
        <li class="<?php if (basename($_SERVER['PHP_SELF']) === 'step_tracker.php') {echo 'active';} ?>">
            <a href="step_tracker.php"><i class="fa-solid fa-shoe-prints"></i>Step Tracker</a>
        </li>
        <li>
            <a id="maintenance-link"><i class="fa-solid fa-screwdriver-wrench"></i>Maintenance</a>
            <ul id="maintenance-sublist">
                <li class="<?php if  (basename($_SERVER['PHP_SELF']) == 'health_condition.php'){echo 'active'; } ?>">
                    <a href="health_condition.php">Health Condition</a>
                </li>
                <li class="<?php if  (basename($_SERVER['PHP_SELF']) == 'activity_recommendation.php'){echo 'active'; } ?>">
                    <a href="activity_recommendation.php">Recommend Activity</a>
                </li>
                <li class="<?php if  (basename($_SERVER['PHP_SELF']) == 'food_recommendation.php'){echo 'active'; } ?>">
                    <a href="food_recommendation.php">Recommend Food</a>
                </li>
                <li class="<?php if  (basename($_SERVER['PHP_SELF']) == 'activities_list.php'){echo 'active'; } ?>">
                    <a href="activities_list.php">Add Activity</a>
                </li>
                <li class="<?php if  (basename($_SERVER['PHP_SELF']) == 'foods_list.php'){echo 'active'; } ?>">
                    <a href="foods_list.php">Add Food</a>
                </li>
                <li class="<?php if  (basename($_SERVER['PHP_SELF']) == 'ingredients_list.php'){echo 'active'; } ?>">
                    <a href="ingredients_list.php">Add Ingredient</a>
                </li>
            </ul>
        </li>
        <li>
            <a href="#" onclick="confirmLogout();"><i class="fa-solid fa-right-from-bracket"></i>Logout</a>
        </li>
    </ul>
</nav>

<script>
    var maintenanceLink = document.getElementById("maintenance-link");
    var maintenanceSublist = document.getElementById("maintenance-sublist");
    
    maintenanceLink.addEventListener("click", function() {
        maintenanceSublist.classList.toggle("show");
    });

    function confirmLogout() {
        if (confirm("Are you sure you want to logout?")) {
            // Remove isLoggedIn flag from localStorage
            localStorage.removeItem('isLoggedIn');
            window.location.href = "index.php";
        }
    }
</script>