package com.sameer.mydrive.Extra;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;
import com.sameer.mydrive.Adapter.MainAdapter;
import com.sameer.mydrive.BackgroundWork.MyWorker;
import com.sameer.mydrive.Object.Folder;
import com.sameer.mydrive.Object.GoogleDriveFileHolder;
import com.sameer.mydrive.R;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FloatingActionButton mFab;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static DriveServiceHelper mDriveServiceHelper;


    public static String TYPE_AUDIO = "application/vnd.google-apps.audio";
    public static String TYPE_GOOGLE_DOCS = "application/vnd.google-apps.document";
    public static String TYPE_GOOGLE_DRAWING = "application/vnd.google-apps.drawing";
    public static String TYPE_GOOGLE_DRIVE_FILE = "application/vnd.google-apps.file";
    public static String TYPE_GOOGLE_FORMS = "application/vnd.google-apps.form";
    public static String TYPE_GOOGLE_FUSION_TABLES = "application/vnd.google-apps.fusiontable";
    public static String TYPE_GOOGLE_MY_MAPS = "application/vnd.google-apps.map";
    public static String TYPE_PHOTO = "application/vnd.google-apps.photo";
    public static String TYPE_GOOGLE_SLIDES = "application/vnd.google-apps.presentation";
    public static String TYPE_GOOGLE_APPS_SCRIPTS = "application/vnd.google-apps.script";
    public static String TYPE_GOOGLE_SITES = "application/vnd.google-apps.site";
    public static String TYPE_GOOGLE_SHEETS = "application/vnd.google-apps.spreadsheet";
    public static String TYPE_UNKNOWN = "application/vnd.google-apps.unknown";
    public static String TYPE_VIDEO = "application/vnd.google-apps.video";
    public static String TYPE_3_RD_PARTY_SHORTCUT = "application/vnd.google-apps.drive-sdk";


    public static String EXPORT_TYPE_HTML = "text/html";
    public static String EXPORT_TYPE_HTML_ZIPPED = "application/zip";
    public static String EXPORT_TYPE_PLAIN_TEXT = "text/plain";
    public static String EXPORT_TYPE_RICH_TEXT = "application/rtf";
    public static String EXPORT_TYPE_OPEN_OFFICE_DOC = "application/vnd.oasis.opendocument.text";
    public static String EXPORT_TYPE_PDF = "application/pdf";
    public static String EXPORT_TYPE_MS_WORD_DOCUMENT = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static String EXPORT_TYPE_EPUB = "application/epub+zip";
    public static String EXPORT_TYPE_MS_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String EXPORT_TYPE_OPEN_OFFICE_SHEET = "application/x-vnd.oasis.opendocument.spreadsheet";
    public static String EXPORT_TYPE_CSV = "text/csv";
    public static String EXPORT_TYPE_TSV = "text/tab-separated-values";
    public static String EXPORT_TYPE_JPEG = "application/zip";
    public static String EXPORT_TYPE_PNG = "image/png";
    public static String EXPORT_TYPE_SVG = "image/svg+xml";
    public static String EXPORT_TYPE_MS_POWER_POINT = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static String EXPORT_TYPE_OPEN_OFFICE_PRESENTATION = "application/vnd.oasis.opendocument.presentation";
    public static String EXPORT_TYPE_JSON = "application/vnd.google-apps.script+json";



    private List<Folder> mList;
    private RecyclerView mRecycler;
    private TextView mEmail;
    private ImageView mSyncAll;
    private SwipeRefreshLayout mSwipe;
    private static TextView mResult;
    private static ScrollView mScroll;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        mList = new ArrayList<>();
        mRecycler = findViewById(R.id.am_recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFab = findViewById(R.id.am_fab);
        mResult = findViewById(R.id.am_result);
        mEmail = findViewById(R.id.am_email);
        mSyncAll = findViewById(R.id.am_sync_all);
        mSwipe = findViewById(R.id.am_swipe);
        mScroll = findViewById(R.id.am_scroll);


        //WorkManager

       /* PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,1, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueue(periodicWorkRequest);

        */




        requestSignIn();
        init();
    }


    private void init() {

        
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ExploreActivity.class).putExtra("filePath", "/mnt/sdcard/"));
            }
        });
        mSyncAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sync all folders");
                builder.setPositiveButton("Sync", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        syncAll();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkSync();
                loadMedia();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void syncAll() {
        if (!isConnected()){
            Toast.makeText(this, "No Internet!", Toast.LENGTH_SHORT).show();
        }else {
            Set<String> keys = sharedPreferences.getAll().keySet();
            Gson gson = new Gson();
            for(String key : keys) {
                String json = sharedPreferences.getString(key, "default");
                if (json.equals("default")) {
                    Log.d(TAG, "LoadMedia: key pair not found! key: " + key);
                } else {
                    boolean updateFound = false;
                    Folder folder = gson.fromJson(json, Folder.class);
                    if (folder.getSyncable()){
                        if (folder.getLocalAddress() != null){
                            Log.d(TAG, "checkSync: Folder found!: " + folder.getLocalAddress());
                            SyncFolders syncFolders = new SyncFolders(folder.getLocalAddress());
                            syncFolders.execute();
                            //MyWorker.FilepathSet(folder.getLocalAddress());
                            folder.setLastSync(getDay());
                            saveMedia(folder);
                            updateFound = true;
                        }
                    }
                    if (updateFound){
                        scrollTextUpdate("Please wait, syncing files\n");
                    }else {
                        scrollTextUpdate("No changes detected\n");
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnected()){
            Toast.makeText(this, "No Internet!", Toast.LENGTH_SHORT).show();
        }
        try {
            loadMedia();
        }catch (Exception e){
            Log.d(TAG, "onStart: Load Media: " + e.getMessage());
        }
        checkSync();
    }

    public static boolean saveMedia(Folder folder) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(folder);
            editor.putString(folder.getFolderName(), json);
            editor.commit();
            return true;
        }catch (Exception e){
            Log.d(TAG, "saveMedia: " + e.getMessage());
            return false;
        }
    }

    public void loadMedia(){
        Set<String> keys = sharedPreferences.getAll().keySet();
        Gson gson = new Gson();
        mList.clear();
        for(String key : keys) {
            String json = sharedPreferences.getString(key, "default");
            if (json.equals("default")){
                Log.d(TAG, "LoadMedia: key pair not found! key: " + key);
            }else {
                Folder folder = gson.fromJson(json, Folder.class);
                mList.add(folder);
            }
        }
        MainAdapter adapter = new MainAdapter(MainActivity.this, mList);
        mRecycler.setAdapter(adapter);

    }

    public static Folder loadFolder(String folderSP) {
        Set<String> keys = sharedPreferences.getAll().keySet();
        Gson gson = new Gson();
        for(String key : keys) {
            String json = sharedPreferences.getString(key, "default");
            if (json.equals("default")){
                Log.d(TAG, "LoadMedia: key pair not found! key: " + key);
            }else {
                Folder folder = gson.fromJson(json, Folder.class);
                if (folder.getFolderName().equals(folderSP)){
                    return folder;
                }
            }
        }
        return null;
    }

    private void requestSignIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(MainActivity.this, signInOptions);

        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Log.d(TAG, "onActivityResult: SigIN");

                }
                handleSignInResult(resultData);
                break;
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent resultData) {
        GoogleSignIn.getSignedInAccountFromIntent(resultData)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "onSuccess: Current Account: " + googleSignInAccount.getEmail());
                        mEmail.setText(googleSignInAccount.getEmail());
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Drive googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),credential).setApplicationName("Drive API Migration").build();
                        mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "handleSignInResult: onFailure: Exception: "  + e.getMessage());
                    }
                });
    }


    //Used for back-round synchronization
    public void checkSync(){
        Log.d(TAG, "checkSync: Init");
        Set<String> keys = sharedPreferences.getAll().keySet();
        Gson gson = new Gson();
        for(String key : keys) {
            String json = sharedPreferences.getString(key, "default");
            if (json.equals("default")){
                Log.d(TAG, "LoadMedia: key pair not found! key: " + key);
            }else {
                boolean updateFound = false;
                Folder folder = gson.fromJson(json, Folder.class);
                String days[] = {"Monday" , "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                String todayDay = getDay();
                if (folder.getSyncable()){
                    if (!todayDay.equals(folder.getLastSync())){
                        for (int i=0; i<days.length; i++){
                            if (todayDay.equals(days[i]) && folder.getDays()[i]){
                                if (hasPassed(folder.getTime(), getCurrentTime())){
                                    Log.d(TAG, "checkSync: sync started");
                                    Log.d(TAG, "checkSync: Today: " + todayDay + " Day: " + folder.getDays()[i]);
                                    if (folder.getLocalAddress() != null){
                                        Log.d(TAG, "checkSync: Folder found!: " + folder.getLocalAddress());
                                        SyncFolders syncFolders = new SyncFolders(folder.getLocalAddress());
                                        syncFolders.execute();
                                        MyWorker.FilepathSet(folder.getLocalAddress());
                                        folder.setLastSync(todayDay);
                                        saveMedia(folder);
                                        updateFound = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (updateFound){
                    scrollTextUpdate("Please wait, syncing files\n");
                }
            }
        }
    }

    // 2nd CheckSync used when app is in on state

   /* public void checkSync_2(){
        Log.d(TAG, "checkSync: Init");
        Set<String> keys = sharedPreferences.getAll().keySet();
        Gson gson = new Gson();
        for(String key : keys) {
            String json = sharedPreferences.getString(key, "default");
            if (json.equals("default")){
                Log.d(TAG, "LoadMedia: key pair not found! key: " + key);
            }else {
                boolean updateFound = false;
                Folder folder = gson.fromJson(json, Folder.class);
                String days[] = {"Monday" , "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                String todayDay = getDay();
                if (folder.getSyncable()){
                    if (!todayDay.equals(folder.getLastSync())){
                        for (int i=0; i<days.length; i++){
                            if (todayDay.equals(days[i]) && folder.getDays()[i]){
                                if (hasPassed(folder.getTime(), getCurrentTime())){
                                    Log.d(TAG, "checkSync: sync started");
                                    Log.d(TAG, "checkSync: Today: " + todayDay + " Day: " + folder.getDays()[i]);
                                    if (folder.getLocalAddress() != null){
                                        Log.d(TAG, "checkSync: Folder found!: " + folder.getLocalAddress());
                                        SyncFolders syncFolders = new SyncFolders(folder.getLocalAddress());
                                        syncFolders.execute();
                                        //MyWorker.FilepathSet(folder.getLocalAddress());
                                        folder.setLastSync(todayDay);
                                        saveMedia(folder);
                                        updateFound = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (updateFound){
                    scrollTextUpdate("Please wait, syncing files\n");
                }
            }
        }
    }

    */

    private String getCurrentTime() {
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date current = new Date(System.currentTimeMillis());
        String currTime = sdf.format(current);
        Log.d(TAG, "getCurrentTime: " + currTime);
        return currTime;
    }

    private boolean hasPassed(String baseTime, String currTime) {
        try {
            String format = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date curr = sdf.parse(currTime);
            Date base = sdf.parse(baseTime);
            Log.d(TAG, "hasPassed: Curr: " + curr.toString() + " base: " + base.toString());
            if (curr.getTime() - base.getTime() >= 0)	return true;
        }catch (Exception e){
            Log.d(TAG, "hasPassed: parseException: " + e.getMessage());
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private String getDay() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
    }

    public void initSync(String localPath) {
        Log.d(TAG, "initSync: Init");
        mDriveServiceHelper.queryGlobal("myDrive")
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String rootID) {
                        if (rootID != null){
                            Log.d(TAG, "onSuccess: Root folder found");
                            syncFolder(rootID, localPath);
                        }else {
                            Log.d(TAG, "onSuccess: Root folder not found, Creating folder");
                            mDriveServiceHelper.insertFolder(null, "myDrive", "rootFolder")
                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String rootID) {
                                            syncFolder(rootID, localPath);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: Insert Folder: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Query Global: " + e.getMessage());
                    }
                });
    }

    public void syncFolder(String parentID, String localPath) {
        Log.d(TAG, "syncFolder: Init for: " + localPath);
        mDriveServiceHelper.queryFiles(parentID)
                .addOnSuccessListener(new OnSuccessListener<List<GoogleDriveFileHolder>>() {
                    @Override
                    public void onSuccess(List<GoogleDriveFileHolder> googleDriveFileHolders) {
                        File directory = new File(localPath);
                        String holderId = null;
                        boolean folderExist = false;
                        for (GoogleDriveFileHolder driveFileHolder : googleDriveFileHolders) {
                            if (directory.getName().equals(driveFileHolder.getName())){
                                folderExist= true;
                                holderId = driveFileHolder.getId();
                                break;
                            }
                        }
                        if (!folderExist){
                            scrollTextUpdate("onSuccess: " + directory.getName() + " Does not exit, Creating...\n");
                            Log.d(TAG, "onSuccess: " + directory.getName() + " Does not exit, Creating...");
                            mDriveServiceHelper.insertFolder(parentID, directory.getName(), directory.getPath())
                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String Id) {
                                            scrollTextUpdate("onSuccess: " + directory.getName() + " Created\n");
                                            Log.d(TAG, "onSuccess: " + directory.getName() + " Created");
                                            File listFiles[] = directory.listFiles();
                                            if (listFiles != null && listFiles.length > 0) {
                                                for (int i = 0; i < listFiles.length; i++) {
                                                    File file = listFiles[i];
                                                    if (file.isDirectory() && !file.isHidden()){
                                                        syncFolder(Id, file.getPath());
                                                    }else if (file.isFile() && !file.isHidden()) {
                                                        scrollTextUpdate("Creating file: if\n");
                                                        syncFile(Id, file);
                                                    }
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: Inset Folder: " + e.getMessage());
                                        }
                                    });
                        }else {
                            scrollTextUpdate("onSuccess: " + directory.getName() + " Already exit\n");
                            Log.d(TAG, "onSuccess: " + directory.getName() + " Already exit");
                            File listFiles[] = directory.listFiles();
                            if (listFiles != null && listFiles.length > 0) {
                                for (int i = 0; i < listFiles.length; i++) {
                                    File file = listFiles[i];
                                    if (file.isDirectory() && !file.isHidden()) {
                                        syncFolder(holderId, file.getPath());
                                    }
                                }
                            }
                            if (holderId != null){
                                String finalHolderId = holderId;
                                mDriveServiceHelper.queryFiles(holderId)
                                        .addOnSuccessListener(new OnSuccessListener<List<GoogleDriveFileHolder>>() {
                                            @Override
                                            public void onSuccess(List<GoogleDriveFileHolder> googleDriveFileHolders) {
                                                File listFiles[] = directory.listFiles();
                                                if (listFiles != null && listFiles.length > 0) {
                                                    for (int i = 0; i < listFiles.length; i++) {
                                                        File file = listFiles[i];
                                                        if (file.isFile() && !file.isHidden()) {
                                                            boolean isFileExist = false;
                                                            for (GoogleDriveFileHolder driveFileHolder : googleDriveFileHolders){
                                                                if (file.getName().equals(driveFileHolder.getName())){
                                                                    isFileExist = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (!isFileExist){
                                                                scrollTextUpdate("onSuccess: " + file.getName() + " Creating...\n");
                                                                syncFile(finalHolderId, file);
                                                            }
                                                        }
                                                    }
                                                }


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: Query FIles: " + e.getMessage());
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Exception: " + e.getMessage());
                    }
                });

    }

    private void syncFile(String parentID, File file) {
        String extn = getFileExtension(file);
        String mimeType;
        if (extn.equals(".txt")){ mimeType = EXPORT_TYPE_PLAIN_TEXT; }
        else if (extn.equals(".html")) { mimeType = EXPORT_TYPE_HTML; }
        else if (extn.equals(".zip")) { mimeType = EXPORT_TYPE_HTML_ZIPPED; }
        else if (extn.equals(".pdf")) { mimeType = EXPORT_TYPE_PDF; }
        else if (extn.equals(".csv")) { mimeType = EXPORT_TYPE_CSV; }
        else if (extn.equals(".png")) { mimeType = EXPORT_TYPE_PNG; }
        else if (extn.equals(".json")) { mimeType = EXPORT_TYPE_JSON; }
        else if (extn.equals(".jpeg")) { mimeType = TYPE_PHOTO; }
        else if (extn.equals(".jpg")) { mimeType = TYPE_PHOTO; }
        else if (extn.equals(".mp3")) { mimeType = TYPE_AUDIO; }
        else if (extn.equals(".mp4")) { mimeType = TYPE_VIDEO; }
        else { mimeType = TYPE_UNKNOWN; }
        mDriveServiceHelper.insertFile(parentID,mimeType,file.getName(), file.getPath())
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        scrollTextUpdate("onSuccess: " + file.getName() + " Created\n");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: File Inset: " + e.getMessage());
                    }
                });
    }

    public static void deleteFolder(String folderName){
        editor.remove(folderName);
        editor.apply();
    }

    public String getFileExtension(File file){
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1){
            return "";
        }
        return name.substring(lastIndexOf);
    }

    private class SyncFolders extends AsyncTask<Void, Void, String>{

        String fileLocation;

        public SyncFolders(String fileLocation) {
            this.fileLocation = fileLocation;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Init");
            try {
                initSync(fileLocation);
            }catch (Exception e){
                Log.d(TAG, "doInBackground: Init Sync: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: Init");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: Init");
        }
    }

    private static void scrollTextUpdate(String msg){
        mResult.post(new Runnable() {
            @Override
            public void run() {
                mResult.append(msg);
            }
        });
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }else {
            return false;
        }
    }

}