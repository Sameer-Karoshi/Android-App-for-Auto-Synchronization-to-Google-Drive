package com.sameer.mydrive.Temp;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private static final String TAG = "DriveServiceHelper";
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;


    public static String TYPE_AUDIO = "application/vnd.google-apps.audio";
    public static String TYPE_GOOGLE_DOCS = "application/vnd.google-apps.document";
    public static String TYPE_GOOGLE_DRAWING = "application/vnd.google-apps.drawing";
    public static String TYPE_GOOGLE_DRIVE_FILE = "application/vnd.google-apps.file";
//    public static String TYPE_GOOGLE_DRIVE_FOLDER = DriveFolder.MIME_TYPE;
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

    public DriveServiceHelper(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }

    public Task<String> createFile() {
        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList("root"))
                    .setMimeType("text/plain")
                    .setName("Untitled file");

            File googleFile = mDriveService.files().create(metadata).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }
            return googleFile.getId();
        });
    }

    public Task<String> createFilePDF(String filepath) {
        return Tasks.call(mExecutor, () -> {
            java.io.File file = new java.io.File(filepath);
            File fileMetadata = new File()
                    .setName(file.getName());
            FileContent mediaContent = new FileContent("application/pdf", file);
            File myFile = null;
            try {
                myFile = mDriveService.files().create(fileMetadata, mediaContent).execute();
            }catch (Exception e){
                Log.d(TAG, "createFilePDF: " + e.getMessage());
            }
            if (myFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }
            return myFile.getId();
        });
    }

    public Task<String> createFolder(String parentID, String name) {
        return Tasks.call(mExecutor, () -> {
            List<String> parent;
            if (parentID == null) { parent = Collections.singletonList("myRootDrive"); }
            else { parent = Collections.singletonList(parentID); }
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setParents(parent);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            File file = mDriveService.files().create(fileMetadata).execute();
            return file.getId();
        });
    }

    public Task<String> isFolderExist(String folderName) {
        return Tasks.call(mExecutor, () -> {
            Log.d(TAG, "isFolderExist: mDriveService: \n" + mDriveService.files().toString());
            Log.d(TAG, "isFolderExist: mDriveService: \n" + mDriveService.files().list().toString());
            FileList result = mDriveService.files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' name='" + folderName + "' and trashed=false")
                    .setSpaces("drive")
                    .execute();
            if (result.getFiles().size() > 0) {
                return result.getFiles().get(0).getId();
            }
            return null;
        });
    }

    public Task<String> insertFile(String parentID, String mimeType, String fileName, String filePath){
        return Tasks.call(mExecutor, () -> {
            List<String> parent;
            if (parentID == null) { parent = Collections.singletonList("myRootDrive"); }
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
            String parent = "root";
            if (parentID != null) {
                parent = parentID;
            }
            FileList result = mDriveService.files().list().setQ("'" + parent + "' in parents and trashed=false").setFields("files(id, name,size,createdTime,modifiedTime,starred,mimeType,description)").setSpaces("drive").execute();
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

}
