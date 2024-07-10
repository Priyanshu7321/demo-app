package com.example.demoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.viewHolder> {

    Context context;
    List<fileClass> arrayList;
    public recyclerAdapter(Context context, List<fileClass> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(arrayList.get(position).uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        holder.imageView.setImageBitmap(bitmap);
        holder.textView1.setText(arrayList.get(position).filename);
        holder.textView2.setText(arrayList.get(position).filesize);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView1,textView2;
        public viewHolder(@NonNull View itemView){
            super(itemView);
            imageView=itemView.findViewById(R.id.fileImage);
            textView1=itemView.findViewById(R.id.fileName);
            textView2=itemView.findViewById(R.id.fileSize);
        }
    }
}
