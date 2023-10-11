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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendsRequestsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUserRef;
    private String currentUserID;

    private RecyclerView mRecyclerView;
    private SearchView mSearchField;

    private ArrayList<FriendsModel> mFriendRequests;
    private FindFriendsRequestsAdapter mFriendRequestsAdapter;

    public FindFriendsRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_friends_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendRequests = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_friend_requests);
        mSearchField = view.findViewById(R.id.sv_search);

        mFriendRequestsAdapter = new FindFriendsRequestsAdapter(mFriendRequests);
        mRecyclerView.setAdapter(mFriendRequestsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFriendRequests.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    if (snapshot.hasChild(currentUserID)
                            && snapshot.child(currentUserID).hasChild("Friend Requests")
                            && snapshot.child(currentUserID).child("Friend Requests").hasChild(data.getKey())) {
                        FriendsModel myDataModel = data.getValue(FriendsModel.class);
                        mFriendRequests.add(myDataModel);
                    }
                }

                mFriendRequestsAdapter.notifyDataSetChanged();
                mSearchField.clearFocus();
                mFriendRequestsAdapter.getFilter().filter("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Users", "Error: " + error.getMessage());
            }
        });

        mSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mFriendRequestsAdapter.getFilter().filter(s);
                return true;
            }
        });

        return view;
    }
}