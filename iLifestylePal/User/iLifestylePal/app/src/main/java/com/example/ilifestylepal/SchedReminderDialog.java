package com.example.ilifestylepal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class SchedReminderDialog extends AppCompatDialogFragment {
    private RadioButton rb_5, rb_10, rb_15, rb_30, rb_60;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference SleepScheduleRef = FirebaseDatabase.getInstance().getReference().child("Sleep Schedule").child(currentUserID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sched_reminder_dialog,null);
        rb_5 = view.findViewById(R.id.radio_5);
        rb_10 = view.findViewById(R.id.radio_10);
        rb_15 = view.findViewById(R.id.radio_15);
        rb_30 = view.findViewById(R.id.radio_30);
        rb_60 = view.findViewById(R.id.radio_60);

        builder.setView(view)
                .setTitle("Set Reminder Notification")
                .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int min = 0;

                        if(rb_5.isChecked()) {
                            min = 5;
                        }

                        if(rb_10.isChecked()) {
                            min = 10;
                        }

                        if(rb_15.isChecked()) {
                            min = 15;
                        }

                        if(rb_30.isChecked()) {
                            min = 30;
                        }

                        if(rb_60.isChecked()) {
                            min = 60;
                        }

                        SharedPreferences sharedPref = getContext().getSharedPreferences("iLifestylePal", Context.MODE_PRIVATE);
                        sharedPref.edit().putBoolean("reminder_status",true).apply();
                        sharedPref.edit().putInt("reminder_minutes", min).apply();

                        HashMap settingsMap = new HashMap();
                        settingsMap.put("reminder_minutes", min);
                        settingsMap.put("reminder_status", "ON");

                        SleepScheduleRef.child("Settings").updateChildren(settingsMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                            }
                        });

                        Toast.makeText(getActivity(), "Reminder ON", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap settingsMap = new HashMap();
                        settingsMap.put("reminder_status", "_OFF");

                        SleepScheduleRef.child("Settings").updateChildren(settingsMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                            }
                        });

                        Toast.makeText(getActivity(), "Reminder OFF", Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }
}
