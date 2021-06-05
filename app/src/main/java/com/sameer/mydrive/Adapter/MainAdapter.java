// This adapter is for main page where we can see all folders that will be synced(Last Sync is also visible)
package com.sameer.mydrive.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.sameer.mydrive.Extra.EditSyncActivity;
import com.sameer.mydrive.Extra.MainActivity;
import com.sameer.mydrive.Object.Folder;
import com.sameer.mydrive.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> implements PopupMenu.OnMenuItemClickListener{

    private Context mContext;
    private List<Folder> mList;
    int count = 0;

    private static final String TAG = "MainAdapter";



    public MainAdapter(Context mContext, List<Folder> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.folder_view, parent, false);
        return new MainViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MainViewHolder holder, final int position) {
        Folder folder = mList.get(position);
        holder.mName.setText(folder.getFolderName());
        holder.mLast.setText("Last sync: " + folder.getLastSync());
        if (folder.getStatus() == 0){
            holder.mStatus.setText("Status: Next sync: Monday at 14:34");
            holder.mProgress.setVisibility(View.INVISIBLE);
        }
        else if (folder.getStatus() == 1){
            holder.mProgress.setVisibility(View.VISIBLE);
            holder.mStatus.setText("Status: Syncing...");
        }
        else if (folder.getStatus() == -1){
            holder.mStatus.setText("Status: Sync is Disabled");
            holder.mProgress.setVisibility(View.INVISIBLE);
        }

        String imgType = folder.getSyncable() ? "ic_cloud" : "ic_cloud_off";
        String uri = "drawable/" + imgType;
        int imgResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
        holder.mImage.setImageDrawable(mContext.getResources().getDrawable(imgResource));

        if (folder.getLastSync().trim().equals("null")){
            holder.mLast.setText("Last sync: none");
        }

        if (!folder.getSyncable()){
            holder.mLast.setText("Sync disabled");
        }

        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = position;
                showPopupMenu(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    public class MainViewHolder extends RecyclerView.ViewHolder {
        private TextView mName, mMore, mLast, mStatus;
        private ProgressBar mProgress;
        private ImageView mImage;
        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.fldrv_name);
            mLast = itemView.findViewById(R.id.fldrv_last);
            mMore = itemView.findViewById(R.id.fldrv_more);
            mStatus = itemView.findViewById(R.id.fldrv_status);
            mProgress = itemView.findViewById(R.id.fldrv_progress);
            mImage = itemView.findViewById(R.id.fldrv_img);
        }
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.popup_main_menu);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.popup_main_edit:
                Edit();
                return true;
            case R.id.popup_main_delete:
                Delete();
                return true;
            default:
                return false;
        }
    }



    private String getCurrentDate() {
        LocalDateTime myDateObject = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            myDateObject = LocalDateTime.now();
            DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            return myDateObject.format(myFormat);
        }
        return "ND";
    }

    private void Delete() {
        MainActivity.deleteFolder(mList.get(count).getFolderName());
        notifyItemRemoved(count);
    }

    private void Edit() {
        mContext.startActivity(new Intent(mContext, EditSyncActivity.class).putExtra("path", mList.get(count).getLocalAddress()).putExtra("folder", mList.get(count).getFolderName()));
    }

//    public  void mySync(String filePath){
//        Log.d(TAG, "mySync: mySync: Init");
//        syncFolderTask syncFolderTask = new syncFolderTask();
//        syncFolderTask.execute(filePath);
//    }
//
//    public class syncFolderTask extends AsyncTask<String, Integer, Void> {
//        ProgressDialog progressDialog;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.d(TAG, "onPreExecute: Init");
//            progressDialog = new ProgressDialog(mContext);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.setMessage("Please wait...");
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Log.d(TAG, "onPostExecute: Init");
//            progressDialog.dismiss();
//        }
//
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            Log.d(TAG, "doInBackground: Init");
////            MainActivity.initSync(strings[0]);
//            return null;
//        }
//    }
}
