package com.example.demoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<fileClass> fileClassList;
    RecyclerView recyclerView;
    recyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileClassList=new ArrayList<>();
        adapter=new recyclerAdapter(MainActivity.this,fileClassList);
        recyclerView.setAdapter(adapter);
        askpermission();
        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    fileSelect();
                }
            }
        });
    }
    public void askpermission(){
        String[] permission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        activityResultLauncher.launch(permission);
    }
    ActivityResultLauncher<String[]> activityResultLauncher=registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),result->{
        if(result.containsValue(false)){
            Toast.makeText(MainActivity.this,"Please allow permission",3000).show();
            askpermission();
        }
    });
    ActivityResultLauncher<Intent> activityResultLauncher1=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    fetchFileNameAndSize(data.getData());
                }
            }
        }
    });
    private void fetchFileNameAndSize(Uri uri) {
        String displayName = null;
        long fileSize = 0;

        fileClass obj=new fileClass();
        obj.uri=uri;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

            cursor.moveToFirst();
            if (nameIndex != -1) {
                displayName = cursor.getString(nameIndex);
                obj.filename=displayName;
                Log.d("filename",displayName);
            }
            if (sizeIndex != -1) {
                fileSize = cursor.getLong(sizeIndex);
                double fileSizeInMB = (double) fileSize / (1024 * 1024);
                DecimalFormat decimalFormat=new DecimalFormat("#.##");
                obj.filesize=decimalFormat.format(fileSizeInMB)+" mb ";
                Log.d("filesize",String.valueOf(fileSize));
            }
            cursor.close();
        }
        Log.d("uri",uri.toString());
        fileClassList.add(obj);
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fileSelect(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Uri initialUri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
        activityResultLauncher1.launch(intent);
    }
}