package com.example.ilifestylepal;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private CircleImageView civ_profile_pic;
    private TextView tv_username;
    private TextView tv_email;
    private TextView tv_posts_count;
    private TextView tv_likes_count;
    private TextView tv_friends_count;
    private LinearLayout ll_account;
    private LinearLayout ll_posts;
    private TextView tv_account;
    private TextView tv_posts;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUsersRef;
    private DatabaseReference DatabasePostsRef;
    private StorageReference StorageProfileImagesRef;
    private String currentUserID;

    private ActivityResultLauncher<String> mGetContent;
    private Uri resultUri;
    private String downloadUrl;
    private ProgressDialog loadingBar;

    private String currentFragment = "Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        DatabasePostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        StorageProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        civ_profile_pic = findViewById(R.id.civ_profile_pic);
        tv_username = findViewById(R.id.tv_username);
        tv_email = findViewById(R.id.tv_email);
        tv_posts_count = findViewById(R.id.tv_posts_count);
        tv_likes_count = findViewById(R.id.tv_likes_count);
        tv_friends_count = findViewById(R.id.tv_friends_count);
        ll_account = findViewById(R.id.ll_account);
        ll_posts = findViewById(R.id.ll_posts);
        tv_account = findViewById(R.id.tv_account);
        tv_posts = findViewById(R.id.tv_posts);

        loadingBar = new ProgressDialog(this);

        ll_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentFragment.equals("Account")) {
                    currentFragment = "Account";
                    ll_account.setBackgroundResource(R.drawable.border_style_2);
                    tv_account.setTextColor(getResources().getColor(R.color.black));
                    Drawable ic_account = getResources().getDrawable(R.drawable.ic_account);
                    tv_account.setCompoundDrawablesWithIntrinsicBounds(ic_account, null, null, null);
                    ll_posts.setBackgroundResource(R.drawable.border_style_3);
                    tv_posts.setTextColor(getResources().getColor(R.color.gray));
                    Drawable ic_posts = getResources().getDrawable(R.drawable.ic_posts2);
                    tv_posts.setCompoundDrawablesWithIntrinsicBounds(ic_posts, null, null, null);

                    MyProfile_AccountFragment myProfile_accountFragment = new MyProfile_AccountFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, myProfile_accountFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        ll_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentFragment.equals("Posts")) {
                    currentFragment = "Posts";
                    ll_posts.setBackgroundResource(R.drawable.border_style_2);
                    tv_posts.setTextColor(getResources().getColor(R.color.black));
                    Drawable ic_posts = getResources().getDrawable(R.drawable.ic_posts);
                    tv_posts.setCompoundDrawablesWithIntrinsicBounds(ic_posts, null, null, null);
                    ll_account.setBackgroundResource(R.drawable.border_style_3);
                    tv_account.setTextColor(getResources().getColor(R.color.gray));
                    Drawable ic_account = getResources().getDrawable(R.drawable.ic_account2);
                    tv_account.setCompoundDrawablesWithIntrinsicBounds(ic_account, null, null, null);

                    MyProfile_PostsFragment myProfile_postsFragment = new MyProfile_PostsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, myProfile_postsFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        FetchUserInfo();
        FetchFriendsCount();
        FetchPostsAndLikesCount();
        ChangeProfilePicture();
        SetDefaultFragmentView();
    }

    private void FetchUserInfo() {
        tv_email.setText(mAuth.getCurrentUser().getEmail());
        DatabaseUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fn = "Firstname", ln = "Lastname";
                    if (snapshot.hasChild("pic_url")) {
                        String picURL = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(MyProfile.this).load(picURL).placeholder(R.drawable.ic_profile).into(civ_profile_pic);
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
                    } else {
                        DatabaseUsersRef.child("email").setValue(mAuth.getCurrentUser().getEmail());
                    }
                    if (!snapshot.hasChild("uid")) {
                        DatabaseUsersRef.child("uid").setValue(currentUserID);
                    }
                    if (!snapshot.hasChild("gender")) {
                        DatabaseUsersRef.child("gender").setValue("Male");
                    }
                    if (!snapshot.hasChild("height")) {
                        DatabaseUsersRef.child("height").setValue(0);
                    }
                    if (!snapshot.hasChild("weight")) {
                        DatabaseUsersRef.child("weight").setValue(0);
                    }
                    if (!snapshot.hasChild("bmi")) {
                        DatabaseUsersRef.child("bmi").setValue(0);
                    }
                    tv_username.setText(fn + " " + ln);
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
        DatabasePostsRef.orderByChild("post_uid").equalTo(currentUserID).addValueEventListener(new ValueEventListener() {
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

    private void ChangeProfilePicture() {
        civ_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
                builder.setTitle("Profile Picture");
                builder.setMessage("Do you want to continue editing your profile picture?");
                builder.setCancelable(false);

                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mGetContent.launch("image/*");
                        dialogInterface.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(MyProfile.this, CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });
    }

    private void SetDefaultFragmentView() {
        MyProfile_AccountFragment myProfile_accountFragment = new MyProfile_AccountFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, myProfile_accountFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==-1 && requestCode==101){
            String result = data.getStringExtra("RESULT");
            resultUri = null;
            if(result!=null){
                resultUri = Uri.parse(result);

                StorageReference filePath = StorageProfileImagesRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageProfileImagesRef.child(currentUserID + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                downloadUrl = task.getResult().toString();

                                DatabaseReference UsersRef = DatabaseUsersRef;
                                ValueEventListener UsersRefListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            UsersRef.child("pic_url").setValue(downloadUrl);
                                        }
                                        UsersRef.removeEventListener(this);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("DB Users", "Error: " + error.getMessage());
                                    }
                                };
                                UsersRef.addValueEventListener(UsersRefListener);
                                Toast.makeText(MyProfile.this, "Profile Picture Updated", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyProfile.this, "Unable to Update Profile Picture", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double pr = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        loadingBar.setTitle("Changing Profile Picture");
                        loadingBar.setMessage((int)pr + "%  " + "Please wait... " );
                        loadingBar.show();
                        loadingBar.setCanceledOnTouchOutside(false);
                    }
                });
            }
            Picasso.with(MyProfile.this).load(resultUri).placeholder(R.drawable.ic_profile).into(civ_profile_pic);
        }
    }
}