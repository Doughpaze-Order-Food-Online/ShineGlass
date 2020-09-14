package com.mg.shineglass.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mg.shineglass.Interface.deleteFile;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Category;

import java.io.File;
import java.util.List;


public class FileUploadAdapter extends RecyclerView.Adapter<FileUploadAdapter.FileUploadItemHolder>  {

    private List<Uri> list;
    private Context context;
    private deleteFile deleteFile;

    public FileUploadAdapter(List<Uri> list, Context context,deleteFile deleteFile) {
        this.list=list;
        this.context=context;
        this.deleteFile=deleteFile;
    }


    @NonNull
    @Override
    public FileUploadAdapter.FileUploadItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_file_item, parent, false);
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
        return list.size();

    }

    class FileUploadItemHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView remove;


        FileUploadItemHolder (View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.file_name_txt);
            remove=itemView.findViewById(R.id.remove);

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
