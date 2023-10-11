package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends AppCompatActivity {

    private CircleImageView civ_profile_pic;
    private TextView tv_username;
    private TextView tv_email;
    private TextView tv_posts_count;
    private TextView tv_likes_count;
    private TextView tv_friends_count;

    private CircleImageView civ_profile_pic2;
    private TextView tv_username2;
    private ImageView iv_post_image;

    private LinearLayout btn_add_friend;
    private LinearLayout btn_unfriend;
    private LinearLayout btn_accept_request;
    private LinearLayout btn_cancel_request;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUsersRef, CurrentUserRef;
    private DatabaseReference DatabasePostsRef;
    private String currentUserID;

    private RecyclerView mRecyclerView;
    private ArrayList<PostsModel> mPostsList;
    private HomePostsAdapter mAdapter;

    private String uid = "";
    private String username = "";
    private Boolean unfriend = false, accept = false, cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        uid = getIntent().getExtras().get("uid").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        CurrentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        DatabasePostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.view_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("View Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        civ_profile_pic = findViewById(R.id.civ_profile_pic);
        tv_username = findViewById(R.id.tv_username);
        tv_email = findViewById(R.id.tv_email);
        tv_posts_count = findViewById(R.id.tv_posts_count);
        tv_likes_count = findViewById(R.id.tv_likes_count);
        tv_friends_count = findViewById(R.id.tv_friends_count);

        civ_profile_pic2 = findViewById(R.id.civ_profile_pic2);
        tv_username2 = findViewById(R.id.tv_username2);
        iv_post_image = findViewById(R.id.iv_post_image);

        btn_add_friend = findViewById(R.id.btn_add_friend);
        btn_unfriend = findViewById(R.id.btn_unfriend);
        btn_accept_request = findViewById(R.id.btn_accept_request);
        btn_cancel_request = findViewById(R.id.btn_cancel_request);
        btn_add_friend.setVisibility(View.GONE);
        btn_unfriend.setVisibility(View.GONE);
        btn_accept_request.setVisibility(View.GONE);
        btn_cancel_request.setVisibility(View.GONE);

        mPostsList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.view_post_list);
        mRecyclerView.requestFocus();

        mAdapter = new HomePostsAdapter(mPostsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFriend();
            }
        });

        btn_unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UnFriend();
            }
        });

        btn_accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AcceptRequest();
            }
        });

        btn_cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelRequest();
            }
        });

        DatabasePostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String post_id = data.getKey();
                    String post_uid = data.child("post_uid").getValue(String.class);

                    if (post_uid.equals(uid)) {
                        PostsModel myDataModel = data.getValue(PostsModel.class);
                        myDataModel.setPost_id(post_id);
                        mPostsList.add(myDataModel);
                    }
                }

                Collections.sort(mPostsList, new Comparator<PostsModel>() {
                    @Override
                    public int compare(PostsModel o1, PostsModel o2) {
                        long ts1 = o1.getPost_timestamp();
                        long ts2 = o2.getPost_timestamp();
                        Date d1 = new Date(ts1);
                        Date d2 = new Date(ts2);
                        Timestamp t1 = new Timestamp(d1);
                        Timestamp t2 = new Timestamp(d2);
                        return t2.compareTo(t1); // Sort in descending order
                    }
                });

                mAdapter.notifyDataSetChanged();
                mAdapter.getFilter().filter("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Posts", "Error: " + error.getMessage());
            }
        });

        FetchUserInfo();
        FetchFriendsCount();
        FetchPostsAndLikesCount();
        CheckFriendStatus();
    }

    private void AddFriend() {
        DatabaseUsersRef.child("Friend Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(currentUserID)) {
                    DatabaseUsersRef.child("Friend Requests").child(currentUserID).setValue(true);
                    AddFriendRequestNotification(uid, currentUserID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friend Requests", "Error: " + error.getMessage());
            }
        });
    }

    private void CancelRequest() {
        DatabaseUsersRef.child("Friend Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUserID)) {
                    DatabaseUsersRef.child("Friend Requests").child(currentUserID).removeValue();
                    RemoveFriendRequestNotification(uid, currentUserID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friend Requests", "Error: " + error.getMessage());
            }
        });
    }

    private void AcceptRequest() {
        CurrentUserRef.child("Friend Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
                    CurrentUserRef.child("Friend Requests").child(uid).removeValue();
                    RemoveFriendRequestNotification(currentUserID, uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friend Requests", "Error: " + error.getMessage());
            }
        });
        CurrentUserRef.child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(uid)) {
                    CurrentUserRef.child("Friends").child(uid).setValue(true);
                    AddFriendNotification(currentUserID, uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friends", "Error: " + error.getMessage());
            }
        });
        DatabaseUsersRef.child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(currentUserID)) {
                    DatabaseUsersRef.child("Friends").child(currentUserID).setValue(true);
                    AddFriendNotification(uid, currentUserID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friends", "Error: " + error.getMessage());
            }
        });
    }

    private void UnFriend() {
        CurrentUserRef.child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild(uid)) {
                        CurrentUserRef.child("Friends").child(uid).removeValue();
                        RemoveFriendNotification(currentUserID, uid);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friends", "Error: " + error.getMessage());
            }
        });
        DatabaseUsersRef.child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild(currentUserID)) {
                        DatabaseUsersRef.child("Friends").child(currentUserID).removeValue();
                        RemoveFriendNotification(uid, currentUserID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friends", "Error: " + error.getMessage());
            }
        });
    }

    private void AddFriendRequestNotification(String uid, String currentUserID) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notif_desc = "has sent you friend request";

        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        String savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm a");
        String savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String randomKey = notifRef.push().getKey();

        HashMap notifMap = new HashMap();
        notifMap.put("notif_id", randomKey);
        notifMap.put("notif_category", "Friend Request");
        notifMap.put("notif_timestamp", ServerValue.TIMESTAMP);
        notifMap.put("notif_date", savedCurrentDate);
        notifMap.put("notif_time", savedCurrentTime);
        notifMap.put("notif_description", notif_desc);
        notifMap.put("notif_uid", uid);
        notifMap.put("notif_status", "unread");
        notifMap.put("sender_uid", currentUserID);

        notifRef.child(randomKey).updateChildren(notifMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.e("Notifications", "Notification Added Successfully.");
                } else {
                    Log.e("Notifications", "Error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void RemoveFriendRequestNotification(String uid, String currentUserID) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.hasChild("notif_category") &&
                            childSnapshot.child("notif_category").getValue().toString().equals("Friend Request") &&
                            childSnapshot.hasChild("notif_uid") &&
                            childSnapshot.hasChild("sender_uid") &&
                            childSnapshot.child("notif_uid").getValue().toString().equals(uid) &&
                            childSnapshot.child("sender_uid").getValue().toString().equals(currentUserID)) {
                        childSnapshot.getRef().removeValue(); // remove the child
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Notification", "Error: " + error.getMessage());
            }
        });
    }

    private void AddFriendNotification(String uid, String currentUserID) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notif_desc = "You are now friend with";

        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        String savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm a");
        String savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String randomKey = notifRef.push().getKey();

        HashMap notifMap = new HashMap();
        notifMap.put("notif_id", randomKey);
        notifMap.put("notif_category", "Friend");
        notifMap.put("notif_timestamp", ServerValue.TIMESTAMP);
        notifMap.put("notif_date", savedCurrentDate);
        notifMap.put("notif_time", savedCurrentTime);
        notifMap.put("notif_description", notif_desc);
        notifMap.put("notif_uid", uid);
        notifMap.put("notif_status", "unread");
        notifMap.put("sender_uid", currentUserID);

        notifRef.child(randomKey).updateChildren(notifMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.e("Notifications", "Notification Added Successfully.");
                } else {
                    Log.e("Notifications", "Error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void RemoveFriendNotification(String currentUserID, String uid) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.hasChild("notif_category") &&
                            childSnapshot.child("notif_category").getValue().toString().equals("Friend") &&
                            childSnapshot.hasChild("notif_uid") &&
                            childSnapshot.hasChild("sender_uid") &&
                            childSnapshot.child("notif_uid").getValue().toString().equals(uid) &&
                            childSnapshot.child("sender_uid").getValue().toString().equals(currentUserID)) {
                        childSnapshot.getRef().removeValue(); // remove the child
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Notification", "Error: " + error.getMessage());
            }
        });
    }

    private void ShowOrHideAddFriendButton() {
        if (unfriend == true) {
            btn_add_friend.setVisibility(View.GONE);
            btn_unfriend.setVisibility(View.VISIBLE);
            btn_accept_request.setVisibility(View.GONE);
            btn_cancel_request.setVisibility(View.GONE);
        } else if (accept == true) {
            btn_add_friend.setVisibility(View.GONE);
            btn_unfriend.setVisibility(View.GONE);
            btn_accept_request.setVisibility(View.VISIBLE);
            btn_cancel_request.setVisibility(View.GONE);
        } else if (cancel == true) {
            btn_add_friend.setVisibility(View.GONE);
            btn_unfriend.setVisibility(View.GONE);
            btn_accept_request.setVisibility(View.GONE);
            btn_cancel_request.setVisibility(View.VISIBLE);
        } else {
            btn_add_friend.setVisibility(View.VISIBLE);
            btn_unfriend.setVisibility(View.GONE);
            btn_accept_request.setVisibility(View.GONE);
            btn_cancel_request.setVisibility(View.GONE);
        }
    }

    private void CheckFriendStatus() {
        CurrentUserRef.child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
                    unfriend = true;
                    accept = false;
                    cancel = false;
                } else {
                    unfriend = false;
                }
                ShowOrHideAddFriendButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friends", "Error: " + error.getMessage());
            }
        });
        CurrentUserRef.child("Friend Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
                    accept = true;
                    cancel = false;
                    unfriend = false;
                } else {
                    accept = false;
                }
                ShowOrHideAddFriendButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friend Requests", "Error: " + error.getMessage());
            }
        });
        DatabaseUsersRef.child("Friend Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUserID)) {
                    cancel = true;
                    accept = false;
                    unfriend = false;
                } else {
                    cancel = false;
                }
                ShowOrHideAddFriendButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friends", "Error: " + error.getMessage());
            }
        });
    }

    private void FetchUserInfo() {
        DatabaseUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fn = "Firstname", ln = "Lastname";
                    if (snapshot.hasChild("pic_url")) {
                        String picURL = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(ViewProfile.this).load(picURL).placeholder(R.drawable.ic_profile).into(civ_profile_pic);
                        Picasso.with(ViewProfile.this).load(picURL).placeholder(R.drawable.ic_profile).into(civ_profile_pic2);
                        Picasso.with(ViewProfile.this).load(picURL).placeholder(R.drawable.ic_profile).into(iv_post_image);
                    }
                    if (snapshot.hasChild("firstname")) {
                        fn = snapshot.child("firstname").getValue().toString();
                    }
                    if (snapshot.hasChild("lastname")) {
                        ln = snapshot.child("lastname").getValue().toString();
                    }
                    if (snapshot.hasChild("email")) {
                        String email = snapshot.child("email").getValue().toString();
                        tv_email.setText(email);
                    }
                    tv_username.setText(fn + " " + ln);
                    tv_username2.setText(fn + " " + ln);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        CurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fn = "", ln = "";
                    if (snapshot.hasChild("firstname")) {
                        fn = snapshot.child("firstname").getValue().toString();
                    }
                    if (snapshot.hasChild("lastname")) {
                        ln = snapshot.child("lastname").getValue().toString();
                    }
                    username = fn + " " + ln;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchFriendsCount() {
        DatabaseUsersRef.child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalFriends = (int) snapshot.getChildrenCount();
                tv_friends_count.setText(String.valueOf(totalFriends));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friends", "Error: " + error.getMessage());
            }
        });
    }

    private void FetchPostsAndLikesCount() {
        DatabasePostsRef.orderByChild("post_uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalPosts = (int) snapshot.getChildrenCount();
                int totalLikes = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    int likesCount = (int) postSnapshot.child("Likes").getChildrenCount();
                    totalLikes += likesCount;
                }
                tv_posts_count.setText(String.valueOf(totalPosts));
                tv_likes_count.setText(String.valueOf(totalLikes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Posts", "Error: " + error.getMessage());
            }
        });
    }
}