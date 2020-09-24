package com.mg.shineglass.utils;

import android.app.AlertDialog;
import android.content.Context;

import com.mg.shineglass.R;

public class progress {
    AlertDialog.Builder builder;


    public void setDialogue(Boolean show, Context context)
    {
        builder =new AlertDialog.Builder(context);
        builder.setView(R.layout.progress_loading);
    }


}
