package com.example.ilifestylepal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HomePublicPostsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUsersRef, DatabasePostsRef;
    private String currentUserID;

    private RecyclerView mRecyclerView;
    private SearchView mSearchField;

    private ArrayList<PostsModel> mPostsList;
    private HomePostsAdapter mAdapter;

    public HomePublicPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_public_posts, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabasePostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mPostsList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_public_posts);
        mSearchField = view.findViewById(R.id.sv_search);
        mSearchField.clearFocus();
        mRecyclerView.requestFocus();

        mAdapter = new HomePostsAdapter(mPostsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabasePostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String post_id = data.getKey();
                    if (data.child("post_privacy").getValue(String.class).equals("Friends")) {
                        continue;
                    }
                    PostsModel myDataModel = data.getValue(PostsModel.class);
                    myDataModel.setPost_id(post_id);
                    mPostsList.add(myDataModel);
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

        mSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return true;
            }
        });

        return view;
    }
}