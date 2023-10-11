package com.example.ilifestylepal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogBox_Message {

    Context context;
    String title, message, positiveLabel, negativeLabel;
    DialogInterface.OnClickListener positiveButtonListener;

    public DialogBox_Message(Context context, String title, String message, String positiveLabel, String negativeLabel) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.positiveLabel = positiveLabel;
        this.negativeLabel = negativeLabel;
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener listener) {
        positiveButtonListener = listener;
    }

    public void displayDialogBox(){
        // Create an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message of the dialog box
        builder.setTitle(title);
        builder.setMessage(message);

        // Set the positive button
        builder.setPositiveButton(positiveLabel, positiveButtonListener);

        // Set the negative button
        builder.setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the negative button, do something here
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        // Create and show the dialog box
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
