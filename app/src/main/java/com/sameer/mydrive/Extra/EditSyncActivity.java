package com.sameer.mydrive.Extra;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.sameer.mydrive.BackgroundWork.MyWorker;
import com.sameer.mydrive.Object.Folder;
import com.sameer.mydrive.R;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class EditSyncActivity extends AppCompatActivity {

    private static final String TAG = "EditSyncActivity";

    private TextView mAddress, mReset;
    private TimePicker mTimePicker;
    private Button mSubmit;
    private RadioButton mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday, mSunday;
    private String filePath;
    private Switch mSyncSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sync);
        filePath = getIntent().getStringExtra("path");
        mAddress = findViewById(R.id.aes_address);
        mAddress.setText(filePath);
        mTimePicker = findViewById(R.id.aes_time_picker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(2);
        mTimePicker.setCurrentMinute(0);
        mSubmit = findViewById(R.id.aes_submit);
        mReset = findViewById(R.id.aes_reset);
        mSyncSwitch = findViewById(R.id.aes_sync);
        mMonday = findViewById(R.id.rb_monday);
        mTuesday = findViewById(R.id.rb_tuesday);
        mWednesday = findViewById(R.id.rb_wednesday);
        mThursday = findViewById(R.id.rb_thursday);
        mFriday = findViewById(R.id.rb_friday);
        mSaturday = findViewById(R.id.rb_saturday);
        mSunday = findViewById(R.id.rb_sunday);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitActivity();
            }
        });
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMonday.setChecked(false);
                mTuesday.setChecked(false);
                mWednesday.setChecked(false);
                mThursday.setChecked(false);
                mFriday.setChecked(false);
                mSaturday.setChecked(false);
                mSunday.setChecked(false);
            }
        });
        String folderSP = getIntent().getStringExtra("folder");
        if (folderSP != null){
            updateForm(folderSP);
        }
    }

    private void updateForm(String folderSP) {
        Folder folder = MainActivity.loadFolder(folderSP);
        if (folder != null){
            mSyncSwitch.setChecked(folder.getSyncable());
            mMonday.setChecked(folder.getDays()[0]);
            mTuesday.setChecked(folder.getDays()[1]);
            mWednesday.setChecked(folder.getDays()[2]);
            mThursday.setChecked(folder.getDays()[3]);
            mFriday.setChecked(folder.getDays()[4]);
            mSaturday.setChecked(folder.getDays()[5]);
            mSunday.setChecked(folder.getDays()[6]);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int hr = Integer.parseInt(folder.getTime().split(":")[0]);
                    int mn = Integer.parseInt(folder.getTime().split(":")[1]);
                    mTimePicker.setMinute(mn);
                    mTimePicker.setHour(hr);


                    //WorkManager
                    for (int i = 0; i < 7;i++){
                        if(folder.getDays()[i]){
                            String FolderPath = folder.getLocalAddress();
                            /*MyWorker.FilepathSet(FolderPath);
                            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,1,TimeUnit.MINUTES)
                                    .build();
                            WorkManager.getInstance().enqueue(periodicWorkRequest);

                             */

                        }
                    }
                }
            }catch (Exception e){
                Log.d(TAG, "updateForm: Time: " + e.getMessage());
            }
        }
    }


    private void submitActivity() {
        String time = mTimePicker.getCurrentHour() + ":" + mTimePicker.getCurrentMinute();
        Boolean[] days = {
                mMonday.isChecked(),
                mTuesday.isChecked(),
                mWednesday.isChecked(),
                mThursday.isChecked(),
                mFriday.isChecked(),
                mSaturday.isChecked(),
                mSunday.isChecked(),
        };
        boolean flag = false;
        for(int i=0; i<days.length; i++) {
            if (days[i]){
                flag = true;
                break;
            }
        }
        boolean isSyncable = mSyncSwitch.isChecked();
        int status;
        if (!isSyncable) status = -1;
        else status = 0;
        if (flag){
            File file = new File(filePath);
            Folder folder = new Folder(file.getName(), file.getPath(), days, time, "null", isSyncable, status);
            boolean isSaved = MainActivity.saveMedia(folder);
            if (isSaved){
                Toast.makeText(EditSyncActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(EditSyncActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(EditSyncActivity.this, MainActivity.class).putExtra("initSync", filePath));
        }else {
            Toast.makeText(EditSyncActivity.this, "Select at least one day", Toast.LENGTH_SHORT).show();
        }
    }


}
