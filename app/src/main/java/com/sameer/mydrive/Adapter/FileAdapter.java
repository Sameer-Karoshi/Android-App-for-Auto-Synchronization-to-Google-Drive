package com.sameer.mydrive.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.sameer.mydrive.Extra.EditSyncActivity;
import com.sameer.mydrive.Extra.ExploreActivity;
import com.sameer.mydrive.R;
import com.sameer.mydrive.Temp.TempActivity;
import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> implements PopupMenu.OnMenuItemClickListener{

    private static final String TAG = "FileAdapter";

    private Context context;
    private List<File> mList;
    int count = 0;

    public FileAdapter(Context context, List<File> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public FileAdapter.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_view, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapter.FileViewHolder holder, final int position) {
        final File file = mList.get(position);
        holder.mName.setText(file.getName());

        String details = "null";
        if (file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0){
            details = file.listFiles().length + " Items";
//            holder.mMore.setVisibility(View.INVISIBLE);
        }else if (!file.isDirectory()){
            details = String.valueOf(file.length()/1000000.0) + " MB";
            holder.mMore.setVisibility(View.INVISIBLE);
        }
        holder.mDetails.setText(details);

        String imgType = file.isDirectory() ? "ic_folder" : "ic_file";
        String uri = "drawable/" + imgType;
        int imgResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        holder.mType.setImageDrawable(context.getResources().getDrawable(imgResource));

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file.isDirectory()){
                    context.startActivity(new Intent(context, ExploreActivity.class).putExtra("filePath", file.getPath()));
                }else {
                    Toast.makeText(context, "Not a Directory!\n" + file.getPath(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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



    public class FileViewHolder extends RecyclerView.ViewHolder {

        private TextView mName, mDetails, mMore;
        private LinearLayout mLayout;
        private ImageView mType;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.fv_name);
            mDetails = itemView.findViewById(R.id.fv_details);
            mMore = itemView.findViewById(R.id.fv_more);
            mLayout = itemView.findViewById(R.id.fv_layout);
            mType = itemView.findViewById(R.id.fv_image);
        }
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.popup_select:
                selectMedia();
//                copyPath();
                return true;
            default:
                return false;
        }
    }

    private void selectMedia() {
        context.startActivity(new Intent(context, EditSyncActivity.class).putExtra("path", mList.get(count).getPath()));
    }
    private void copyPath(){
        context.startActivity(new Intent(context, TempActivity.class).putExtra("path", mList.get(count).getPath()));
    }
}
