package com.example.ilifestylepal;

import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> implements Filterable {

    ArrayList<NotificationsModel> mNotifList;
    ArrayList<NotificationsModel> mNotifListFull;

    public NotificationsAdapter(ArrayList<NotificationsModel> mNotifList) {
        this.mNotifListFull = mNotifList;
        this.mNotifList = new ArrayList<>(mNotifListFull);
    }

    @NonNull
    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_notifications, parent, false);
        return new NotificationsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.MyViewHolder holder, int position) {
        NotificationsModel model = mNotifList.get(position);

        String notif_id = String.valueOf(model.getNotif_id());
        String notif_uid = String.valueOf(model.getNotif_uid());
        String notif_category = String.valueOf(model.getNotif_category());
        String notif_date = String.valueOf(model.getNotif_date());
        String notif_time = String.valueOf(model.getNotif_time());
        String notif_description = String.valueOf(model.getNotif_description());
        String notif_status = String.valueOf(model.getNotif_status());

        FirebaseAuth mAuth;
        DatabaseReference UsersRef, NotifRef;
        String currentUserID;
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        NotifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        holder.tv_date_time.setText(notif_date + " " + notif_time);

        if (notif_status.equals("unread")) {
            holder.ll_1.setBackgroundResource(R.color.background2);
        } else {
            holder.ll_1.setBackgroundResource(R.color.white);
        }

        if (notif_category.equals("Report")) {
            holder.tv_notif_desc.setText(notif_description);
            holder.civ_pic.setImageResource(R.drawable.ilifestylepal_logo);
        }

        if (notif_category.equals("Friend Request")) {
            String sender_uid = String.valueOf(model.getSender_uid());

            UsersRef.child(sender_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fn = "", ln = "" , full = "";
                    if (snapshot.hasChild("pic_url")) {
                        String picURL = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(holder.itemView.getContext())
                                .load(picURL)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(holder.civ_pic);
                    }
                    if (snapshot.hasChild("firstname")) {
                        fn = snapshot.child("firstname").getValue().toString();
                    }
                    if (snapshot.hasChild("lastname")) {
                        ln = snapshot.child("lastname").getValue().toString();
                    }
                    full = fn + " " + ln;

                    SpannableString spannableString = new SpannableString(full + " " + notif_description);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, full.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tv_notif_desc.setText(spannableString);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("User", "Error: " + error.getMessage());
                }
            });
        }

        if (notif_category.equals("Friend")) {
            String sender_uid = String.valueOf(model.getSender_uid());

            UsersRef.child(sender_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fn = "", ln = "" , full = "";
                    if (snapshot.hasChild("pic_url")) {
                        String picURL = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(holder.itemView.getContext())
                                .load(picURL)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(holder.civ_pic);
                    }
                    if (snapshot.hasChild("firstname")) {
                        fn = snapshot.child("firstname").getValue().toString();
                    }
                    if (snapshot.hasChild("lastname")) {
                        ln = snapshot.child("lastname").getValue().toString();
                    }
                    full = fn + " " + ln;

                    SpannableString spannableString = new SpannableString(full + " and You are now friend.");
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, full.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tv_notif_desc.setText(spannableString);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("User", "Error: " + error.getMessage());
                }
            });
        }

        if (notif_category.equals("Comment")) {
            String sender_uid = String.valueOf(model.getSender_uid());

            UsersRef.child(sender_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fn = "", ln = "" , full = "";
                    if (snapshot.hasChild("pic_url")) {
                        String picURL = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(holder.itemView.getContext())
                                .load(picURL)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(holder.civ_pic);
                    }
                    if (snapshot.hasChild("firstname")) {
                        fn = snapshot.child("firstname").getValue().toString();
                    }
                    if (snapshot.hasChild("lastname")) {
                        ln = snapshot.child("lastname").getValue().toString();
                    }
                    full = fn + " " + ln;

                    SpannableString spannableString = new SpannableString(full + " " + notif_description + ".");
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, full.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tv_notif_desc.setText(spannableString);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("User", "Error: " + error.getMessage());
                }
            });
        }

        holder.ll_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotifRef.child(notif_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (notif_status.equals("unread")) {
                            NotifRef.child(notif_id).child("notif_status").setValue("read");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Notifications", "Error: " + error.getMessage());
                    }
                });
                if (notif_category.equals("Friend Request") || notif_category.equals("Friend")) {
                    Intent findfriends = new Intent(holder.itemView.getContext(), FindFriends.class);
                    holder.itemView.getContext().startActivity(findfriends);
                }
                if (notif_category.equals("Comment")) {
                    String post_id = String.valueOf(model.getPost_id());
                    DatabaseReference PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id);
                    PostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Intent comment = new Intent(holder.itemView.getContext(), PostComments.class);
                                comment.putExtra("post_id", post_id);
                                holder.itemView.getContext().startActivity(comment);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Post", "Error: " + error.getMessage());
                        }
                    });
                }
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civ_pic;
        TextView tv_notif_desc;
        TextView tv_date_time;
        LinearLayout ll_1;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civ_pic = itemView.findViewById(R.id.civ_pic);
            tv_notif_desc = itemView.findViewById(R.id.tv_notif_desc);
            tv_date_time = itemView.findViewById(R.id.tv_date_time);
            ll_1 = itemView.findViewById(R.id.ll_1);
        }
    }

    @Override
    public int getItemCount() {
        return mNotifList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<NotificationsModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mNotifListFull);
            } else {
                // Filter the data based on the constraint
                for (NotificationsModel data : mNotifListFull) {
                    if (data.getNotif_category().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            mNotifList.clear();
            mNotifList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
