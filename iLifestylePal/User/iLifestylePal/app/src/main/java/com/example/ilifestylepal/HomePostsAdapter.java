package com.example.ilifestylepal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePostsAdapter extends RecyclerView.Adapter<HomePostsAdapter.MyViewHolder> implements Filterable {

    ArrayList<PostsModel> mPostsList;
    ArrayList<PostsModel> mPostsListFull;
    private List<SimpleExoPlayer> playerList = new ArrayList<>();
    private Boolean LikesChecker = false;

    public HomePostsAdapter(ArrayList<PostsModel> mPostsList) {
        this.mPostsListFull = mPostsList;
        this.mPostsList = new ArrayList<>(mPostsListFull);
    }

    @NonNull
    @Override
    public HomePostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_posts_layout, parent, false);
        return new HomePostsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePostsAdapter.MyViewHolder holder, int position) {
        PostsModel model = mPostsList.get(position);

        FirebaseAuth mAuth;
        DatabaseReference DatabaseUsersRef, DatabasePostsRef;
        String currentUserID;
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabasePostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        LikesChecker = true;

        holder.tv_post_privacy.setText(String.valueOf(model.getPost_privacy()));
        holder.tv_post_date.setText(String.valueOf(model.getPost_date()));
        holder.tv_post_time.setText(String.valueOf(model.getPost_time()));
        holder.tv_post_description.setText(String.valueOf(model.getPost_description()));

        String post_uid = String.valueOf(model.getPost_uid());
        String post_id = String.valueOf(model.getPost_id());

        DatabasePostsRef.child(post_id).child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count > 1) {
                    holder.tv_comments_count.setText(count + " comments");
                } else {
                    holder.tv_comments_count.setText(count + " comment");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Comments", "Error: " + error.getMessage());
            }
        });

        DatabasePostsRef.child(post_id).child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count > 1) {
                    holder.tv_likes_count.setText(count + " likes");
                } else {
                    holder.tv_likes_count.setText(count + " like");
                }
                if (snapshot.hasChild(currentUserID)) {
                    holder.btn_like.setImageResource(R.drawable.ic_heart_filled);
                } else {
                    holder.btn_like.setImageResource(R.drawable.ic_heart_unfilled);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Likes", "Error: " + error.getMessage());
            }
        });

        DatabaseUsersRef.child(post_uid).addValueEventListener(new ValueEventListener() {
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
                Log.e("DB Posts", "Error: " + error.getMessage());
            }
        });

        if (String.valueOf(model.getPost_type()).equals("image")) {
            holder.ll_post_video.setVisibility(View.GONE);
            holder.iv_post_image.setVisibility(View.VISIBLE);

            Picasso.with(holder.itemView.getContext())
                    .load(String.valueOf(model.getPost_url()))
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(holder.iv_post_image);
        } else {
            holder.ll_post_video.setVisibility(View.VISIBLE);
            holder.iv_post_image.setVisibility(View.GONE);

            SimpleExoPlayer player = new SimpleExoPlayer.Builder(holder.itemView.getContext()).build();
            playerList.add(player);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(holder.itemView.getContext())).createMediaSource(Uri.parse(String.valueOf(model.getPost_url())));
            player.setMediaSource(mediaSource);
            player.prepare();
            holder.player_view.setPlayer(player);
            player.setPlayWhenReady(false);
        }

        holder.btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference likesRef = DatabasePostsRef.child(post_id).child("Likes");
                ValueEventListener likesRefListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (LikesChecker == true) {
                            if (snapshot.hasChild(currentUserID)) {
                                likesRef.child(currentUserID).removeValue();
                            } else {
                                likesRef.child(currentUserID).setValue(true);
                            }
                            LikesChecker = false;
                            likesRef.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DB Likes", "Error: " + error.getMessage());
                    }
                };
                likesRef.addValueEventListener(likesRefListener);
            }
        });

        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comment = new Intent(holder.itemView.getContext(), PostComments.class);
                comment.putExtra("post_id", post_id);
                holder.itemView.getContext().startActivity(comment);
            }
        });

        holder.civ_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUserID.equals(post_uid)) {
                    Intent my_profile = new Intent(holder.itemView.getContext(), MyProfile.class);
                    holder.itemView.getContext().startActivity(my_profile);
                } else {
                    Intent view_profile = new Intent(holder.itemView.getContext(), ViewProfile.class);
                    view_profile.putExtra("uid", post_uid);
                    holder.itemView.getContext().startActivity(view_profile);
                }
            }
        });

        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUserID.equals(post_uid)) {
                    Intent my_profile = new Intent(holder.itemView.getContext(), MyProfile.class);
                    holder.itemView.getContext().startActivity(my_profile);
                } else {
                    Intent view_profile = new Intent(holder.itemView.getContext(), ViewProfile.class);
                    view_profile.putExtra("uid", post_uid);
                    holder.itemView.getContext().startActivity(view_profile);
                }
            }
        });

        holder.ib_post_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calCurrentDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                String savedCurrentDate = currentDate.format(calCurrentDate.getTime());

                Calendar calCurrentTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                String savedCurrentTime = currentTime.format(calCurrentTime.getTime());

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_post_menu, null);

                Button btn_report = dialogView.findViewById(R.id.btn_report);
                Button btn_update = dialogView.findViewById(R.id.btn_update);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btn_report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post_uid.equals(currentUserID)) {
                            Toast.makeText(holder.itemView.getContext(), "Unable to report own post.", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(holder.itemView.getContext());
                            LayoutInflater inflater2 = LayoutInflater.from(holder.itemView.getContext());
                            View dialogView2 = inflater2.inflate(R.layout.dialog_report_post, null);

                            EditText et_report2 = dialogView2.findViewById(R.id.et_report);
                            Button btn_report2 = dialogView2.findViewById(R.id.btn_report);
                            Button btn_cancel2 = dialogView2.findViewById(R.id.btn_cancel);

                            builder2.setView(dialogView2);
                            AlertDialog alertDialog2 = builder2.create();
                            alertDialog2.show();

                            btn_report2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DatabaseReference DatabaseReportPostsRef = FirebaseDatabase.getInstance().getReference()
                                            .child("Reports")
                                            .child("Posts")
                                            .child(post_id)
                                            .child(currentUserID);

                                    String report_2 = et_report2.getText().toString();

                                    HashMap reportMap = new HashMap();
                                    reportMap.put("report_timestamp", ServerValue.TIMESTAMP);
                                    reportMap.put("report_date", savedCurrentDate);
                                    reportMap.put("report_description", report_2);
                                    reportMap.put("post_uid", post_uid);
                                    reportMap.put("post_id", post_id);
                                    reportMap.put("report_uid", currentUserID);
                                    reportMap.put("report_time", savedCurrentTime);

                                    if (TextUtils.isEmpty(report_2)) {
                                        Toast.makeText(holder.itemView.getContext(), "Please write your report.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DatabaseReportPostsRef.updateChildren(reportMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(holder.itemView.getContext(), "Post Reported", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.e("DB Post Reports", "Error: " + task.getException().getMessage());
                                                }
                                            }
                                        });
                                    }
                                    alertDialog2.dismiss();
                                }
                            });

                            btn_cancel2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog2.dismiss();
                                }
                            });
                        }
                    }
                });

                btn_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post_uid.equals(currentUserID)) {
                            alertDialog.dismiss();

                            Intent update_post = new Intent(holder.itemView.getContext(), UpdatePost.class);
                            update_post.putExtra("post_id", post_id);
                            holder.itemView.getContext().startActivity(update_post);

                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post_uid.equals(currentUserID)) {
                            alertDialog.dismiss();

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(holder.itemView.getContext());
                            builder2.setTitle("Deleting Post");
                            builder2.setMessage("Are you sure about deleting this post?");
                            builder2.setCancelable(false);

                            builder2.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference postRef = DatabasePostsRef.child(post_id);
                                    ValueEventListener postRefListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                postRef.removeValue();
                                            }
                                            postRef.removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("DB Post", "Error: " + error.getMessage());
                                        }
                                    };
                                    postRef.addValueEventListener(postRefListener);

                                    DatabaseReference postReportRef = FirebaseDatabase.getInstance().getReference()
                                            .child("Reports")
                                            .child("Posts")
                                            .child(post_id);
                                    ValueEventListener postReportRefListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                postReportRef.removeValue();
                                            }
                                            postReportRef.removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("DB Post Report", "Error: " + error.getMessage());
                                        }
                                    };
                                    postReportRef.addValueEventListener(postReportRefListener);

                                    Toast.makeText(holder.itemView.getContext(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    alertDialog.show();
                                }
                            });

                            AlertDialog alertDialog2 = builder2.create();
                            alertDialog2.show();
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civ_profile_pic;
        TextView tv_username;
        TextView tv_post_privacy;
        TextView tv_post_date;
        TextView tv_post_time;
        TextView tv_post_description;
        ImageButton ib_post_menu;
        ImageView iv_post_image;
        LinearLayout ll_post_video;
        PlayerView player_view;
        ImageButton btn_like;
        ImageButton btn_comment;
        TextView tv_likes_count;
        TextView tv_comments_count;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civ_profile_pic = itemView.findViewById(R.id.civ_profile_pic);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_post_privacy = itemView.findViewById(R.id.tv_post_privacy);
            tv_post_date = itemView.findViewById(R.id.tv_post_date);
            tv_post_time = itemView.findViewById(R.id.tv_post_time);
            tv_post_description = itemView.findViewById(R.id.tv_post_description);
            ib_post_menu = itemView.findViewById(R.id.ib_post_menu);
            iv_post_image = itemView.findViewById(R.id.iv_post_image);
            ll_post_video = itemView.findViewById(R.id.ll_post_video);
            player_view = itemView.findViewById(R.id.player_view);
            btn_like = itemView.findViewById(R.id.btn_like);
            btn_comment = itemView.findViewById(R.id.btn_comment);
            tv_likes_count = itemView.findViewById(R.id.tv_likes_count);
            tv_comments_count = itemView.findViewById(R.id.tv_comments_count);
        }
    }

    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<PostsModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mPostsListFull);
            } else {
                // Filter the data based on the constraint
                for (PostsModel data : mPostsListFull) {
                    if (data.getPost_description().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            mPostsList.clear();
            mPostsList.addAll((ArrayList)results.values);

            for (SimpleExoPlayer player : playerList) {
                player.release();
            }

            notifyDataSetChanged();
        }
    };
}
