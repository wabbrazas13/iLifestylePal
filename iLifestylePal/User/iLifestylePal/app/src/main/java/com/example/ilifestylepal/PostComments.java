package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostComments extends AppCompatActivity {
    private TextView tv_comments_count;
    private EditText et_comment;
    private ImageButton btn_add_comment;
    private RecyclerView rv_comments;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUsersRef, DatabasePostCommentsRef, PostRef;
    private String currentUserID;

    private String post_id;
    private String post_uid = "";

    private ArrayList<CommentsModel> mCommentsList;
    private CommentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        post_id = getIntent().getExtras().get("post_id").toString();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id);
        DatabasePostCommentsRef = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(post_id)
                .child("Comments");

        et_comment = findViewById(R.id.et_comment);
        tv_comments_count = findViewById(R.id.tv_comments_count);
        btn_add_comment = findViewById(R.id.btn_add_comment);

        rv_comments = findViewById(R.id.rv_comments);
        mCommentsList = new ArrayList<>();
        mAdapter = new CommentsAdapter(mCommentsList);
        rv_comments.setAdapter(mAdapter);
        rv_comments.setLayoutManager(new LinearLayoutManager(this));

        DatabasePostCommentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCommentsList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String comment_id = data.getKey();
                    CommentsModel myDataModel = data.getValue(CommentsModel.class);
                    myDataModel.setComment_id(comment_id);
                    myDataModel.setPost_id(post_id);
                    mCommentsList.add(myDataModel);
                }

                Collections.sort(mCommentsList, new Comparator<CommentsModel>() {
                    @Override
                    public int compare(CommentsModel o1, CommentsModel o2) {
                        long ts1 = o1.getComment_timestamp();
                        long ts2 = o2.getComment_timestamp();
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
                Log.e("DB Comments", "Error: " + error.getMessage());
            }
        });

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("post_uid")) {
                        post_uid = snapshot.child("post_uid").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Post", "Error: " + error.getMessage());
            }
        });

        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateComment();
            }
        });

        tv_comments_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(PostComments.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        SetCommentsCount();
    }

    private void SetCommentsCount() {
        DatabasePostCommentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count > 1) {
                    tv_comments_count.setText(count + " comments");
                } else {
                    tv_comments_count.setText(count + " comment");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Comments", "Error: " + error.getMessage());
            }
        });
    }

    private void ValidateComment() {
        String comment = et_comment.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "You have not written any comment.", Toast.LENGTH_SHORT).show();
        } else {
            AddComment();
        }
    }

    private void AddComment() {
        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        String savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String randomKey = DatabasePostCommentsRef.push().getKey();
        String comment_text = et_comment.getText().toString();

        HashMap commentMap = new HashMap();
        commentMap.put("comment_timestamp", ServerValue.TIMESTAMP);
        commentMap.put("comment_uid", currentUserID);
        commentMap.put("comment_text", comment_text);
        commentMap.put("comment_date", savedCurrentDate);
        commentMap.put("comment_time", savedCurrentTime);

        DatabasePostCommentsRef.child(randomKey).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PostComments.this, "Comment Added.", Toast.LENGTH_SHORT).show();
                    et_comment.setText("");
                    if (!post_uid.equals(currentUserID)) {
                        AddNotification(randomKey);
                    }
                } else {
                    Log.e("DB Comments", "Error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void AddNotification(String comment_id) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notif_desc = "commented on your post";

        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        String savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm a");
        String savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String randomKey = notificationRef.push().getKey();

        HashMap notifMap = new HashMap();
        notifMap.put("notif_id", randomKey);
        notifMap.put("notif_category", "Comment");
        notifMap.put("notif_timestamp", ServerValue.TIMESTAMP);
        notifMap.put("notif_date", savedCurrentDate);
        notifMap.put("notif_time", savedCurrentTime);
        notifMap.put("notif_description", notif_desc);
        notifMap.put("notif_uid", post_uid);
        notifMap.put("notif_status", "unread");
        notifMap.put("post_id", post_id);
        notifMap.put("sender_uid", currentUserID);
        notifMap.put("comment_id", comment_id);

        notificationRef.child(randomKey).updateChildren(notifMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.e("DB Notifications", "Notification Added Successfully.");
                } else {
                    Log.e("DB Notifications", "Error: " + task.getException().getMessage());
                }
            }
        });
    }
}