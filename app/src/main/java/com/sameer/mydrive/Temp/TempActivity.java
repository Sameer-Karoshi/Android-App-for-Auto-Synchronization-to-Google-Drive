package com.sameer.mydrive.Temp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.sameer.mydrive.R;

import java.util.Collections;
import java.util.List;

public class TempActivity extends AppCompatActivity {

    private static final String TAG = "TempActivity";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;
    private DriveServiceHelper mDriveServiceHelper;

    private EditText mEdit;
    private Button mUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        mEdit = findViewById(R.id.at_edit);
        mUpload = findViewById(R.id.at_upload);
//        String path = getIntent().getStringExtra("path");
//        mEdit.setText(path);
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadPdfFile(mEdit.getText().toString().trim());
//                createFolder();
//                querys();
                myServices();
            }
        });
        requestSignIn();
    }

    private void myServices() {

    }

    private void querys(String ID) {
        mDriveServiceHelper.queryFiles(ID)
                .addOnSuccessListener(new OnSuccessListener<List<GoogleDriveFileHolder>>() {
                    @Override
                    public void onSuccess(List<GoogleDriveFileHolder> googleDriveFileHolders) {
                        Log.d(TAG, "onSuccess: Size: " + googleDriveFileHolders.size());
                        for (GoogleDriveFileHolder driveFileHolder : googleDriveFileHolders) {
                            Log.d(TAG, "onSuccess: " +
                                    "Name: " + driveFileHolder.getName() +
                                    " MimeType: " + driveFileHolder.getMimeType());

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

    private void requestSignIn() {
        Log.d(TAG, "requestSignIn: Init");
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(TempActivity.this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        Log.d(TAG, "onActivityResult: ********** Init: requestCode: " + requestCode);
        Log.d(TAG, "onActivityResult: " + String.valueOf(resultCode == Activity.RESULT_OK) + " " + String.valueOf(resultData != null));
        Log.d(TAG, "onActivityResult: resultCode: " + resultCode);
        Log.d(TAG, "onActivityResult: resultOK: " + Activity.RESULT_OK);

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Log.d(TAG, "onActivityResult: SigIN");

                }
                handleSignInResult(resultData);
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri != null) {
                        openFileFromFilePicker(uri);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent resultData) {
        Log.d(TAG, "handleSignInResult: Init");
        GoogleSignIn.getSignedInAccountFromIntent(resultData)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "onSuccess: Current Account: " + googleSignInAccount.getEmail());
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(TempActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
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

    private void openFileFromFilePicker(Uri uri) {
        Log.d(TAG, "openFileFromFilePicker: " + uri);
    }

    private void uploadPdfFile(String path){
        ProgressDialog progressDialog = new ProgressDialog(TempActivity.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mDriveServiceHelper.createFilePDF(path)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(TempActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(TempActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createFolder(){
        ProgressDialog progressDialog = new ProgressDialog(TempActivity.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mDriveServiceHelper.isFolderExist("myDrive")
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s != null){
                            querys(s);
//                            mDriveServiceHelper.insertFile(s, "text/plain", "myFile_2.txt", mEdit.getText().toString().trim())
//                                    .addOnSuccessListener(new OnSuccessListener<String>() {
//                                        @Override
//                                        public void onSuccess(String s) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(TempActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, "onSuccess: FileDI: " + s);
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "onFailure: Exception: " + e.getMessage());
//                                }
//                            });

//                            mDriveServiceHelper.createFolder(s, "myRooty")
//                                    .addOnSuccessListener(new OnSuccessListener<String>() {
//                                        @Override
//                                        public void onSuccess(String s) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(TempActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, "onSuccess: FileDI: " + s);
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "onFailure: Exception: " + e.getMessage());
//                                }
//                            });
                        }
                        else Log.d(TAG, "onSuccess: File Does Not Exist");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Exception: " + e.getMessage());
                    }
                });
//        mDriveServiceHelper.isFolderExist("myDrive")
//                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean aBoolean) {
//                        if (!aBoolean){
//                            mDriveServiceHelper.createFolder("myDrive")
//                                    .addOnSuccessListener(new OnSuccessListener<String>() {
//                                        @Override
//                                        public void onSuccess(String s) {
//                                            progressDialog.dismiss();
//                                            Log.d(TAG, "onSuccess: FileID: " + s);
//                                            Toast.makeText(TempActivity.this, "Folder Created: " + s, Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            progressDialog.dismiss();
//                                            Log.d(TAG, "onFailure: createFolder: " + e.getMessage());
//                                            Toast.makeText(TempActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }else {
//                            progressDialog.dismiss();
//                            Toast.makeText(TempActivity.this, "File already Exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Log.d(TAG, "onFailure: isFolderExist: " + e.getMessage());
//                        Toast.makeText(TempActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
        }



    @Override
    protected void onStart() {
        super.onStart();

    }


}
