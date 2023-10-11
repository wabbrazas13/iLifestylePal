package com.example.ilifestylepal;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

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

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> implements Filterable {

    ArrayList<CommentsModel> mCommentsList;
    ArrayList<CommentsModel> mCommentsListFull;

    public CommentsAdapter(ArrayList<CommentsModel> mCommentsList) {
        this.mCommentsListFull = mCommentsList;
        this.mCommentsList = new ArrayList<>(mCommentsListFull);
    }

    @NonNull
    @Override
    public CommentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_comments_layout, parent, false);
        return new CommentsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.MyViewHolder holder, int position) {
        CommentsModel model = mCommentsList.get(position);

        String post_id = String.valueOf(model.getPost_id());
        String comment_id = String.valueOf(model.getComment_id());
        String comment_uid = String.valueOf(model.getComment_uid());
        String comment_text = String.valueOf(model.getComment_text());
        String comment_date = String.valueOf(model.getComment_date());
        String comment_time = String.valueOf(model.getComment_time());

        holder.tv_comment.setText(comment_text);
        holder.tv_date.setText(comment_date);
        holder.tv_time.setText(comment_time);

        FirebaseAuth mAuth;
        DatabaseReference DatabaseUsersRef, DatabasePostsRef;
        String currentUserID;
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabasePostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        DatabaseUsersRef.child(comment_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("pic_url")) {
                        String pic_url = snapshot.child("pic_url").getValue().toString();
                        Picasso.with(holder.itemView.getContext())
                                .load(pic_url)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(holder.civ_profile_pic);
                    }
                    if (snapshot.hasChild("fullname")) {
                        holder.tv_username.setText(snapshot.child("fullname").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Users", "Error: " + error.getMessage());
            }
        });

        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment_uid.equals(currentUserID)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog_update_delete_comment, null);

                    EditText comment = dialogView.findViewById(R.id.et_comment);
                    TextView update = dialogView.findViewById(R.id.tv_update);
                    TextView delete = dialogView.findViewById(R.id.tv_delete);

                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    comment.setText(comment_text);

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference commentRef = DatabasePostsRef.child(post_id).child("Comments").child(comment_id);
                            ValueEventListener commentRefListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild("comment_text")) {
                                        commentRef.child("comment_text").setValue(comment.getText().toString());
                                        Toast.makeText(holder.itemView.getContext(), "Comment Updated", Toast.LENGTH_SHORT).show();
                                    }
                                    commentRef.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("DB Comments", "Error: " + error.getMessage());
                                }
                            };
                            commentRef.addValueEventListener(commentRefListener);
                            alertDialog.dismiss();
                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference commentRef = DatabasePostsRef.child(post_id).child("Comments").child(comment_id);
                            ValueEventListener commentRefListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        commentRef.removeValue();
                                        Toast.makeText(holder.itemView.getContext(), "Comment Deleted", Toast.LENGTH_SHORT).show();
                                        try {
                                            RemoveNotification(post_id, comment_id);
                                        } catch (Exception e) {
                                            Log.e("Notification", "Error: " + e.getMessage());
                                        }

                                    }
                                    commentRef.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("DB Comments", "Error: " + error.getMessage());
                                }
                            };
                            commentRef.addValueEventListener(commentRefListener);
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void RemoveNotification(String post_id, String comment_id) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.hasChild("comment_id") && childSnapshot.hasChild("post_id")) {
                        String commentId = childSnapshot.child("comment_id").getValue(String.class);
                        String postId = childSnapshot.child("post_id").getValue(String.class);
                        if (commentId.equals(comment_id) && postId.equals(post_id)) {
                            childSnapshot.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Notification", "Error: " + error.getMessage());
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civ_profile_pic;
        TextView tv_username;
        TextView tv_comment;
        TextView tv_date;
        TextView tv_time;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civ_profile_pic = itemView.findViewById(R.id.civ_profile_pic);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<CommentsModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mCommentsListFull);
            } else {
                // Filter the data based on the constraint
                for (CommentsModel data : mCommentsListFull) {
                    if (data.getComment_text().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            mCommentsList.clear();
            mCommentsList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
