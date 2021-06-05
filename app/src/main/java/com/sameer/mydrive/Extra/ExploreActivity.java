package com.sameer.mydrive.Extra;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.sameer.mydrive.Adapter.FileAdapter;
import com.sameer.mydrive.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private static final String TAG = "ExploreActivity";

    private RecyclerView mRecycler;
    private List<File> mList;

    private static final int REQUEST_PERMISSION = 44;
    private boolean isPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        mList = new ArrayList<>();
        mRecycler = findViewById(R.id.ae_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);

    }

    private void Explore(File directory) {
        File listFiles[] = directory.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            mList.clear();
            for (int i=0; i<listFiles.length; i++){
                if (!listFiles[i].isHidden()){
                    mList.add(listFiles[i]);
                    Log.d(TAG, "Explore: name_____________________________" + listFiles[i].getName());
                }
            }
            Collections.sort(mList);
            FileAdapter adapter = new FileAdapter(ExploreActivity.this, mList);
            mRecycler.setAdapter(adapter);
        }
    }

    private void askPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)){
            if ((ActivityCompat.shouldShowRequestPermissionRationale(ExploreActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))){

            }else {
                ActivityCompat.requestPermissions(ExploreActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermissionGranted = true;
                String filePath = getIntent().getStringExtra("filePath");
                File directory = new File(filePath);
                Explore(directory);
            }else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        askPermission();
        if (isPermissionGranted){
            String filePath = getIntent().getStringExtra("filePath");
            File directory = new File(filePath);
            Explore(directory);
        }
    }
}
