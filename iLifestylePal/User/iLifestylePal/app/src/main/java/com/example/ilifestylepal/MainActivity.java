package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private LinearLayout ll_notif;
    private TextView tv_notif_count;
    private ImageView iv_notif;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private String currentUserID;

    private CircleImageView nav_user_img;
    private TextView nav_username, nav_email;
    private LinearLayout linear_public, linear_add_post, linear_friends;
    private int btn_active = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child("Daily Activity").child("User ID");
        dbreference.push();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        nav_user_img = navView.findViewById(R.id.nav_user_img);
        nav_username = navView.findViewById(R.id.nav_username);
        nav_email = navView.findViewById(R.id.nav_email);

        ll_notif = findViewById(R.id.ll_notif);
        tv_notif_count = findViewById(R.id.tv_notif_count);
        iv_notif = findViewById(R.id.iv_notif);

        linear_public = findViewById(R.id.linear_public);
        linear_add_post = findViewById(R.id.linear_add_post);
        linear_friends = findViewById(R.id.linear_friends);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nav_email.setText(mAuth.getCurrentUser().getEmail());

        DatabaseReference NotifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.hasChild("notif_uid") &&
                            childSnapshot.hasChild("notif_status") &&
                            childSnapshot.child("notif_uid").getValue().equals(currentUserID) &&
                            childSnapshot.child("notif_status").getValue().equals("unread")) {
                        count++;
                    }
                }
                if (count == 0) {
                    tv_notif_count.setVisibility(View.GONE);
                } else if (count < 100) {
                    tv_notif_count.setVisibility(View.VISIBLE);
                    tv_notif_count.setText(count + "+");
                } else {
                    tv_notif_count.setVisibility(View.VISIBLE);
                    tv_notif_count.setText("99+");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Notifications", "Error: " + error.getMessage());
            }
        });

        ll_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notif = new Intent(MainActivity.this, Notifications.class);
                startActivity(notif);
            }
        });

        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String firstname = "", lastname = "";

                    if(snapshot.hasChild("firstname"))
                    {
                        firstname = snapshot.child("firstname").getValue().toString();
                    }
                    if(snapshot.hasChild("lastname"))
                    {
                        lastname = snapshot.child("lastname").getValue().toString();
                    }
                    if(snapshot.hasChild("pic_url"))
                    {
                        String picURL = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(MainActivity.this).load(picURL).placeholder(R.drawable.ic_profile).into(nav_user_img);
                    }

                    nav_username.setText(firstname + " " + lastname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        linear_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPostDialog addPostDialog = new AddPostDialog();
                addPostDialog.show(getSupportFragmentManager(), "AddPostDialog");
            }
        });

        SetDefaultHomeFragmentView();
        SetHomePublicPostsFragment();
        SetHomeFriendsPostsFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null){
            SendUserToOtherActivity(LoginActivity.class);
        }
        else{
            CheckUserExistence();
        }
    }

    private void SetDefaultHomeFragmentView() {
        HomePublicPostsFragment homePublicPostsFragment = new HomePublicPostsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.home_fragment_container, homePublicPostsFragment);
        fragmentTransaction.commit();
    }

    private void SetHomePublicPostsFragment() {
        linear_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_active != 1) {
                    btn_active = 1;
                    HomePublicPostsFragment homePublicPostsFragment = new HomePublicPostsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.home_fragment_container, homePublicPostsFragment);
                    fragmentTransaction.commit();

                    // Change the background
                    linear_public.setBackground(getResources().getDrawable(R.drawable.border_style_5));
                    linear_friends.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                }
            }
        });
    }

    private void SetHomeFriendsPostsFragment() {
        linear_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_active != 2) {
                    btn_active = 2;
                    HomeFriendsPostsFragment homeFriendsPostsFragment = new HomeFriendsPostsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.home_fragment_container, homeFriendsPostsFragment);
                    fragmentTransaction.commit();

                    // Change the background
                    linear_public.setBackground(getResources().getDrawable(R.drawable.border_style_1));
                    linear_friends.setBackground(getResources().getDrawable(R.drawable.border_style_5));
                }
            }
        });
    }

    private void CheckUserExistence() {
        UserRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!snapshot.hasChild("birthdate") ||
                            !snapshot.hasChild("firstname") ||
                            !snapshot.hasChild("lastname") ||
                            !snapshot.hasChild("height") ||
                            !snapshot.hasChild("weight") ||
                            !snapshot.hasChild("gender"))
                    {
                        Toast.makeText(MainActivity.this, "Please Complete Your Profile Setup First.", Toast.LENGTH_LONG).show();
                        SendUserToOtherActivity(MyProfile.class);
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Please Set Up Your Profile First.", Toast.LENGTH_LONG).show();
                    SendUserToOtherActivity(MyProfile.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB User", "Error: " + error.getMessage());
            }
        });
    }

    private void SendUserToOtherActivity(Class<?> className){
        Intent intent = new Intent(MainActivity.this, className);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item){
        switch (item.getItemId()){
            case R.id.nav_notifications:
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                SendUserToOtherActivity(Notifications.class);
                break;
            case R.id.nav_myProfile:
                Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
                SendUserToOtherActivity(MyProfile.class);
                break;
            case R.id.nav_findFriends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                SendUserToOtherActivity(FindFriends.class);
                break;
            case R.id.nav_dailyactivity:
                Toast.makeText(this, "Daily Activity", Toast.LENGTH_SHORT).show();
                SendUserToOtherActivity(DailyActivity_Activity.class);
                break;
            case R.id.nav_foodjournal:
                Toast.makeText(this, "Food Journal", Toast.LENGTH_SHORT).show();
                SendUserToOtherActivity( Food_Journal_Activity.class);
                break;
            case R.id.nav_sleepschedule:
                Toast.makeText(this, "Sleep Schedule", Toast.LENGTH_SHORT).show();
                SendUserToOtherActivity(SleepSchedule.class);
                break;
            case R.id.nav_steptracker:
                Toast.makeText(this, "Step Tracker", Toast.LENGTH_SHORT).show();
                SendUserToOtherActivity(StepTracker.class);
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show();
                //mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                // Sign out the user
                FirebaseAuth.getInstance().signOut();

                // If using Google Sign-In, sign out from Google as well
                GoogleSignIn.getClient(MainActivity.this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                        .signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Redirect the user to the login activity after successful logout
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // Optional: Close the main activity to prevent going back to it after logout
                            }
                        });
                SendUserToOtherActivity(LoginActivity.class);
                break;
        }
    }

}