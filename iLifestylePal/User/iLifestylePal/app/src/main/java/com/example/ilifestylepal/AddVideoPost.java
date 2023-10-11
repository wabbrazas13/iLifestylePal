package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddVideoPost extends AppCompatActivity {

    private TextInputLayout til_post_type;
    private CircleImageView civ_profile_pic;
    private VideoView vv_video_post;
    private TextView tv_username;
    private AutoCompleteTextView actv_privacy;
    private EditText et_post_description;
    private Button btn_select_video, btn_upload_post;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUserRef, DatabasePostRef;
    private StorageReference StoragePostVideoRef;
    private String currentUserID;

    private Uri videoUri;
    private String downloadUrl;

    private ProgressDialog loadingBar;
    private String savedCurrentDate, savedCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabasePostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        StoragePostVideoRef = FirebaseStorage.getInstance().getReference().child("Post Videos");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.add_video_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Video Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        til_post_type = findViewById(R.id.til_post_type);
        civ_profile_pic = findViewById(R.id.civ_profile_pic);
        vv_video_post = findViewById(R.id.vv_video_post);
        tv_username = findViewById(R.id.tv_username);
        actv_privacy = findViewById(R.id.actv_privacy);
        et_post_description = findViewById(R.id.et_post_description);
        btn_select_video = findViewById(R.id.btn_select_video);
        btn_upload_post = findViewById(R.id.btn_upload_post);

        loadingBar = new ProgressDialog(this);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(vv_video_post);
        vv_video_post.setMediaController(mediaController);

        String[] privacy = {"Public", "Friends"};
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_gender, privacy);
        actv_privacy.setAdapter(arrayAdapter);

        btn_upload_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostVideoInfo();
            }
        });

        actv_privacy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SetIconDrawableForTextInputLayout(i);
            }
        });

        btn_select_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 13);
            }
        });

        FetchCurrentUserInfo();
    }

    private void ValidatePostVideoInfo() {
        String desc = et_post_description.getText().toString();
        if(videoUri==null){
            Toast.makeText(this, "Please select post video...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(desc)){
            Toast.makeText(this, "Please write something about the video...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Uploading Video Post");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            StoringPostVideoToFirebaseStorage();
        }
    }

    private void StoringPostVideoToFirebaseStorage() {
        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String postRandomName = "VID-" + savedCurrentDate + savedCurrentTime;
        StorageReference filePath = StoragePostVideoRef.child(postRandomName + currentUserID + videoUri.getLastPathSegment() + ".mp4");

        UploadTask uploadTask = filePath.putFile(videoUri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                loadingBar.setMessage(String.format("%.0f",progress) + "% Please wait...");
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StoragePostVideoRef.child(postRandomName + currentUserID + videoUri.getLastPathSegment() + ".mp4").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadUrl = task.getResult().toString();
                        if(task.isSuccessful()){
                            StoringPostVideoInfoToFirebaseDatabase();
                        }else{
                            Toast.makeText(AddVideoPost.this, "Error Occurred: ." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void StoringPostVideoInfoToFirebaseDatabase() {
        String post_privacy = actv_privacy.getText().toString();
        String post_description = et_post_description.getText().toString();
        String randomKey = DatabasePostRef.push().getKey();

        HashMap PostMap = new HashMap();
        PostMap.put("post_timestamp", ServerValue.TIMESTAMP);
        PostMap.put("post_uid", currentUserID);
        PostMap.put("post_date", savedCurrentDate);
        PostMap.put("post_time", savedCurrentTime);
        PostMap.put("post_privacy", post_privacy);
        PostMap.put("post_type", "video");
        PostMap.put("post_url", downloadUrl);
        PostMap.put("post_description", post_description);

        DatabasePostRef.child(randomKey).updateChildren(PostMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddVideoPost.this, "Post Uploaded.", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            SendUserToMainActivity();
                        }else{
                            Toast.makeText(AddVideoPost.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddVideoPost.this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 13 && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            vv_video_post.setVideoURI(videoUri);
            vv_video_post.start();
        }
    }

    private void SetIconDrawableForTextInputLayout(int i) {
        if(i == 0){
            til_post_type.setStartIconDrawable(R.drawable.ic_public);
        }
        if(i == 1){
            til_post_type.setStartIconDrawable(R.drawable.ic_friends);
        }
    }

    private void FetchCurrentUserInfo() {
        DatabaseUserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String firstname = "", lastname = "";
                    if(snapshot.hasChild("firstname")) {
                        firstname = snapshot.child("firstname").getValue().toString();
                    }
                    if(snapshot.hasChild("lastname")) {
                        lastname = snapshot.child("lastname").getValue().toString();
                    }
                    if(snapshot.hasChild("pic_url")) {
                        String picURL = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(AddVideoPost.this).load(picURL).placeholder(R.drawable.ic_profile).into(civ_profile_pic);
                    }
                    tv_username.setText(firstname + " " + lastname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}