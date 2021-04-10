package com.example.viewrecorder.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.viewrecorder.Adapter.RecordingAdapter;
import com.example.viewrecorder.R;
import com.example.viewrecorder.model.Recording;

public class EditName extends AlertDialog {
    public static  EditText mEditText;
    Button mButton;

    public EditName(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_name, null);
        mEditText = view.findViewById(R.id.editname);
        mButton = view.findViewById(R.id.saveeditedname);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = 300;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }

    public static String updatedName(){
        String editedName = mEditText.getText().toString();
        return editedName;
    }



}
