package com.example.ilifestylepal;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsFriendsAdapter extends RecyclerView.Adapter<FindFriendsFriendsAdapter.MyViewHolder> implements Filterable {

    ArrayList<FriendsModel> mFriendsList;
    ArrayList<FriendsModel> mFriendsListFull;
    private Boolean FriendsChecker = false;

    public FindFriendsFriendsAdapter(ArrayList<FriendsModel> mFriendsList) {
        this.mFriendsListFull = mFriendsList;
        this.mFriendsList = new ArrayList<>(mFriendsListFull);
    }

    @NonNull
    @Override
    public FindFriendsFriendsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_friends_layout, parent, false);
        return new FindFriendsFriendsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendsFriendsAdapter.MyViewHolder holder, int position) {
        FirebaseAuth mAuth;
        DatabaseReference DatabaseUserRef;
        String currentUserID;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsChecker = true;

        FriendsModel model = mFriendsList.get(position);
        holder.tv_username.setText(String.valueOf(model.getFullname()));
        holder.tv_email.setText(String.valueOf(model.getEmail()));

        Picasso.with(holder.itemView.getContext())
                .load(model.getPic_url())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.civ_profile_pic);

        holder.btn_2.setText("Unfriend");
        holder.btn_2.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.add_friend));

        holder.btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference friendsRef = DatabaseUserRef.child(currentUserID);
                ValueEventListener friendsRefListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (FriendsChecker == true) {
                            friendsRef.child("Friends").child(model.getUid()).removeValue();
                            RemoveFriendNotification(currentUserID, model.getUid());
                            FriendsChecker = false;
                            friendsRef.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DB Friends", "Error: " + error.getMessage());
                    }
                };
                friendsRef.addValueEventListener(friendsRefListener);

                DatabaseUserRef.child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DatabaseUserRef.child(model.getUid()).child("Friends").child(currentUserID).removeValue();
                        RemoveFriendNotification(model.getUid(), currentUserID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DB Friends", "Error: " + error.getMessage());
                    }
                });
            }
        });

        holder.btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent view_profile = new Intent(holder.itemView.getContext(), ViewProfile.class);
                view_profile.putExtra("uid", model.getUid());
                holder.itemView.getContext().startActivity(view_profile);
            }
        });

        holder.civ_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent view_profile = new Intent(holder.itemView.getContext(), ViewProfile.class);
                view_profile.putExtra("uid", model.getUid());
                holder.itemView.getContext().startActivity(view_profile);
            }
        });

        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent view_profile = new Intent(holder.itemView.getContext(), ViewProfile.class);
                view_profile.putExtra("uid", model.getUid());
                holder.itemView.getContext().startActivity(view_profile);
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
                            childSnapshot.child("notif_uid").getValue().toString().equals(currentUserID) &&
                            childSnapshot.child("sender_uid").getValue().toString().equals(uid)) {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civ_profile_pic;
        TextView tv_username;
        TextView tv_email;
        Button btn_1;
        Button btn_2;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civ_profile_pic = itemView.findViewById(R.id.civ_profile_pic);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_email = itemView.findViewById(R.id.tv_email);
            btn_1 = itemView.findViewById(R.id.btn_1);
            btn_2 = itemView.findViewById(R.id.btn_2);
        }
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FriendsModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mFriendsListFull);
            } else {
                // Filter the data based on the constraint
                for (FriendsModel data : mFriendsListFull) {
                    if (data.getFullname().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(data);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            mFriendsList.clear();
            mFriendsList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
