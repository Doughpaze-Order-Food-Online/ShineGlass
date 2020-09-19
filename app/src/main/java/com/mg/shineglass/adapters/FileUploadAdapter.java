package com.mg.shineglass.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mg.shineglass.Interface.deleteFile;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.NewRequestActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Category;

import java.io.File;
import java.util.List;


public class FileUploadAdapter extends RecyclerView.Adapter<FileUploadAdapter.FileUploadItemHolder>  {

    private List<Uri> list;
    private Context context;
    private deleteFile deleteFile;
    private Boolean flag;

    public FileUploadAdapter(List<Uri> list, Context context,deleteFile deleteFile,Boolean flag) {
        this.list=list;
        this.context=context;
        this.deleteFile=deleteFile;
        this.flag=flag;
    }


    @NonNull
    @Override
    public FileUploadAdapter.FileUploadItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(flag?R.layout.uploaded_cart_item:R.layout.uploaded_file_item, parent, false);
        return new FileUploadAdapter.FileUploadItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileUploadAdapter.FileUploadItemHolder fileUploadItemHolder, int i) {

        fileUploadItemHolder.name.setText(getFileName(list.get(i)));
        fileUploadItemHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile.remove(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null)
        {
            return list.size();
        }
        return 0;

    }

    class FileUploadItemHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private FrameLayout remove;


        FileUploadItemHolder (View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.file_name_txt);
            remove=itemView.findViewById(R.id.cross_btn);

        }
    }







    public String getFileName(Uri uri) {
      String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }




}
