package com.example.viewrecorder.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.viewrecorder.R;

public class HelpDialog extends AlertDialog {
    public static TextView mHelpText;
    Button mButton;

    public HelpDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.help_layout, null);
        mHelpText = view.findViewById(R.id.helpDialogText);
        String text = "Connect to only one bluetooth Device";
        mHelpText.setText(text);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = 300;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }
}
