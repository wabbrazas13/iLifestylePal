package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Notifications extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference NotificationRef;
    private String currentUserID;

    private RecyclerView mRecyclerView;

    private ArrayList<NotificationsModel> mNotifList;
    private NotificationsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.notif_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNotifList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_notif);
        mRecyclerView.requestFocus();

        mAdapter = new NotificationsAdapter(mNotifList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        NotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mNotifList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.child("notif_uid").getValue(String.class).equals(currentUserID)) {
                        NotificationsModel myDataModel = data.getValue(NotificationsModel.class);
                        mNotifList.add(myDataModel);
                    }
                }

                Collections.sort(mNotifList, new Comparator<NotificationsModel>() {
                    @Override
                    public int compare(NotificationsModel o1, NotificationsModel o2) {
                        long ts1 = o1.getNotif_timestamp();
                        long ts2 = o2.getNotif_timestamp();
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
                Log.e("Notifications", "Error: " + error.getMessage());
            }
        });
    }
}