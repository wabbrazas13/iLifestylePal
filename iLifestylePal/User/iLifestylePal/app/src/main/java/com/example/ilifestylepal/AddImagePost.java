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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddImagePost extends AppCompatActivity {
    private TextInputLayout til_post_type;
    private CircleImageView civ_profile_pic;
    private ImageView iv_image_post;
    private TextView tv_username;
    private AutoCompleteTextView actv_privacy;
    private EditText et_post_description;
    private Button btn_select_image, btn_upload_post;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUserRef, DatabasePostRef;
    private StorageReference StoragePostImageRef;
    private String currentUserID;

    private ActivityResultLauncher<String> mGetContent;
    private Uri resultUri;
    private String downloadUrl;

    private ProgressDialog loadingBar;
    private String savedCurrentDate, savedCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabasePostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        StoragePostImageRef = FirebaseStorage.getInstance().getReference().child("Post Images");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.add_image_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Image Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        til_post_type = findViewById(R.id.til_post_type);
        civ_profile_pic = findViewById(R.id.civ_profile_pic);
        iv_image_post = findViewById(R.id.iv_image_post);
        tv_username = findViewById(R.id.tv_username);
        actv_privacy = findViewById(R.id.actv_privacy);
        et_post_description = findViewById(R.id.et_post_description);
        btn_select_image = findViewById(R.id.btn_select_image);
        btn_upload_post = findViewById(R.id.btn_upload_post);

        String[] privacy = {"Public", "Friends"};
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_gender, privacy);
        actv_privacy.setAdapter(arrayAdapter);

        loadingBar = new ProgressDialog(this);

        btn_upload_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostImageInfo();
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
                Intent intent = new Intent(AddImagePost.this, CropperActivity2.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });

        actv_privacy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SetIconDrawableForTextInputLayout(i);
            }
        });

        FetchCurrentUserInfo();

        // Retrieve the intent
        Intent intent = getIntent();

        // Check if the intent has the desired extra
        if (intent.hasExtra("imageUri")) {
            // Retrieve the image URI from the intent
            String imageUriString = intent.getStringExtra("imageUri");

            // Convert the URI string back to a URI object
            resultUri = Uri.parse(imageUriString);

            // Use the image URI as needed (e.g., load the image into an ImageView)
            iv_image_post.setImageURI(resultUri);
        }
    }

    private void ValidatePostImageInfo() {
        String desc = et_post_description.getText().toString();
        if(resultUri==null){
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(desc)){
            Toast.makeText(this, "Please write something about the image...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Uploading Image Post");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            StoringPostImageToFirebaseStorage();
        }
    }

    private void StoringPostImageToFirebaseStorage() {
        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String postRandomName = "IMG-" + savedCurrentDate + savedCurrentTime;
        StorageReference filePath = StoragePostImageRef.child(postRandomName + currentUserID + resultUri.getLastPathSegment());

        filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StoragePostImageRef.child(postRandomName + currentUserID + resultUri.getLastPathSegment()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadUrl = task.getResult().toString();
                        if(task.isSuccessful()){
                            StoringPostImageInfoToFirebaseDatabase();
                        }else{
                            Toast.makeText(AddImagePost.this, "Error Occurred: ." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void StoringPostImageInfoToFirebaseDatabase() {
        String post_privacy = actv_privacy.getText().toString();
        String post_description = et_post_description.getText().toString();
        String randomKey = DatabasePostRef.push().getKey();

        HashMap PostMap = new HashMap();
        PostMap.put("post_timestamp", ServerValue.TIMESTAMP);
        PostMap.put("post_uid", currentUserID);
        PostMap.put("post_date", savedCurrentDate);
        PostMap.put("post_time", savedCurrentTime);
        PostMap.put("post_privacy", post_privacy);
        PostMap.put("post_type", "image");
        PostMap.put("post_url", downloadUrl);
        PostMap.put("post_description", post_description);

        DatabasePostRef.child(randomKey).updateChildren(PostMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddImagePost.this, "Post Uploaded.", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            SendUserToMainActivity();
                        }else{
                            Toast.makeText(AddImagePost.this, "Error Occurred : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddImagePost.this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1 && requestCode==101){
            String result = data.getStringExtra("RESULT");
            resultUri = null;
            if(result!=null){
                resultUri = Uri.parse(result);
            }
            Picasso.with(AddImagePost.this).load(resultUri).placeholder(R.drawable.add_image).into(iv_image_post);
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
                        Picasso.with(AddImagePost.this).load(picURL).placeholder(R.drawable.ic_profile).into(civ_profile_pic);
                    }
                    tv_username.setText(firstname + " " + lastname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SetIconDrawableForTextInputLayout(int i) {
        if(i == 0){
            til_post_type.setStartIconDrawable(R.drawable.ic_public);
        }
        if(i == 1){
            til_post_type.setStartIconDrawable(R.drawable.ic_friends);
        }
    }
}