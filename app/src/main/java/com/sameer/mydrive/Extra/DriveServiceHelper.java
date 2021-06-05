package com.sameer.mydrive.Extra;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.sameer.mydrive.Object.GoogleDriveFileHolder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private static final String TAG = "DriveServiceHelper";
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }

    public Task<String> insertFolder(String parentID, String fileName, String filePath) {
        return Tasks.call(mExecutor, () -> {
            File fileMetadata = new File()
                    .setName(fileName)
                    .setMimeType("application/vnd.google-apps.folder")
                    .setDescription(filePath);
            if (parentID != null){
                fileMetadata.setParents(Collections.singletonList(parentID));
            }
            File file = mDriveService.files().create(fileMetadata).execute();
            return file.getId();
        });
    }

    public Task<String> insertFile(String parentID, String mimeType, String fileName, String filePath){
        return Tasks.call(mExecutor, () -> {
            List<String> parent;
            if (parentID == null) { parent = Collections.singletonList("myDrive"); }
            else { parent = Collections.singletonList(parentID); }
            File body = new File()
                    .setName(fileName)
                    .setMimeType(mimeType)
                    .setDescription(filePath)
                    .setParents(parent);
            java.io.File fileContent = new java.io.File(filePath);
            FileContent mediaContent = new FileContent(mimeType, fileContent);
            File file = mDriveService.files().create(body, mediaContent).execute();
            Log.d(TAG, "insertFile: FileID: " + file.getId());
            return file.getId();
        });
    }

    public Task<List<GoogleDriveFileHolder>> queryFiles(String parentID){
        return Tasks.call(mExecutor, () -> {
            List<GoogleDriveFileHolder> mGoogleDriveFileHolderList = new ArrayList<>();
            String parent = "myDrive";
            if (parentID != null) {
                parent = parentID;
            }
            FileList result = mDriveService.files().list().setQ("'" + parent + "' in parents").setFields("files(id, name,size,createdTime,modifiedTime,starred,mimeType,description)").setSpaces("drive").execute();
            for (int i=0; i<result.getFiles().size(); i++){
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                googleDriveFileHolder.setId(result.getFiles().get(i).getId());
                googleDriveFileHolder.setName(result.getFiles().get(i).getName());
                if (result.getFiles().get(i).getSize() != null) {
                    googleDriveFileHolder.setSize(result.getFiles().get(i).getSize());
                }

                if (result.getFiles().get(i).getModifiedTime() != null) {
                    googleDriveFileHolder.setModifiedTime(result.getFiles().get(i).getModifiedTime());
                }

                if (result.getFiles().get(i).getCreatedTime() != null) {
                    googleDriveFileHolder.setCreatedTime(result.getFiles().get(i).getCreatedTime());
                }

                if (result.getFiles().get(i).getStarred() != null) {
                    googleDriveFileHolder.setStarred(result.getFiles().get(i).getStarred());
                }

                if (result.getFiles().get(i).getMimeType() != null) {
                    googleDriveFileHolder.setMimeType(result.getFiles().get(i).getMimeType());
                }
                if (result.getFiles().get(i).getDescription() != null) {
                    googleDriveFileHolder.setDescriptioon(result.getFiles().get(i).getDescription());
                }
                mGoogleDriveFileHolderList.add(googleDriveFileHolder);
            }
            return mGoogleDriveFileHolderList;
        });
    }

    public Task<String> queryGlobal(String folderName) {
        return Tasks.call(mExecutor, () -> {
            Log.d(TAG, "isFolderExist: mDriveService: \n" + mDriveService.files().toString());
            Log.d(TAG, "isFolderExist: mDriveService: \n" + mDriveService.files().list().toString());
            FileList result = mDriveService.files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' and name='" + folderName + "' and trashed=false")
                    .setSpaces("drive")
                    .execute();
            if (result.getFiles().size() > 0) {
                return result.getFiles().get(0).getId();
            }
            return null;
        });
    }
}
