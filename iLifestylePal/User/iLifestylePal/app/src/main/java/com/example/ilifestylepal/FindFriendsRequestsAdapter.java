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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsRequestsAdapter extends RecyclerView.Adapter<FindFriendsRequestsAdapter.MyViewHolder> implements Filterable {

    ArrayList<FriendsModel> mFriendRequests;
    ArrayList<FriendsModel> mFriendRequestsFull;
    private Boolean FriendRequestChecker = false;
    private Boolean FriendsChecker = false;

    public FindFriendsRequestsAdapter(ArrayList<FriendsModel> mFriendRequests) {
        this.mFriendRequestsFull = mFriendRequests;
        this.mFriendRequests = new ArrayList<>(mFriendRequestsFull);
    }

    @NonNull
    @Override
    public FindFriendsRequestsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_friends_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendsRequestsAdapter.MyViewHolder holder, int position) {
        FirebaseAuth mAuth;
        DatabaseReference DatabaseUserRef;
        String currentUserID;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendRequestChecker = true;
        FriendsChecker = true;

        FriendsModel model = mFriendRequests.get(position);
        holder.tv_username.setText(String.valueOf(model.getFullname()));
        holder.tv_email.setText("has sent you friend request");

        Picasso.with(holder.itemView.getContext())
                .load(model.getPic_url())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.civ_profile_pic);

        holder.btn_1.setText("Confirm");
        holder.btn_1.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.add_friend));
        holder.btn_2.setText("Reject");
        holder.btn_2.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.cancel_friend));

        holder.btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference friendsRef = DatabaseUserRef.child(currentUserID);
                ValueEventListener friendsRefListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (FriendsChecker == true) {
                            friendsRef.child("Friends").child(model.getUid()).setValue(true);
                            friendsRef.child("Friend Requests").child(model.getUid()).removeValue();
                            RemoveFriendRequestNotification(model.getUid(), currentUserID);
                            AddFriendNotification(currentUserID, model.getUid());
                            FriendRequestChecker = false;
                            friendsRef.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Friends", "Error: " + error.getMessage());
                    }
                };
                friendsRef.addValueEventListener(friendsRefListener);

                DatabaseUserRef.child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DatabaseUserRef.child(model.getUid()).child("Friends").child(currentUserID).setValue(true);
                        AddFriendNotification(model.getUid(), currentUserID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Friends", "Error: " + error.getMessage());
                    }
                });
            }
        });

        holder.btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference friendRequestRef = DatabaseUserRef.child(currentUserID).child("Friend Requests");
                ValueEventListener friendRequestListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (FriendRequestChecker == true) {
                            friendRequestRef.child(model.getUid()).removeValue();
                            RemoveFriendRequestNotification(model.getUid(), currentUserID);
                            FriendRequestChecker = false;
                            friendRequestRef.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DB Friend Requests", "Error: " + error.getMessage());
                    }
                };
                friendRequestRef.addValueEventListener(friendRequestListener);
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

    private void AddFriendNotification(String uid, String currentUserID) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notif_desc = "You are now friend with";

        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        String savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm a");
        String savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String randomKey = notifRef.push().getKey();

        HashMap notifMap = new HashMap();
        notifMap.put("notif_id", randomKey);
        notifMap.put("notif_category", "Friend");
        notifMap.put("notif_timestamp", ServerValue.TIMESTAMP);
        notifMap.put("notif_date", savedCurrentDate);
        notifMap.put("notif_time", savedCurrentTime);
        notifMap.put("notif_description", notif_desc);
        notifMap.put("notif_uid", uid);
        notifMap.put("notif_status", "unread");
        notifMap.put("sender_uid", currentUserID);

        notifRef.child(randomKey).updateChildren(notifMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.e("Notifications", "Notification Added Successfully.");
                } else {
                    Log.e("Notifications", "Error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void RemoveFriendRequestNotification(String uid, String currentUserID) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.hasChild("notif_category") &&
                            childSnapshot.child("notif_category").getValue().toString().equals("Friend Request") &&
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
        return mFriendRequests.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FriendsModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mFriendRequestsFull);
            } else {
                // Filter the data based on the constraint
                for (FriendsModel data : mFriendRequestsFull) {
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
            mFriendRequests.clear();
            mFriendRequests.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
