package com.example.ilifestylepal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddPostDialog extends AppCompatDialogFragment {

    private Button btn_post_image, btn_post_video, btn_cancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_post,null);
        btn_post_image = view.findViewById(R.id.btn_post_image);
        btn_post_video = view.findViewById(R.id.btn_post_video);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        btn_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addImagePostIntent = new Intent(getContext(), AddImagePost.class);
                startActivity(addImagePostIntent);
            }
        });

        btn_post_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addVideoPostIntent = new Intent(getContext(), AddVideoPost.class);
                startActivity(addVideoPostIntent);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
