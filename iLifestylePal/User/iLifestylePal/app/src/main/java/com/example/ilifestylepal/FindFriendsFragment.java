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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUserRef;
    private String currentUserID;

    private RecyclerView mRecyclerView;
    private SearchView mSearchField;

    private ArrayList<FriendsModel> mDataList;
    private FindFriendsAdapter mAdapter;

    public FindFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mDataList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_find_friends);
        mSearchField = view.findViewById(R.id.sv_search);

        mAdapter = new FindFriendsAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDataList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals(currentUserID) ||
                            (snapshot.child(currentUserID).hasChild("Friend Requests") &&
                                    snapshot.child(currentUserID).child("Friend Requests").hasChild(data.getKey())) ||
                            (snapshot.child(currentUserID).hasChild("Friends") &&
                                    snapshot.child(currentUserID).child("Friends").hasChild(data.getKey()))) {
                        continue;
                    }

                    FriendsModel myDataModel = data.getValue(FriendsModel.class);
                    mDataList.add(myDataModel);
                }

                mAdapter.notifyDataSetChanged();
                mSearchField.clearFocus();
                mAdapter.getFilter().filter("");
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
                mAdapter.getFilter().filter(s);
                return true;
            }
        });
        
        return view;
    }
}