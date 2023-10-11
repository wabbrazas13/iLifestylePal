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

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.MyViewHolder> implements Filterable {

    ArrayList<FriendsModel> mDataList;
    ArrayList<FriendsModel> mDataListFull;
    private Boolean FriendRequestChecker = false;

    public FindFriendsAdapter(ArrayList<FriendsModel> mDataList) {
        this.mDataListFull = mDataList;
        this.mDataList = new ArrayList<>(mDataListFull);
    }

    @NonNull
    @Override
    public FindFriendsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_friends_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendsAdapter.MyViewHolder holder, int position) {
        FirebaseAuth mAuth;
        DatabaseReference DatabaseUserRef;
        String currentUserID;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendRequestChecker = true;

        FriendsModel model = mDataList.get(position);
        holder.tv_username.setText(String.valueOf(model.getFullname()));
        holder.tv_email.setText(String.valueOf(model.getEmail()));

        Picasso.with(holder.itemView.getContext())
                .load(model.getPic_url())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.civ_profile_pic);

        DatabaseUserRef.child(model.getUid()).child("Friend Requests").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.btn_2.setText("Cancel");
                    holder.btn_2.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.cancel_friend));
                } else {
                    holder.btn_2.setText("Add Friend");
                    holder.btn_2.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.add_friend));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Friend Requests", "Error: " + error.getMessage());
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

        holder.btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference friendRequestRef = DatabaseUserRef.child(model.getUid()).child("Friend Requests");
                ValueEventListener friendRequestListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (FriendRequestChecker == true) {
                            if (snapshot.hasChild(currentUserID)) {
                                friendRequestRef.child(currentUserID).removeValue();
                                RemoveFriendRequestNotification(model.getUid(), currentUserID);
                            } else {
                                friendRequestRef.child(currentUserID).setValue(true);
                                AddFriendRequestNotification(model.getUid(), currentUserID);
                            }
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
    }

    private void AddFriendRequestNotification(String uid, String currentUserID) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notif_desc = "has sent you friend request";

        Calendar calCurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        String savedCurrentDate = currentDate.format(calCurrentDate.getTime());

        Calendar calCurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm a");
        String savedCurrentTime = currentTime.format(calCurrentTime.getTime());

        String randomKey = notifRef.push().getKey();

        HashMap notifMap = new HashMap();
        notifMap.put("notif_id", randomKey);
        notifMap.put("notif_category", "Friend Request");
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
                            childSnapshot.child("notif_uid").getValue().toString().equals(uid) &&
                            childSnapshot.child("sender_uid").getValue().toString().equals(currentUserID)) {
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
        return mDataList.size();
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
                filteredList.addAll(mDataListFull);
            } else {
                // Filter the data based on the constraint
                for (FriendsModel data : mDataListFull) {
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
            mDataList.clear();
            mDataList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
