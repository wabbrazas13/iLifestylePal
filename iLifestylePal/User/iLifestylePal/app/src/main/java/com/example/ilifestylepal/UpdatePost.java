package com.example.ilifestylepal;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdatePost extends AppCompatActivity {

    private TextInputLayout til_post_type;
    private CircleImageView civ_profile_pic;
    private ImageView iv_image_post;
    private VideoView vv_video_post;
    private TextView tv_username;
    private AutoCompleteTextView actv_privacy;
    private EditText et_post_description;
    private Button btn_select_image, btn_select_video, btn_update_post;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUserRef, DatabasePostRef;
    private StorageReference StoragePostImageRef, StoragePostVideoRef;
    private String currentUserID;

    private ActivityResultLauncher<String> mGetContent;
    private Uri imageUri;
    private Uri videoUri;
    private String downloadUrl;
    private int changeUri = 1;

    private ProgressDialog loadingBar;
    private String savedCurrentDate, savedCurrentTime;

    private String post_id;
    private String post_url;
    private String post_description;
    private String post_privacy;

    private ArrayAdapter arrayPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        post_id = getIntent().getExtras().get("post_id").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabasePostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        StoragePostImageRef = FirebaseStorage.getInstance().getReference().child("Post Images");
        StoragePostVideoRef = FirebaseStorage.getInstance().getReference().child("Post Videos");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.update_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        til_post_type = findViewById(R.id.til_post_type);
        civ_profile_pic = findViewById(R.id.civ_profile_pic);
        iv_image_post = findViewById(R.id.iv_image_post);
        vv_video_post = findViewById(R.id.vv_video_post);
        tv_username = findViewById(R.id.tv_username);
        actv_privacy = findViewById(R.id.actv_privacy);
        et_post_description = findViewById(R.id.et_post_description);
        btn_select_image = findViewById(R.id.btn_select_image);
        btn_select_video = findViewById(R.id.btn_select_video);
        btn_update_post = findViewById(R.id.btn_update_post);

        loadingBar = new ProgressDialog(this);

        String[] privacy = {"Public", "Friends"};
        arrayPrivacy = new ArrayAdapter<String>(this, R.layout.list_gender, privacy);
        actv_privacy.setAdapter(arrayPrivacy);

        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        actv_privacy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SetIconDrawableForTextInputLayout(i);
            }
        });

        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(UpdatePost.this, CropperActivity2.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
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

        btn_update_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostChanges();
            }
        });

        FetchUserData();
        FetchPostData();
    }

    private void ValidatePostChanges() {
        String privacy = actv_privacy.getText().toString();
        String description = et_post_description.getText().toString();

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please write something about your post...", Toast.LENGTH_SHORT).show();
        } else if (privacy.equals(post_privacy) && description.equals(post_description) && changeUri == 1) {
            Toast.makeText(this, "Please change something to update post...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Updating Post");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            if (changeUri == 2) {
                UploadNewImageFile();
            } else if (changeUri == 3) {
                UploadNewVideoFile();
            } else {
                UpdatePostDetails();
            }
        }
    }

    private void UpdatePostDetails() {
        DatabasePostRef.child(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("post_date")) {
                    DatabasePostRef.child(post_id).child("post_date").setValue(savedCurrentDate);
                }
                if (snapshot.hasChild("post_time")) {
                    DatabasePostRef.child(post_id).child("post_time").setValue(savedCurrentTime);
                }
                if (snapshot.hasChild("post_timestamp")) {
                    DatabasePostRef.child(post_id).child("post_timestamp").setValue(ServerValue.TIMESTAMP);
                }
                if (snapshot.hasChild("post_description")) {
                    DatabasePostRef.child(post_id).child("post_description").setValue(et_post_description.getText().toString());
                }
                if (snapshot.hasChild("post_privacy")) {
                    DatabasePostRef.child(post_id).child("post_privacy").setValue(actv_privacy.getText().toString());
                }
                if (snapshot.hasChild("post_type")) {
                    if (changeUri == 2) {
                        DatabasePostRef.child(post_id).child("post_type").setValue("image");
                    }
                    if (changeUri == 3) {
                        DatabasePostRef.child(post_id).child("post_type").setValue("video");
                    }
                }
                if (snapshot.hasChild("post_url")) {
                    DatabasePostRef.child(post_id).child("post_url").setValue(downloadUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Posts", "Error: " + error.getMessage());
            }
        });

        loadingBar.dismiss();
        Toast.makeText(this, "Post Updated.", Toast.LENGTH_SHORT).show();
        Intent main = new Intent(UpdatePost.this, MainActivity.class);
        startActivity(main);
    }

    private void UploadNewVideoFile() {
        String postRandomName = "VID-" + savedCurrentDate + savedCurrentTime;
        StorageReference filePath = StoragePostVideoRef.child(postRandomName + currentUserID + videoUri.getLastPathSegment() + ".mp4");
        UploadTask uploadTask = filePath.putFile(videoUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StoragePostVideoRef.child(postRandomName + currentUserID + videoUri.getLastPathSegment() + ".mp4").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadUrl = task.getResult().toString();
                        if(task.isSuccessful()){
                            DeletePreviousFile();
                        }else{
                            Toast.makeText(UpdatePost.this, "Error Occurred: ." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void UploadNewImageFile() {
        String postRandomName = "IMG-" + savedCurrentDate + savedCurrentTime;
        StorageReference filePath = StoragePostImageRef.child(postRandomName + currentUserID + imageUri.getLastPathSegment());

        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StoragePostImageRef.child(postRandomName + currentUserID + imageUri.getLastPathSegment()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadUrl = task.getResult().toString();
                        if(task.isSuccessful()){
                            DeletePreviousFile();
                        }else{
                            Toast.makeText(UpdatePost.this, "Error Occurred: ." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void DeletePreviousFile() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference OldStoragePostRef = firebaseStorage.getReferenceFromUrl(post_url);

        OldStoragePostRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                UpdatePostDetails();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdatePost.this, "Error Occurred: ." + e.getMessage(), Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==-1 && requestCode==101){
            String result = data.getStringExtra("RESULT");
            imageUri = null;
            if(result!=null){
                imageUri = Uri.parse(result);
            }
            iv_image_post.setVisibility(View.VISIBLE);
            vv_video_post.setVisibility(View.GONE);
            Picasso.with(UpdatePost.this).load(imageUri).placeholder(R.drawable.add_image).into(iv_image_post);
            changeUri = 2;
        }

        if (requestCode == 13 && resultCode == RESULT_OK && data != null) {
            iv_image_post.setVisibility(View.GONE);
            vv_video_post.setVisibility(View.VISIBLE);
            videoUri = data.getData();
            vv_video_post.setVideoURI(videoUri);
            vv_video_post.start();
            changeUri = 3;
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

    private void FetchUserData() {
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
                        Picasso.with(UpdatePost.this).load(picURL).placeholder(R.drawable.ic_profile).into(civ_profile_pic);
                    }
                    tv_username.setText(firstname + " " + lastname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Users", "Error: " + error.getMessage());
            }
        });
    }

    private void FetchPostData() {
        DatabasePostRef.child(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("post_privacy")) {
                        String privacy = snapshot.child("post_privacy").getValue().toString();
                        post_privacy = privacy;

                        if (privacy.equals("Public")) {
                            actv_privacy.setText("Public", false);
                            til_post_type.setStartIconDrawable(R.drawable.ic_public);
                        } else {
                            actv_privacy.setText("Friends", false);
                            til_post_type.setStartIconDrawable(R.drawable.ic_friends);
                        }
                    }
                    if (snapshot.hasChild("post_description")) {
                        String description = snapshot.child("post_description").getValue().toString();
                        post_description = description;
                        et_post_description.setText(description);
                    }
                    if (snapshot.hasChild("post_url")) {
                        String url = snapshot.child("post_url").getValue().toString();
                        post_url = url;
                        downloadUrl = url;

                        if (snapshot.hasChild("post_type")) {
                            String type = snapshot.child("post_type").getValue().toString();
                            if (type.equals("image")) {
                                iv_image_post.setVisibility(View.VISIBLE);
                                vv_video_post.setVisibility(View.GONE);
                                Picasso.with(UpdatePost.this).load(url).placeholder(R.drawable.add_image).into(iv_image_post);
                            }
                            if (type.equals("video")) {
                                iv_image_post.setVisibility(View.GONE);
                                vv_video_post.setVisibility(View.VISIBLE);

                                MediaController mediaController = new MediaController(UpdatePost.this);
                                mediaController.setAnchorView(vv_video_post);
                                vv_video_post.setMediaController(mediaController);
                                vv_video_post.setVideoURI(Uri.parse(url));
                                vv_video_post.start();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Posts", "Error: " + error.getMessage());
            }
        });
    }
}